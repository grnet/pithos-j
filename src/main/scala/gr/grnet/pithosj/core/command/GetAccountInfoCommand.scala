/*
 * Copyright (C) 2010-2014 GRNET S.A.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gr.grnet.pithosj.core.command

import gr.grnet.common.http.Method
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.keymap.PithosHeaderKeys
import typedkey.env.{ImEnv, MEnv}

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
   * The parsed [[gr.grnet.common.key.HeaderKey]]
   * and its associated non-String value are recorded in the provided `env`.
   */
  override protected def tryParseNonStringResponseHeader(
    env: MEnv,
    name: String,
    value: String
  ) = {
    name match {
      case PithosHeaderKeys.Pithos.X_Account_Bytes_Used.name ⇒
        env.update(PithosHeaderKeys.Pithos.X_Account_Bytes_Used, value.toLong)
        true

      case PithosHeaderKeys.Pithos.X_Account_Container_Count.name ⇒
        env.update(PithosHeaderKeys.Pithos.X_Account_Container_Count, value.toInt)
        true

      case PithosHeaderKeys.Pithos.X_Account_Policy_Quota.name ⇒
        env.update(PithosHeaderKeys.Pithos.X_Account_Policy_Quota, value.toLong)
        true

      case _ ⇒
        false
    }
  }


  override def buildResultData(
    responseHeaders: ImEnv, statusCode: Int, statusText: String, startMillis: Long, stopMillis: Long,
    getResponseBody: () => String
  ): GetAccountInfoResultData = {

    GetAccountInfoResultData(
      X_Account_Bytes_Used = responseHeaders.get(PithosHeaderKeys.Pithos.X_Account_Bytes_Used),
      X_Account_Container_Count = responseHeaders.get(PithosHeaderKeys.Pithos.X_Account_Container_Count),
      X_Account_Policy_Quota = responseHeaders.get(PithosHeaderKeys.Pithos.X_Account_Policy_Quota)
    )
  }
}
