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
import com.ning.http.client.{Response, AsyncCompletionHandler, AsyncHttpClient}
import gr.grnet.pithosj.core.result.{AccountInfoResult}
import java.io.InputStream

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final class AsyncHttpPithosClient(http: AsyncHttpClient) extends Pithos {
  def ping(connInfo: ConnectionInfo) = null

  def getAccountInfo(connInfo: ConnectionInfo) = {
    val getBuilder = http.prepareHead(Paths.build(connInfo.baseURL, connInfo.userID))
    getBuilder.addHeader(Headers.Pithos.X_Auth_Token, connInfo.userToken)
    val startMillis = System.currentTimeMillis()
    getBuilder.execute(new AsyncCompletionHandler[AccountInfoResult] {
      def onCompleted(response: Response) = {
        val completionMillis = System.currentTimeMillis() - startMillis
        val headers = response.getHeaders
        val keys = headers.keySet().iterator()
        while(keys.hasNext) {
          val key = keys.next()
          System.out.println("%s: %s".format(key, headers.getJoinedValue(key, ",")))
        }

        val body = response.getResponseBody
        System.out.println("")
        System.out.println("===================================")
        System.out.println(body)
        System.out.println("===================================")

        val statusCode = response.getStatusCode
        val statusText = response.getStatusText
        System.out.println("")
        System.out.println("===================================")
        System.out.println("%s %s".format(statusCode, statusText))
        System.out.println("Completion time: %s ms".format(completionMillis))
        System.out.println("===================================")

        val meta = new MetaData
        Helpers.copyResponseHeaders(response, meta)

        new AccountInfoResult(statusCode, statusText, meta, completionMillis)
      }
    })
  }

  def replaceAccountMeta(connInfo: ConnectionInfo, meta: MetaData) = null

  def deleteAccountMeta(connInfo: ConnectionInfo, metaKey: String) = null

  def listContainers(connInfo: ConnectionInfo) = null

  def createContainer(connInfo: ConnectionInfo, container: String) = null

  def getContainerInfo(connInfo: ConnectionInfo, container: String) = null

  def deleteContainer(connInfo: ConnectionInfo, container: String) = null

  def createDirectory(connInfo: ConnectionInfo, directory: String) = null

  def getObjectMeta(connInfo: ConnectionInfo, obj: String) = null

  def deleteObjectMeta(connInfo: ConnectionInfo, obj: String, metaKey: String) = null

  def replaceObjectMeta(connInfo: ConnectionInfo, obj: String, meta: MetaData) = null

  def getObjectInfo(connInfo: ConnectionInfo, obj: String) = null

  def getObject(connInfo: ConnectionInfo, obj: String) = null

  def uploadObject(connInfo: ConnectionInfo, obj: String, in: InputStream, size: Long) = null

  def deleteObject(connInfo: ConnectionInfo, obj: String) = null

  def copyObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String
  ) = null

  def moveObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String
  ) = null

  def listObjects(connInfo: ConnectionInfo) = null

  def listObjectsInPath(connInfo: ConnectionInfo, pathPrefix: String) {}
}
