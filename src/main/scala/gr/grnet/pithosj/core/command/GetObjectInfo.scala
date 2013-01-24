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

import gr.grnet.pithosj.core.Const.{Headers, IHeader}
import gr.grnet.pithosj.core.command.result.{GetObjectInfoResultData, GetObjectInfoResult}
import gr.grnet.pithosj.core.http.HTTPMethod
import gr.grnet.pithosj.core.{Const, MetaData, ConnectionInfo}

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
case class GetObjectInfo(
    connectionInfo: ConnectionInfo,
    container: String,
    path: String
) extends CommandSkeleton[GetObjectInfoResult] {
  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod = HTTPMethod.HEAD

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  def successCodes = Set(200)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverURLPathElements = Seq(connectionInfo.userID, container, path)

  def buildResult(
      responseHeaders: MetaData,
      statusCode: Int,
      statusText: String,
      completionMillis: Long,
      getResponseBody: () ⇒ String
  ) = {
    val resultDataOpt = successCodes(statusCode) match {
      case false ⇒
        None
      case true ⇒
        def h(name: IHeader) = responseHeaders.getOne(name)

        val resultData = GetObjectInfoResultData(
          container = container,
          path = path,
          contentType = h(Headers.Standard.Content_Type),
          contentLength = h(Headers.Standard.Content_Length).toLong,
          // Wed, 19 Sep 2012 08:18:23 GMT
          lastModified = Const.Dates.Format2.parse(h(Headers.Standard.Last_Modified)),
          xObjectHash = h(Headers.Pithos.X_Object_Hash),
          xObjectModifiedBy = h(Headers.Pithos.X_Object_Modified_By),
          xObjectVersionTimestamp = Const.Dates.Format2.parse(h(Headers.Pithos.X_Object_Version_Timestamp)),
          xObjectUUID = h(Headers.Pithos.X_Object_UUID),
          xObjectVersion = h(Headers.Pithos.X_Object_Version),
          eTag = Some(h(Headers.Standard.ETag))
        )

        Some(resultData)
    }

    GetObjectInfoResult(
      this,
      responseHeaders,
      statusCode,
      statusText,
      completionMillis,
      resultDataOpt
    )
  }
}
