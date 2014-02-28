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
import gr.grnet.common.Paths
import gr.grnet.common.http.{Result, CommandDescriptor}
import gr.grnet.common.keymap.{ResultKey, HeaderKey, KeyMap}
import gr.grnet.pithosj.core.Helpers
import gr.grnet.pithosj.core.http.RequestBody
import gr.grnet.pithosj.core.keymap.{ResultKeys, HeaderKeys}
import org.slf4j.LoggerFactory

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait PithosCommandSkeleton extends PithosCommand {
  protected val logger = LoggerFactory.getLogger(this.getClass)

  def onBodyPartReceivedOpt: Option[HttpResponseBodyPart ⇒ STATE] = None

  /**
   * The HTTP query parameters that are set by this command.
   */
  val queryParameters = newQueryParameters

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
  val requestHeaders = newDefaultRequestHeaders

  /**
   * Parse a response header that is specific to this command and whose value must be of non-String type.
   *
   * Returns `true` iff the header is parsed.
   *
   * The parsed [[gr.grnet.common.keymap.HeaderKey]]
   * and its associated non-String value are recorded in the provided `keyMap`.
   */
  protected def tryParseNonStringResponseHeader(
      keyMap: KeyMap,
      name: String,
      values: List[String]
  ): Boolean = {
    values match {
      case value :: _ ⇒
        tryParseNonStringResponseHeader(keyMap, name, value)
      case _ ⇒
        false
    }
  }

  /**
   * Parse a response header that is specific to this command and whose value must be of non-String type.
   *
   * Returns `true` iff the header is parsed.
   *
   * The parsed [[gr.grnet.common.keymap.HeaderKey]]
   * and its associated non-String value are recorded in the provided `keyMap`.
   */
  protected def tryParseNonStringResponseHeader(
      keyMap: KeyMap,
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

  protected def newDefaultRequestHeaders: KeyMap = {
    KeyMap().
      set(HeaderKeys.Pithos.X_Auth_Token, serviceInfo.token)
  }

  protected def newQueryParameters: KeyMap = KeyMap()

  def descriptor: CommandDescriptor = {
    CommandDescriptor(
      userID = serviceInfo.uuid,
      requestURL = serverURLExcludingParameters,
      httpMethod = httpMethod,
      requestHeaders = requestHeaders,
      queryParameters = queryParameters,
      successCodes = successCodes
    )
  }

  def parseAllResponseHeaders(responseHeaders: scala.collection.Map[String, List[String]]): KeyMap = {
    val keyMap = KeyMap()
    val myKeyNames = this.responseHeaderKeys.map(_.name).toSet

    for(keyName ← responseHeaders.keySet) {
      val keyValues = responseHeaders(keyName)

      if(myKeyNames.contains(keyName)) {
        // It is a header specific to this command.
        // Try parse it specifically
        if(!tryParseNonStringResponseHeader(keyMap, keyName, keyValues)) {
          // No specific parsing needed, so just handle it generically.
          Helpers.parseGenericResponseHeader(keyMap, keyName, keyValues)
        }
      }
      else {
        Helpers.parseGenericResponseHeader(keyMap, keyName, keyValues)
      }
    }

    keyMap
  }

  def buildResult(
      responseHeaders: KeyMap,
      statusCode: Int,
      statusText: String,
      startMillis: Long,
      stopMillis: Long,
      getResponseBody: () ⇒ String,
      resultData: KeyMap
  ) = {

    Result(
      descriptor,
      statusCode,
      statusText,
      startMillis,
      stopMillis,
      responseHeaders,
      resultData.set(ResultKeys.ResponseBody, getResponseBody())
    )
  }
}
