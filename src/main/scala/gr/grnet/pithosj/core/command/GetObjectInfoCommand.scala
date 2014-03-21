/*
 * Copyright 2012-2014 GRNET S.A. All rights reserved.
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

import gr.grnet.common.date.DateParsers
import gr.grnet.common.http.Method
import gr.grnet.common.keymap.KeyMap
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.keymap.{PithosResultKeys, PithosHeaderKeys}

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
case class GetObjectInfoCommand(
    serviceInfo: ServiceInfo,
    container: String,
    path: String
) extends PithosCommandSkeleton[GetObjectInfoResultData] {
  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod = Method.HEAD

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
  ): GetObjectInfoResultData =
    GetObjectInfoResultData(
      container = container,
      path = path,
      ETag = responseHeaders.getEx(PithosHeaderKeys.Standard.ETag),
      Content_Type = responseHeaders.getEx(PithosHeaderKeys.Standard.Content_Type),
      Content_Length = responseHeaders.getEx(PithosHeaderKeys.Standard.Content_Length),
      Last_Modified = responseHeaders.getEx(PithosHeaderKeys.Standard.Last_Modified),
      X_Object_Hash = responseHeaders.getEx(PithosHeaderKeys.Pithos.X_Object_Hash),
      X_Object_Modified_By = responseHeaders.getEx(PithosHeaderKeys.Pithos.X_Object_Modified_By),
      X_Object_Version_Timestamp = responseHeaders.getEx(PithosHeaderKeys.Pithos.X_Object_Version_Timestamp),
      X_Object_UUID = responseHeaders.getEx(PithosHeaderKeys.Pithos.X_Object_UUID),
      X_Object_Version = responseHeaders.getEx(PithosHeaderKeys.Pithos.X_Object_Version)
    )
}
