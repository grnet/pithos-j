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

final case class BaseResult(
    statusCode: Int,
    statusText: String,
    headers: MetaData,
    completionMillis: Int
) {

  def contentType = headers.getOne(Const.Headers.Content_Type)

  def is204 = statusCode == 204
  def is400 = statusCode == 400
  def is401 = statusCode == 401
  def is403 = statusCode == 403
  def is404 = statusCode == 404
  def is503 = statusCode == 503
}

abstract class ExtendedResultSkeleton(
    baseResult: BaseResult
) extends Result {
  def statusCode = baseResult.statusCode
  def statusText = baseResult.statusText
  def headers = baseResult.headers
  def completionMillis = baseResult.completionMillis

  def contentType = baseResult.contentType

  def is204 = baseResult.is204
  def is400 = baseResult.is400
  def is401 = baseResult.is401
  def is403 = baseResult.is403
  def is404 = baseResult.is404
  def is503 = baseResult.is503

  def disposeResources() {}
}

final case class BareExtendedResult(
    baseResult: BaseResult
) extends ExtendedResultSkeleton(baseResult)

final case class AccountInfoResult(
    baseResult: BaseResult
) extends ExtendedResultSkeleton(baseResult) {

  def bytesUsed: Long = headers.getOne(Const.Headers.Pithos.X_Account_Bytes_Used).toLong
  def containerCount: Int = headers.getOne(Const.Headers.Pithos.X_Account_Container_Count).toInt
  def policyQuota: Long = headers.getOne(Const.Headers.Pithos.X_Account_Policy_Quota).toLong
  def policyVersioning: String = headers.getOne(Const.Headers.Pithos.X_Account_Policy_Versioning)
  def usageRatio: Double = bytesUsed.toDouble / policyQuota.toDouble
}

final case class ListContainersResult(
    baseResult: BaseResult,
    containers: List[ContainerInfo]
) extends ExtendedResultSkeleton(baseResult)


