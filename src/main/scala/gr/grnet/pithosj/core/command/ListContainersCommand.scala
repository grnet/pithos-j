/*
 * Copyright (C) 2010-2014 GRNET S.A.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gr.grnet.pithosj.core.command

import com.twitter.finagle.httpx.Method.Get
import com.twitter.finagle.httpx.{Response, Status}
import gr.grnet.common.date.DateParsers
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.command.result.ContainerData
import gr.grnet.pithosj.core.http.ResponseFormats
import gr.grnet.pithosj.core.keymap.{PithosRequestParamKeys, PithosResultKeys}
import typedkey.Key
import typedkey.env.MEnv

import scala.xml.XML

case class ListContainersCommand(
  serviceInfo: ServiceInfo
) extends PithosCommandSkeleton[ListContainersResultData] {
  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod = Get

  /**
   * The HTTP query parameters that are set by this command.
   */
  override def queryParameters =
    Map(PithosRequestParamKeys.Format.name → ResponseFormats.XML.responseFormat())

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  def successStatuses = Set(200).map(Status.fromCode)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverRootPathElements = Seq(serviceInfo.rootPath, serviceInfo.uuid)

  /**
   * The keys for extra result data pertaining to this command.
   * Normally, the data that the keys refer to will be parsed
   * from the `HTTP` response body (`XML` or `JSON`).
   */
  override val resultDataKeys = Seq(
    PithosResultKeys.ListContainers
  )

  def buildResultData(response: Response, startMillis: Long, stopMillis: Long): ListContainersResultData = {
    val body = response.contentString
    val xml = XML.loadString(body)

    val containerResults = for {
      container <- xml \ "container"
      count <- container \ "count"
      last_modified <- container \ "last_modified"
      bytes <- container \ "bytes"
      name <- container \ "name"
      x_container_policy <- container \ "x_container_policy"
    } yield {
      // parse:
      //  <x_container_policy>
      //    <key>quota</key>
      //    <value>53687091200</value>
      //    <key>versioning</key>
      //
      //    <value>auto</value>
      //  </x_container_policy>
      val kvPairs = for {
        child <- x_container_policy.nonEmptyChildren if Set("key", "value").contains(child.label.toLowerCase)
      } yield {
        child.text
      }

      // A key is at an even index, a value is at an odd index
      val (keys_i, values_i) = kvPairs.zipWithIndex.partition {
        case (s, index) => index % 2 == 0
      }
      val keys = keys_i.map(_._1) // throw away the index
      val values = values_i.map(_._1)

      val policy = MEnv()
      for((k, v) <- keys.zip(values)) {
        k.toLowerCase match {
          case PithosResultKeys.ContainerQuota.name ⇒
            policy.update(PithosResultKeys.ContainerQuota, v.toLong)

          case _ ⇒
            // All other keys are stored as String keys
            policy.update(Key[String](k), v)
        }
      }

      val containerResultData = ContainerData(
        name.text,
        count.text.toInt,
        DateParsers.parse(last_modified.text, DateParsers.Format1Parser),
        bytes.text.toLong,
        policy.toImmutable
      )

      containerResultData
    }

    ListContainersResultData(
      containers = containerResults
    )
  }
}
