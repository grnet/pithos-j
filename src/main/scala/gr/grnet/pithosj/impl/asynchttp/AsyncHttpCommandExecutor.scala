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

package gr.grnet.pithosj.impl.asynchttp

import gr.grnet.common.http.Result
import gr.grnet.common.http.Method._
import com.ning.http.client.{AsyncCompletionHandler, Response, HttpResponseBodyPart, AsyncHttpClient}
import gr.grnet.common.http.Method
import gr.grnet.pithosj.core.Helpers.RequestBuilder
import gr.grnet.pithosj.core.PithosException
import gr.grnet.pithosj.core.asFullScala
import gr.grnet.pithosj.core.command.{Command, CommandExecutor}
import gr.grnet.pithosj.core.http.{InputStreamRequestBody, StringRequestBody, BytesRequestBody, FileRequestBody, RequestBody}
import scala.concurrent.Promise

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
class AsyncHttpCommandExecutor(http: AsyncHttpClient) extends CommandExecutor {
  /**
   * Creates a request builder for this command.
   */
  private def createRequestBuilder(command: Command): RequestBuilder = {
    val url = command.serverURLExcludingParameters

    val requestBuilder = command.httpMethod match {
      case HEAD ⇒ http.prepareHead(url)
      case GET ⇒ http.prepareGet(url)
      case PUT ⇒ http.preparePut(url)
      case POST ⇒ http.preparePost(url)
      case DELETE ⇒ http.prepareDelete(url)
      case OPTIONS ⇒ http.prepareOptions(url)
      case COPY ⇒ http.preparePut(url).setMethod(Method.COPY.name())
      case method ⇒ throw new PithosException(
        "Unsupported HTTP method %s. All known methods are %s",
        method,
        Method.values().mkString(", ")
      )
    }

    val headers = command.requestHeaders.toMap
    for((name, value) ← headers) {
      requestBuilder.setHeader(name, String.valueOf(value))
    }

    val queryParams = command.queryParameters.toMap
    for((name, value) ← queryParams) {
      requestBuilder.addQueryParameter(name, String.valueOf(value))
    }

    for(requestBody ← command.requestBodyOpt) {
      setBody(requestBuilder, requestBody)
    }

    requestBuilder
  }

  private def setBody(requestBuilder: RequestBuilder, requestBody: RequestBody) {
    requestBody match {
      case FileRequestBody(body) ⇒
        requestBuilder.setBody(body)
      case BytesRequestBody(body) ⇒
        requestBuilder.setBody(body)
      case StringRequestBody(body) ⇒
        requestBuilder.setBody(body)
      case InputStreamRequestBody(body) ⇒
        requestBuilder.setBody(body)
    }
  }

  /**
   * Executes the given command and returns a [[java.util.concurrent.Future]]
   * with the command-specific result.
   */
  def execute(command: Command) = {
    val promise = Promise[Result]()
    val requestBuilder = createRequestBuilder(command)

    val startMillis = System.currentTimeMillis()
    val onBodyPartReceivedOpt = command.onBodyPartReceivedOpt

    val handler = new AsyncCompletionHandler[Result] {
      override def onBodyPartReceived(content: HttpResponseBodyPart) = {
        onBodyPartReceivedOpt match {
          case Some(onBodyPartReceived) ⇒
            onBodyPartReceived.apply(content)
          case None ⇒
            super.onBodyPartReceived(content)
        }
      }

      def onCompleted(response: Response) = {
        val stopMillis = System.currentTimeMillis()
        val rawHeaders = asFullScala(response.getHeaders)
        val responseHeaders = command.parseAllResponseHeaders(rawHeaders)

        val result = command.buildResult(
          responseHeaders,
          response.getStatusCode,
          response.getStatusText,
          startMillis,
          stopMillis,
          () ⇒ response.getResponseBody
        )

        promise.success(result)
        result
      }

      override def onThrowable(t: Throwable) {
        promise.failure(t)
      }
    }

    requestBuilder.execute(handler)
    promise.future
  }
}
