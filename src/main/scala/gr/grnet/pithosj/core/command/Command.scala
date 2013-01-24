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

import com.ning.http.client.AsyncHandler.STATE
import com.ning.http.client.HttpResponseBodyPart
import gr.grnet.pithosj.core.http.{RequestBody, HTTPMethod}
import gr.grnet.pithosj.core.{MetaData, Paths, ConnectionInfo}

/**
 * A command to be executed via the Pithos+ REST API.
 *  Each command specifies its own input data, which will be used
 *  to build up an HTTP request. Also, a command has a specific type (`R`)
 *  of results to be returned from the Pithos+ REST API call.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait Command[+R] {
  /**
   * Specifies the target against which the command will be executed.
   * This includes the Pithos+ server and the Pithos+ user id and token.
   */
  def connectionInfo: ConnectionInfo

  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod: HTTPMethod

  /**
   * The HTTP request headers that are set by this command.
   */
  def requestHeaders: MetaData

  /**
   * The HTTP query parameters that are set by this command.
   */
  def queryParameters: MetaData

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  def successCodes: Set[Int]

  /**
   * Validates this command. Returns some error iff there is any.
   */
  def validate: Option[String] = None

  /**
   * Computes the URL that will be used in the HTTP call.
   * The URL does not contain any needed parameters.
   */
  def serverURLExcludingParameters: String = {
    Paths.buildWithFirst(connectionInfo.serverURL, serverURLPathElements: _*)
  }

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverURLPathElements: Seq[String]

  def onBodyPartReceivedOpt: Option[HttpResponseBodyPart ⇒ STATE]

  /**
   * Provides the HTTP request body, if any.
   */
  def requestBodyOpt: Option[RequestBody]

  def buildResult(
      responseHeaders: MetaData,
      statusCode: Int,
      statusText: String,
      completionMillis: Long,
      getResponseBody: () ⇒ String
  ): R
}
