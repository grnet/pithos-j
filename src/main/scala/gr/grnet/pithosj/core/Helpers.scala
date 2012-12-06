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
import com.ning.http.client.{ListenableFuture, AsyncCompletionHandler, AsyncHttpClient, Response}
import java.util.concurrent.Future

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final object Helpers {
  @inline final def copyResponseHeader(header: String, response: Response, meta: MetaData) {
    meta.set(header, response.getHeader(header))
  }

  final def copyPithosResponseHeaders(response: Response, meta: MetaData) {
    copyResponseHeader(Headers.Pithos.X_Account_Bytes_Used, response, meta)
    copyResponseHeader(Headers.Pithos.X_Account_Container_Count, response, meta)
    copyResponseHeader(Headers.Pithos.X_Account_Policy_Quota, response, meta)
    copyResponseHeader(Headers.Pithos.X_Account_Policy_Versioning, response, meta)
  }

  final def prepareHead(http: AsyncHttpClient, connInfo: ConnectionInfo, paths: String*) = {
    val reqBuilder = http.prepareGet(Paths.buildWithFirst(connInfo.baseURL, paths:_*))
    reqBuilder.addHeader(Headers.Pithos.X_Auth_Token, connInfo.userToken)
    reqBuilder
  }

  final def execAsyncCompletionHandler[Result](
      reqBuilder: AsyncHttpClient#BoundRequestBuilder
  )(  f: Response => Result): Future[Result] = {
    val handler = new AsyncCompletionHandler[Result] {
      def onCompleted(response: Response) = f(response)
    }

    reqBuilder.execute(handler)
  }
}
