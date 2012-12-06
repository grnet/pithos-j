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
import gr.grnet.pithosj.core.result.{ListContainersResult, AccountInfoResult}
import java.io.InputStream
import scala.io.Source

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final class AsyncHttpPithosClient(http: AsyncHttpClient) extends Pithos {
  def ping(connInfo: ConnectionInfo) = ???

  def getAccountInfo(connInfo: ConnectionInfo) = {
    val reqBuilder = Helpers.prepareHead(http, connInfo, connInfo.userID)

    Helpers.execAsyncCompletionHandler(reqBuilder) { (response, completionMillis) =>
      val statusCode = response.getStatusCode
      val statusText = response.getStatusText

      val meta = new MetaData
      Helpers.copyPithosResponseHeaders(response, meta)

      new AccountInfoResult(statusCode, statusText, meta, completionMillis)
    }
  }

  def replaceAccountMeta(connInfo: ConnectionInfo, meta: MetaData) = ???

  def deleteAccountMeta(connInfo: ConnectionInfo, metaKey: String) = ???

  def listContainers(connInfo: ConnectionInfo) = {
    val reqBuilder = Helpers.prepareHead(http, connInfo, connInfo.userID)

    Helpers.execAsyncCompletionHandler(reqBuilder) { (response, completionMillis) =>
      val statusCode = response.getStatusCode
      val statusText = response.getStatusText
      val body = response.getResponseBody
      val containers = Source.fromString(body).getLines().toArray

      val meta = new MetaData
      Helpers.copyPithosResponseHeaders(response, meta)

      new ListContainersResult(statusCode, statusText, meta, completionMillis, containers)
    }
  }

  def createContainer(connInfo: ConnectionInfo, container: String) = ???

  def getContainerInfo(connInfo: ConnectionInfo, container: String) = ???

  def deleteContainer(connInfo: ConnectionInfo, container: String) = ???

  def createDirectory(connInfo: ConnectionInfo, directory: String) = ???

  def getObjectMeta(connInfo: ConnectionInfo, obj: String) = ???

  def deleteObjectMeta(connInfo: ConnectionInfo, obj: String, metaKey: String) = ???

  def replaceObjectMeta(connInfo: ConnectionInfo, obj: String, meta: MetaData) = ???

  def getObjectInfo(connInfo: ConnectionInfo, obj: String) = ???

  def getObject(connInfo: ConnectionInfo, obj: String) = ???

  def uploadObject(connInfo: ConnectionInfo, obj: String, in: InputStream, size: Long) = ???

  def deleteObject(connInfo: ConnectionInfo, obj: String) = ???

  def copyObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String
  ) = ???

  def moveObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String
  ) = ???

  def listObjects(connInfo: ConnectionInfo) = ???

  def listObjectsInPath(connInfo: ConnectionInfo, pathPrefix: String) {}
}
