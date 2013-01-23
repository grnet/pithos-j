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

import com.ning.http.client.{HttpResponseBodyPart, AsyncHttpClient, Response}
import gr.grnet.pithosj.core.{Helpers, Paths, ConnectionInfo}
import gr.grnet.pithosj.core.Const.Headers
import gr.grnet.pithosj.core.Helpers.RequestBuilder
import gr.grnet.pithosj.core.http.HTTPMethod
import gr.grnet.pithosj.core.http.HTTPMethod._
import gr.grnet.pithosj.core.result.info.Info
import gr.grnet.pithosj.core.result.{Result, BaseResult}
import com.ning.http.client.AsyncHandler.STATE
import java.util.concurrent.Future

/**
 * A command to be executed via the Pithos+ REST API.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait Command[I <: Info] {
  def httpMethod: HTTPMethod

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  def successCodes: Set[Int]

  /**
   * Validates this command. Returns some error iff there is any.
   */
  def validate: Option[String] = None

  def computeURL(connInfo: ConnectionInfo): String = {
    Paths.buildWithFirst(connInfo.baseURL, computePathElements(connInfo): _*)
  }

  def computePathElements(connInfo: ConnectionInfo): Seq[String]

  def execute(connInfo: ConnectionInfo, http: AsyncHttpClient): Future[Result[I]]
}
