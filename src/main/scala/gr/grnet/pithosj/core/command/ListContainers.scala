/*
 * Copyright 2012-2013 GRNET S.A. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 *   1. Redistributions of source code must retain the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials
 *      provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY GRNET S.A. ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GRNET S.A OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and
 * documentation are those of the authors and should not be
 * interpreted as representing official policies, either expressed
 * or implied, of GRNET S.A.
 */

package gr.grnet.pithosj.core.command

import gr.grnet.pithosj.core.ConnectionInfo
import gr.grnet.pithosj.core.command.result.{ContainerResultData, Result}
import gr.grnet.pithosj.core.date.DateParsers
import gr.grnet.pithosj.core.http.{ResponseFormats, Method}
import gr.grnet.pithosj.core.keymap.{ResultKeys, RequestParamKeys, KeyMap}
import scala.xml.XML

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
case class ListContainers(connectionInfo: ConnectionInfo) extends CommandSkeleton {
  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod = Method.GET

  /**
   * The HTTP query parameters that are set by this command.
   */
  override val queryParameters = {
    newQueryParameters.
      set(RequestParamKeys.Format, ResponseFormats.XML.responseFormat())
  }

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  def successCodes = Set(200)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverURLPathElements = Seq(connectionInfo.userID)

  /**
   * The keys for extra result data pertaining to this command.
   * Normally, the data that the keys refer to will be parsed
   * from the `HTTP` response body (`XML` or `JSON`).
   */
  override val resultDataKeys = Seq(
    ResultKeys.ListContainers
  )

  override def buildResult(
      responseHeaders: KeyMap,
      statusCode: Int,
      statusText: String,
      startMillis: Long,
      stopMillis: Long,
      getResponseBody: () ⇒ String
  ) = {

    val resultData = KeyMap(responseHeaders)

    if(successCodes(statusCode)) {
      val body = getResponseBody()
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

        val policy = KeyMap()
        for((k, v) <- keys.zip(values)) {
          k.toLowerCase match {
            case ResultKeys.ContainerQuota.name ⇒
              policy.set(ResultKeys.ContainerQuota, v.toLong)
            case k ⇒
              policy.setString(k, v)
          }
        }

        val containerResultData = ContainerResultData(
          name.text,
          count.text.toInt,
          DateParsers.parse(last_modified.text, DateParsers.Format1Parser),
          bytes.text.toLong,
          policy
        )

        containerResultData
      }

      resultData.set(ResultKeys.ListContainers, containerResults.toList)
    }

    Result(
      descriptor,
      statusCode,
      statusText,
      startMillis,
      stopMillis,
      resultData
    )
  }
}