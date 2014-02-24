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

import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.command.result.Result
import gr.grnet.pithosj.core.date.DateParsers
import gr.grnet.pithosj.core.http.Method
import gr.grnet.pithosj.core.keymap.{ResultKeys, HeaderKeys, HeaderKey, KeyMap}

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
case class GetObjectInfo(
    serviceInfo: ServiceInfo,
    container: String,
    path: String
) extends CommandSkeleton {
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
    HeaderKeys.Standard.ETag,
    HeaderKeys.Standard.Content_Type,
    HeaderKeys.Standard.Content_Length,
    HeaderKeys.Standard.Last_Modified,
    HeaderKeys.Pithos.X_Object_Hash,
    HeaderKeys.Pithos.X_Object_Modified_By,
    HeaderKeys.Pithos.X_Object_Version_Timestamp,
    HeaderKeys.Pithos.X_Object_UUID,
    HeaderKeys.Pithos.X_Object_UUID,
    HeaderKeys.Pithos.X_Object_Version
  )

  override val resultDataKeys = Seq(
    ResultKeys.Commands.Container,
    ResultKeys.Commands.Path
  )

  /**
   * Parse a response header that is specific to this command and whose value must be of non-String type.
   *
   * Returns `true` iff the header is parsed.
   *
   * The parsed [[gr.grnet.pithosj.core.keymap.HeaderKey]]
   * and its associated non-String value are recorded in the provided `keyMap`.
   */
  override protected def tryParseNonStringResponseHeader(
      keyMap: KeyMap,
      name: String,
      value: String
  ) = {
    name match {
      case HeaderKeys.Standard.Last_Modified.name ⇒
        // Wed, 19 Sep 2012 08:18:23 GMT
        val parsedDate = DateParsers.parse(value, DateParsers.Format2Parser)
        keyMap.set(
          HeaderKeys.Standard.Last_Modified,
          parsedDate)
        true

      case HeaderKeys.Pithos.X_Object_Version_Timestamp.name ⇒
        // Wed, 19 Sep 2012 08:18:23 GMT
        val parsedDate = DateParsers.parse(value, DateParsers.Format2Parser)
        keyMap.set(
          HeaderKeys.Pithos.X_Object_Version_Timestamp,
          parsedDate)
        true

      case _ ⇒
        false
    }
  }

  override def buildResult(
      initialMap: KeyMap,
      statusCode: Int,
      statusText: String,
      startMillis: Long,
      stopMillis: Long,
      getResponseBody: () ⇒ String
  ) = {

    val resultData = KeyMap(initialMap)

    if(successCodes(statusCode)) {
      resultData.set(ResultKeys.Commands.Container, container)
      resultData.set(ResultKeys.Commands.Path, path)
    }

    super.buildResult(
      resultData,
      statusCode,
      statusText,
      startMillis,
      stopMillis,
      getResponseBody
    )
  }
}
