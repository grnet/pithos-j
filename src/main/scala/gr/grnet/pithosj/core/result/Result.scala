/*
 * Copyright 2012 GRNET S.A. All rights reserved.
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

package gr.grnet.pithosj.core
package result

import gr.grnet.pithosj.core.MetaData
import gr.grnet.pithosj.core.result.info.Info

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final case class Result[I <: Info](
    info: I,
    baseResult: BaseResult
) {

  def statusCode = baseResult.statusCode
  def statusText = baseResult.statusText
  def headers = baseResult.headers
  def completionMillis = baseResult.completionMillis

  final def isStatusCode(statusCode: Int): Boolean = statusCode == this.statusCode

  @inline final def is200 = isStatusCode(200)
  @inline final def is204 = isStatusCode(204)
  @inline final def is400 = isStatusCode(400)
  @inline final def is401 = isStatusCode(401)
  @inline final def is403 = isStatusCode(403)
  @inline final def is404 = isStatusCode(404)
  @inline final def is503 = isStatusCode(503)
}

final case class BaseResult(
    statusCode: Int,
    statusText: String,
    headers: MetaData,
    completionMillis: Int
) {

  def getHeader(name: String) = headers.getOne(name)
  def getHeaders(name: String) = headers.get(name)
}
