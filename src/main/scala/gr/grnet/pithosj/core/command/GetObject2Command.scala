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
import gr.grnet.pithosj.core.keymap.{PithosHeaderKeys, PithosRequestParamKeys, PithosResultKeys}

case class GetObject2Command(
  serviceInfo: ServiceInfo,
  container: String,
  path: String,
  version: String
) extends PithosCommandSkeleton[GetObject2ResultData] {
  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod = Get

  /**
   * The HTTP query parameters that are set by this command.
   */
  override def queryParameters =
    version match {
      case null | "" ⇒ Map()
      case _ ⇒         Map(PithosRequestParamKeys.Version.name → version)
    }

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  def successStatuses = Set(200).map(Status.fromCode)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverRootPathElements =
    if(container.isEmpty) Seq(serviceInfo.rootPath, serviceInfo.uuid, path)
    else                  Seq(serviceInfo.rootPath, serviceInfo.uuid, container, path)

  /**
   * Type-safe keys for `HTTP` response headers that are specific to this command.
   * These usually correspond to Pithos-specific headers, not general-purpose
   * `HTTP` response headers but there may be exceptions.
   *
   * Each command must document which keys it supports.
   */
  override val responseHeaderKeys = Seq(
    PithosHeaderKeys.Standard.ETag,
    PithosHeaderKeys.Standard.Content_Type,
    PithosHeaderKeys.Standard.Content_Length,
    PithosHeaderKeys.Standard.Last_Modified,
    PithosHeaderKeys.Pithos.X_Object_Hash,
    PithosHeaderKeys.Pithos.X_Object_Modified_By,
    PithosHeaderKeys.Pithos.X_Object_Version_Timestamp,
    PithosHeaderKeys.Pithos.X_Object_UUID,
    PithosHeaderKeys.Pithos.X_Object_Version
  )

  override val resultDataKeys = Seq(
    PithosResultKeys.Commands.Container,
    PithosResultKeys.Commands.Path
  )

  def buildResultData(response: Response, startMillis: Long, stopMillis: Long): GetObject2ResultData = {
    val responseHeaders = response.headerMap
    GetObject2ResultData(
      objBuf = response.content,
      container = container,
      path = path,
      ETag = responseHeaders.get(PithosHeaderKeys.Standard.ETag.name),
      Content_Type = responseHeaders.get(PithosHeaderKeys.Standard.Content_Type.name),
      Content_Length = responseHeaders.get(PithosHeaderKeys.Standard.Content_Length.name).map(_.toLong),
      Last_Modified = responseHeaders.get(PithosHeaderKeys.Standard.Last_Modified.name).map(DateParsers.parse(_, DateParsers.Format2Parser)),
      X_Object_Hash = responseHeaders.get(PithosHeaderKeys.Pithos.X_Object_Hash.name),
      X_Object_Modified_By = responseHeaders.get(PithosHeaderKeys.Pithos.X_Object_Modified_By.name),
      X_Object_Version_Timestamp = responseHeaders.get(PithosHeaderKeys.Pithos.X_Object_Version_Timestamp.name).map(DateParsers.parse(_, DateParsers.Format2Parser)),
      X_Object_UUID = responseHeaders.get(PithosHeaderKeys.Pithos.X_Object_UUID.name),
      X_Object_Version = responseHeaders.get(PithosHeaderKeys.Pithos.X_Object_Version.name)
    )
  }
}
