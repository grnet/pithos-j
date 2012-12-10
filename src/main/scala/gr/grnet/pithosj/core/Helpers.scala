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

import Const.Headers
import com.ning.http.client.{HttpResponseBodyPart, AsyncCompletionHandler, AsyncHttpClient, Response}
import gr.grnet.pithosj.core.result.info.Info
import gr.grnet.pithosj.core.result.{Result, BaseResult}
import java.util
import java.util.concurrent.Future
import org.slf4j.LoggerFactory
import com.ning.http.client.AsyncHandler.STATE

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
sealed class Helpers {
  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  @inline def jListOne[T](item: T): util.List[T] = {
    val list = new util.ArrayList[T]()
    list.add(item)
    list
  }

  @inline final def copyResponseHeader(header: String, response: Response, meta: MetaData) {
    meta.set(header, response.getHeaders(header))
  }

  final def copyAllResponseHeaders(response: Response, meta: MetaData) {
    val headers = asScala(response.getHeaders)
    for(k<- headers.keysIterator) {
      copyResponseHeader(k, response, meta)
    }
  }

  final def copyPithosResponseHeaders(response: Response, meta: MetaData) {
    copyResponseHeader(Headers.Pithos.X_Account_Bytes_Used.header, response, meta)
    copyResponseHeader(Headers.Pithos.X_Account_Container_Count.header, response, meta)
    copyResponseHeader(Headers.Pithos.X_Account_Policy_Quota.header, response, meta)
    copyResponseHeader(Headers.Pithos.X_Account_Policy_Versioning.header, response, meta)
  }

  final def prepareHead(http: AsyncHttpClient, connInfo: ConnectionInfo, paths: String*) = {
    val url = Paths.buildWithFirst(connInfo.baseURL, paths: _*)
    logger.debug("prepareHead({})", url)
    val reqBuilder = http.prepareHead(url)
    reqBuilder.addHeader(Headers.Pithos.X_Auth_Token.header, connInfo.userToken)
  }

  final def prepareGet(http: AsyncHttpClient, connInfo: ConnectionInfo, paths: String*) = {
    val url = Paths.buildWithFirst(connInfo.baseURL, paths: _*)
    logger.debug("prepareGet({})", url)
    val reqBuilder = http.prepareGet(url)
    reqBuilder.addHeader(Headers.Pithos.X_Auth_Token.header, connInfo.userToken)
  }

  final def preparePost(http: AsyncHttpClient, connInfo: ConnectionInfo, paths: String*) = {
    val url = Paths.buildWithFirst(connInfo.baseURL, paths: _*)
    logger.debug("preparePost({})", url)
    val reqBuilder = http.preparePost(url)
    reqBuilder.addHeader(Headers.Pithos.X_Auth_Token.header, connInfo.userToken)
  }

  final def preparePut(http: AsyncHttpClient, connInfo: ConnectionInfo, paths: String*) = {
    val url = Paths.buildWithFirst(connInfo.baseURL, paths: _*)
    logger.debug("preparePut({})", url)
    val reqBuilder = http.preparePut(url)
    reqBuilder.addHeader(Headers.Pithos.X_Auth_Token.header, connInfo.userToken)
  }

  final def prepareDelete(http: AsyncHttpClient, connInfo: ConnectionInfo, paths: String*) = {
    val url = Paths.buildWithFirst(connInfo.baseURL, paths: _*)
    logger.debug("prepareDelete({})", url)
    val reqBuilder = http.prepareDelete(url)
    reqBuilder.addHeader(Headers.Pithos.X_Auth_Token.header, connInfo.userToken)
  }

  final def execAsyncCompletionHandler[I <: Info](
      reqBuilder: AsyncHttpClient#BoundRequestBuilder
  )(  p: (HttpResponseBodyPart) => STATE = null)
   (  f: (Response, BaseResult) => Result[I]): Future[Result[I]] = {

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

    reqBuilder.execute(handler)
  }
}

final object Helpers extends Helpers
