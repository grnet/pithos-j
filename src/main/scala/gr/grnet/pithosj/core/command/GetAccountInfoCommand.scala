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

import gr.grnet.common.http.{Result, TResult, Method}
import gr.grnet.common.keymap.KeyMap
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.keymap.PithosHeaderKeys

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
case class GetAccountInfoCommand(serviceInfo: ServiceInfo) extends PithosCommandSkeleton[GetAccountInfoResultData] {
  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod = Method.HEAD

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  def successCodes = Set(204)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverURLPathElements = Seq(serviceInfo.uuid)

  /**
   * Type-safe keys for `HTTP` response headers that are specific to this command.
   * These usually correspond to Pithos-specific headers, not general-purpose
   * `HTTP` response headers but there may be exceptions.
   *
   * Each command must document which keys it supports.
   */
  override val responseHeaderKeys = Seq(
    PithosHeaderKeys.Pithos.X_Account_Bytes_Used,
    PithosHeaderKeys.Pithos.X_Account_Container_Count,
    PithosHeaderKeys.Pithos.X_Account_Policy_Quota,
    PithosHeaderKeys.Pithos.X_Account_Policy_Versioning
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
      case PithosHeaderKeys.Pithos.X_Account_Bytes_Used.name ⇒
        keyMap.set(PithosHeaderKeys.Pithos.X_Account_Bytes_Used, value.toLong)
        true

      case PithosHeaderKeys.Pithos.X_Account_Container_Count.name ⇒
        keyMap.set(PithosHeaderKeys.Pithos.X_Account_Container_Count, value.toInt)
        true

      case PithosHeaderKeys.Pithos.X_Account_Policy_Quota.name ⇒
        keyMap.set(PithosHeaderKeys.Pithos.X_Account_Policy_Quota, value.toLong)
        true

      case _ ⇒
        false
    }
  }


  override def buildResultData(
    responseHeaders: KeyMap, statusCode: Int, statusText: String, startMillis: Long, stopMillis: Long,
    getResponseBody: () => String
  ): GetAccountInfoResultData = {

    GetAccountInfoResultData(
      X_Account_Bytes_Used = responseHeaders.getEx(PithosHeaderKeys.Pithos.X_Account_Bytes_Used),
      X_Account_Container_Count = responseHeaders.getEx(PithosHeaderKeys.Pithos.X_Account_Container_Count),
      X_Account_Policy_Quota = responseHeaders.getEx(PithosHeaderKeys.Pithos.X_Account_Policy_Quota)
    )
  }
}
