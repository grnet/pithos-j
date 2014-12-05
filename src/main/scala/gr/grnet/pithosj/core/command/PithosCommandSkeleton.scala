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

import com.ning.http.client.AsyncHandler.STATE
import com.ning.http.client.HttpResponseBodyPart
import gr.grnet.common.Paths
import gr.grnet.common.http.{CommandDescriptor, RequestBody, Result, TResult}
import gr.grnet.common.key.{HeaderKey, ResultKey}
import gr.grnet.pithosj.core.Helpers
import gr.grnet.pithosj.core.keymap.PithosHeaderKeys
import org.slf4j.LoggerFactory
import typedkey.env.{Env, ImEnv, MEnv}

trait PithosCommandSkeleton[T] extends PithosCommand[T] {
  protected val logger = LoggerFactory.getLogger(this.getClass)

  def onBodyPartReceivedOpt: Option[HttpResponseBodyPart ⇒ STATE] = None

  /**
   * The HTTP query parameters that are set by this command.
   */
  val queryParameters = newQueryParameters.toImmutable

  /**
   * Type-safe keys for `HTTP` response headers that are specific to this command.
   * These usually correspond to Pithos-specific headers, not general-purpose
   * `HTTP` response headers but there may be exceptions.
   *
   * Each command must document which keys it supports.
   */
  val responseHeaderKeys = Seq[HeaderKey[_]]()


  /**
   * The keys for extra result data pertaining to this command.
   * Normally, the data that the keys refer to will be parsed
   * from the `HTTP` response body (`XML` or `JSON`).
   */
  val resultDataKeys = Seq[ResultKey[_]]()

  /**
   * The HTTP request headers that are set by this command.
   */
  val requestHeaders = newDefaultRequestHeaders.toImmutable

  /**
   * Parse a response header that is specific to this command and whose value must be of non-String type.
   *
   * Returns `true` iff the header is parsed.
   *
   * The parsed [[gr.grnet.common.key.HeaderKey]]
   * and its associated non-String value are recorded in the provided `env`.
   */
  protected def tryParseNonStringResponseHeader(
      env: MEnv,
      name: String,
      values: List[String]
  ): Boolean = {
    values match {
      case value :: _ ⇒
        tryParseNonStringResponseHeader(env, name, value)
      case _ ⇒
        false
    }
  }

  /**
   * Parse a response header that is specific to this command and whose value must be of non-String type.
   *
   * Returns `true` iff the header is parsed.
   *
   * The parsed [[gr.grnet.common.key.HeaderKey]]
   * and its associated non-String value are recorded in the provided `env`.
   */
  protected def tryParseNonStringResponseHeader(
      keyMap: MEnv,
      name: String,
      value: String
  ): Boolean = false

  /**
   * Computes the URL that will be used in the HTTP call.
   * The URL does not contain any needed parameters.
   */
  def serverURLExcludingParameters: String  = {
    Paths.buildWithFirst(serviceInfo.serviceURL, serverURLPathElements: _*)
  }

  /**
   * Provides the HTTP request body, if any.
   */
  val requestBodyOpt: Option[RequestBody] = None

  protected def newDefaultRequestHeaders: MEnv =
    MEnv.ofOne(PithosHeaderKeys.Pithos.X_Auth_Token, serviceInfo.token)

  protected def newQueryParameters: MEnv = MEnv()

  def descriptor: CommandDescriptor = {
    CommandDescriptor(
      userID = serviceInfo.uuid,
      requestURL = serverURLExcludingParameters,
      httpMethod = httpMethod,
      requestHeaders = requestHeaders.toImmutable,
      queryParameters = queryParameters.toImmutable,
      successCodes = successCodes
    )
  }

  def parseAllResponseHeaders(responseHeaders: scala.collection.Map[String, List[String]]): ImEnv = {
    val env = MEnv()
    val myKeyNames = this.responseHeaderKeys.map(_.name).toSet

    for(keyName ← responseHeaders.keySet) {
      val keyValues = responseHeaders(keyName)

      if(myKeyNames.contains(keyName)) {
        // It is a header specific to this command.
        // Try parse it specifically
        if(!tryParseNonStringResponseHeader(env, keyName, keyValues)) {
          // No specific parsing needed, so just handle it generically.
          Helpers.parseGenericResponseHeader(env, keyName, keyValues)
        }
      }
      else {
        Helpers.parseGenericResponseHeader(env, keyName, keyValues)
      }
    }

    env.toImmutable
  }

  /**
   * Builds the domain-specific result of this command. Each command knows how to parse the HTTP response
   * in order to produce domain-specific objects.
   */
  override def buildResult(
    responseHeaders: ImEnv,
    statusCode: Int,
    statusText: String,
    startMillis: Long,
    stopMillis: Long,
    getResponseBody: () ⇒ String,
    resultData: ImEnv
  ): TResult[T] = {

    val isSuccess = successCodes(statusCode)
    Result(
      descriptor,
      statusCode,
      statusText,
      startMillis,
      stopMillis,
      responseHeaders,
      if(isSuccess)
        Some(buildResultData(
          responseHeaders = responseHeaders,
          statusCode = statusCode,
          statusText = statusText,
          startMillis = startMillis,
          stopMillis = stopMillis,
          getResponseBody = getResponseBody
        ))
      else None,
      if(!isSuccess)
        Some(getResponseBody())
      else
        None
    )
  }
}
