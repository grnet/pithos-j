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

import com.twitter.finagle.httpx.Response
import com.twitter.io.Buf
import gr.grnet.common.Paths
import gr.grnet.common.http.{Result, TResult}
import gr.grnet.common.key.{HeaderKey, ResultKey}
import gr.grnet.pithosj.core.http.PithosHeader
import gr.grnet.pithosj.core.keymap.PithosHeaderKeys
import org.jboss.netty.handler.codec.http.QueryStringEncoder
import org.slf4j.LoggerFactory
import typedkey.env.MEnv

trait PithosCommandSkeleton[T] extends PithosCommand[T] {
  protected val logger = LoggerFactory.getLogger(this.getClass)

  def onResponseOpt: Option[(Buf) ⇒ Unit] = None

  /**
   * The HTTP query parameters that are set by this command.
   */
  def queryParameters = Map[String, String]()

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
  def requestHeaders = Map(PithosHeader.X_Auth_Token.name() → serviceInfo.token)

  /**
   * Computes the URL that will be used in the HTTP call.
   * The URL does not contain any needed parameters.
   */
  def callURLExcludingParameters: String =
    Paths.buildWithFirst(serviceInfo.serverURL.toString, serverRootPath)

  def callURL: String = {
    val urlNoParams = callURLExcludingParameters
    val encoder = new QueryStringEncoder(urlNoParams)

    for {
      (k, v) ← queryParameters
    } {
      encoder.addParam(k, v)
    }

    encoder.toUri.toString
  }

  /**
   * Provides the HTTP request body, if any.
   */
  def requestBodyOpt: Option[Buf] = None

  protected def newDefaultRequestHeaders: MEnv =
    MEnv.ofOne(PithosHeaderKeys.Pithos.X_Auth_Token, serviceInfo.token)

  protected def newQueryParameters: MEnv = MEnv()

  def buildResult(response: Response, startMillis: Long, stopMillis: Long): TResult[T] = {
    val status = response.status
    val isSuccess = successStatuses(status)

    Result(
      successStatuses,
      status,
      startMillis,
      stopMillis,
      response.headerMap,
      if(isSuccess)  Some(buildResultData(response, startMillis, stopMillis)) else None,
      if(!isSuccess) Some(response.contentString) else None
    )
  }
}
