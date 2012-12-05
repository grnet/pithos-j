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

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
sealed trait Result {
  def statusCode: Int
  def statusText: String
  def headers: MetaData
  def completionMillis: Long

  def contentType: String

  def disposeResources(): Unit

  def is204: Boolean
  def is400: Boolean
  def is401: Boolean
  def is403: Boolean
  def is404: Boolean
  def is503: Boolean
}

abstract class ResultSkeleton(
    _statusCode: Int,
    _statusText: String,
    _headers: MetaData,
    _completionMillis: Long
) extends Result {
  def statusCode = _statusCode
  def statusText = _statusText
  def headers = _headers
  def completionMillis = _completionMillis

  def contentType = headers.get(Const.Headers.Content_Type)

  def is204 = statusCode == 204
  def is400 = statusCode == 400
  def is401 = statusCode == 401
  def is403 = statusCode == 403
  def is404 = statusCode == 404
  def is503 = statusCode == 503
}

final class MetaDataResult(
    statusCode: Int,
    statusText: String,
    headers: MetaData,
    completionMillis: Long
) extends ResultSkeleton(statusCode, statusText, headers, completionMillis) {

  def disposeResources() {}
}

final class AccountInfoResult(
    statusCode: Int,
    statusText: String,
    headers: MetaData,
    completionMillis: Long
) extends ResultSkeleton(statusCode, statusText, headers, completionMillis) {
  def disposeResources() {}

  def bytesUsed: Long = headers.get(Const.Headers.Pithos.X_Account_Bytes_Used).toLong
  def containerCount: Int = headers.get(Const.Headers.Pithos.X_Account_Container_Count).toInt
  def policyQuota: Long = headers.get(Const.Headers.Pithos.X_Account_Policy_Quota).toLong
  def policyVersioning: String = headers.get(Const.Headers.Pithos.X_Account_Policy_Versioning)
  def usageRatio: Double = bytesUsed.toDouble / policyQuota.toDouble
}


