/*
 * Copyright 20122-13 GRNET S.A. All rights reserved.
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

import Const.Headers
import com.ning.http.client.AsyncHandler.STATE
import com.ning.http.client.{HttpResponseBodyPart, AsyncCompletionHandler, AsyncHttpClient, Response}
import gr.grnet.pithosj.core.result.info.Info
import gr.grnet.pithosj.core.result.{Result, BaseResult}
import java.util
import java.util.concurrent.{ExecutionException, TimeUnit, Future}
import org.slf4j.LoggerFactory
import gr.grnet.pithosj.core.command.Command

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
sealed class Helpers {
  import Helpers.RequestBuilder

  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  @inline def jListOne[T](item: T): util.List[T] = {
    val list = new util.ArrayList[T]()
    list.add(item)
    list
  }

  @inline final def copyResponseHeader(header: String, response: Response, meta: MetaData) {
    meta.set(header, response.getHeaders(header))
  }

  @inline final def ifNull(value: String, other: String): String = {
    if(value ne null) value else other
  }

  final def knownGoodFuture[V](value: V): Future[V] = {
    new Future[V] {
      def isCancelled = false

      def get(timeout: Long, unit: TimeUnit) = value

      def get() = value

      def cancel(mayInterruptIfRunning: Boolean) = false

      def isDone = true
    }
  }

  final def knownBadFuture[V](cause: Throwable, message: String): Future[V] = {
    new Future[V] {
      def isCancelled = false

      def get(timeout: Long, unit: TimeUnit) = throw new ExecutionException(message, cause)

      def get() = throw new ExecutionException(message, cause)

      def cancel(mayInterruptIfRunning: Boolean) = false

      def isDone = true
    }
  }

  final def knownBadFuture[V](message: String): Future[V] = {
    knownBadFuture(null, message)
  }

  final def copyAllResponseHeaders(response: Response, meta: MetaData) {
    val headers = asScala(response.getHeaders)
    for(k<- headers.keysIterator) {
      copyResponseHeader(k, response, meta)
    }
  }

  final def prepareVerb(
      preparer: String ⇒ RequestBuilder,
      connInfo: ConnectionInfo,
      paths: String*
  ): RequestBuilder = {
    val url = Paths.buildWithFirst(connInfo.baseURL, paths: _*)
    val requestBuilder = preparer(url)
    requestBuilder.addHeader(Headers.Pithos.X_Auth_Token.header, connInfo.userToken)
  }

  final def prepareHEAD(http: AsyncHttpClient, connInfo: ConnectionInfo, paths: String*) = {
    prepareVerb(http.prepareHead, connInfo, paths: _*)
  }

  final def prepareGET(http: AsyncHttpClient, connInfo: ConnectionInfo, paths: String*) = {
    prepareVerb(http.prepareGet, connInfo, paths: _*)
  }

  final def preparePOST(http: AsyncHttpClient, connInfo: ConnectionInfo, paths: String*) = {
    prepareVerb(http.preparePost, connInfo, paths: _*)
  }

  final def preparePUT(http: AsyncHttpClient, connInfo: ConnectionInfo, paths: String*) = {
    prepareVerb(http.preparePut, connInfo, paths: _*)
  }

  final def prepareDELETE(http: AsyncHttpClient, connInfo: ConnectionInfo, paths: String*) = {
    prepareVerb(http.prepareDelete, connInfo, paths: _*)
  }

  final def execAsyncCompletionHandler[I <: Info](
      requestBuilder: RequestBuilder,
      command: Command[I]
  ): Future[Result[I]] = {
    val p = command.onBodyPartReceived
    val f = command.extractResult(_, _)
    execAsyncCompletionHandler(requestBuilder)(p)(f)
  }

  final def execAsyncCompletionHandler[I <: Info](
      requestBuilder: RequestBuilder
  )(  p: (HttpResponseBodyPart) ⇒ STATE = null)
   (  f: (Response, BaseResult) ⇒ Result[I]): Future[Result[I]] = {

    val startMillis = System.currentTimeMillis()

    val handler = new AsyncCompletionHandler[Result[I]] {
      override def onBodyPartReceived(content: HttpResponseBodyPart) = {
        if(p eq null ) super.onBodyPartReceived(content)
        else           p(content)
      }

      def onCompleted(response: Response) = {
        val meta = new MetaData
        copyAllResponseHeaders(response, meta)

        val baseResult = new BaseResult(
          response.getStatusCode,
          response.getStatusText,
          meta,
          (System.currentTimeMillis() - startMillis).toInt
        )
        f(response, baseResult)
      }
    }

    requestBuilder.execute(handler)
  }
}

final object Helpers extends Helpers {
  type RequestBuilder = AsyncHttpClient#BoundRequestBuilder
}
