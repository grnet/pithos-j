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

package gr.grnet.pithosj.impl.asynchttp

import com.ning.http.client.{AsyncCompletionHandler, Response, HttpResponseBodyPart, AsyncHttpClient}
import gr.grnet.common.http.Method._
import gr.grnet.common.http.{TResult, Result, Method}
import gr.grnet.pithosj.core.Helpers.RequestBuilder
import gr.grnet.pithosj.core.PithosException
import gr.grnet.pithosj.core.asFullScala
import gr.grnet.pithosj.core.command.{PithosCommand, CommandExecutor}
import gr.grnet.pithosj.core.http.{ChannelBufferRequestBody, InputStreamRequestBody, StringRequestBody, BytesRequestBody, FileRequestBody, RequestBody}
import scala.concurrent.Promise
import com.ning.http.client.Request.EntityWriter
import java.io.OutputStream

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
class AsyncHttpCommandExecutor(http: AsyncHttpClient) extends CommandExecutor {
  /**
   * Creates a request builder for this command.
   */
  private def createRequestBuilder[T](command: PithosCommand[T]): RequestBuilder = {
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
      case ChannelBufferRequestBody(body) ⇒
        val entityWriter = new EntityWriter {
          override def writeEntity(out: OutputStream): Unit = body.readBytes(out, body.readableBytes())
        }
        requestBuilder.setBody(entityWriter)
    }
  }

  /**
   * Executes the given command and returns a [[java.util.concurrent.Future]]
   * with the command-specific result.
   */
  def execute[T](command: PithosCommand[T]) = {
    val promise = Promise[TResult[T]]()
    val requestBuilder = createRequestBuilder(command)

    val startMillis = System.currentTimeMillis()
    val onBodyPartReceivedOpt = command.onBodyPartReceivedOpt

    val handler = new AsyncCompletionHandler[TResult[T]] {
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
