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

import gr.grnet.pithosj.core.Const.Headers
import gr.grnet.pithosj.core.command.result.SimpleResult
import gr.grnet.pithosj.core.http.HTTPMethod
import gr.grnet.pithosj.core.{MetaData, ConnectionInfo, Paths}

/**
 * Copies an object around.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
case class CopyObject(
    connectionInfo: ConnectionInfo,
    fromContainer: String,
    fromPath: String,
    toContainer: String,
    toPath: String
) extends CommandSkeleton[SimpleResult] {

  /**
   * The HTTP method by which the command is implemented.
   */
  val httpMethod = HTTPMethod.PUT

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  val successCodes = Set(201)

  /**
   * The HTTP request headers that are set by this command.
   */
  override val requestHeaders = {
    newDefaultRequestHeaders.
      setOne(Headers.Pithos.X_Copy_From, Paths.build(fromContainer, fromPath)).
      setOne(Headers.Standard.Content_Length, 0.toString)
  }

  /**
   * The HTTP request parameters that are set by this command.
   */
  override val queryParameters = MetaData.Empty

  def serverURLPathElements = Seq(connectionInfo.userID, toContainer, toPath)

  def buildResult(
      responseHeaders: MetaData,
      statusCode: Int,
      statusText: String,
      completionMillis: Long,
      getResponseBody: () ⇒ String
  ) = {
    SimpleResult(this, responseHeaders, statusCode, statusText, completionMillis, successCodes(statusCode))
  }
}
