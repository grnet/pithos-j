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

import java.io.OutputStream

import com.ning.http.client.AsyncHandler.STATE
import gr.grnet.common.date.DateParsers
import gr.grnet.common.http.Method
import gr.grnet.common.keymap.KeyMap
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.keymap.{PithosHeaderKeys, PithosRequestParamKeys, PithosResultKeys}

case class GetObjectCommand(
  serviceInfo: ServiceInfo,
  container: String,
  path: String,
  version: String,
  out: OutputStream
) extends PithosCommandSkeleton[GetObjectResultData] {
  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod = Method.GET

  /**
   * The HTTP query parameters that are set by this command.
   */
  override val queryParameters = {
    version match {
      case null ⇒
        newQueryParameters

      case version ⇒
        newQueryParameters.set(PithosRequestParamKeys.Version, version)
    }
  }

  override def onBodyPartReceivedOpt = {
    Some(
      httpResponseBodyPart ⇒ {
        httpResponseBodyPart.writeTo(out)
        STATE.CONTINUE
      }
    )
  }

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  def successCodes = Set(200)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverURLPathElements = Seq(serviceInfo.uuid, container, path)

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

  /**
   * Parse a response header that is specific to this command and whose value must be of non-String type.
   *
   * Returns `true` iff the header is parsed.
   *
   * The parsed [[gr.grnet.common.keymap.HeaderKey]]
   * and its associated non-String value are recorded in the provided `keyMap`.
   */
  override protected def tryParseNonStringResponseHeader(
      keyMap: KeyMap,
      name: String,
      value: String
  ) = {
    name match {
      case PithosHeaderKeys.Standard.Last_Modified.name ⇒
        // Wed, 19 Sep 2012 08:18:23 GMT
        val parsedDate = DateParsers.parse(value, DateParsers.Format2Parser)
        keyMap.set(
          PithosHeaderKeys.Standard.Last_Modified,
          parsedDate)
        true

      case PithosHeaderKeys.Pithos.X_Object_Version_Timestamp.name ⇒
        // Wed, 19 Sep 2012 08:18:23 GMT
        val parsedDate = DateParsers.parse(value, DateParsers.Format2Parser)
        keyMap.set(
          PithosHeaderKeys.Pithos.X_Object_Version_Timestamp,
          parsedDate)
        true

      case _ ⇒
        false
    }
  }

  override def buildResultData(
    responseHeaders: KeyMap, statusCode: Int, statusText: String, startMillis: Long, stopMillis: Long,
    getResponseBody: () => String
  ): GetObjectResultData =
    GetObjectResultData(
      stream = out,
      container = container,
      path = path,
      ETag = responseHeaders.get(PithosHeaderKeys.Standard.ETag),
      Content_Type = responseHeaders.get(PithosHeaderKeys.Standard.Content_Type),
      Content_Length = responseHeaders.get(PithosHeaderKeys.Standard.Content_Length),
      Last_Modified = responseHeaders.get(PithosHeaderKeys.Standard.Last_Modified),
      X_Object_Hash = responseHeaders.get(PithosHeaderKeys.Pithos.X_Object_Hash),
      X_Object_Modified_By = responseHeaders.get(PithosHeaderKeys.Pithos.X_Object_Modified_By),
      X_Object_Version_Timestamp = responseHeaders.get(PithosHeaderKeys.Pithos.X_Object_Version_Timestamp),
      X_Object_UUID = responseHeaders.get(PithosHeaderKeys.Pithos.X_Object_UUID),
      X_Object_Version = responseHeaders.get(PithosHeaderKeys.Pithos.X_Object_Version)
    )
}
