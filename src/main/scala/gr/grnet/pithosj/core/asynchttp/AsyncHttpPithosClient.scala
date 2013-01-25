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

package gr.grnet.pithosj.core.asynchttp

import com.ning.http.client.AsyncHttpClient
import gr.grnet.pithosj.core.command.{ListObjectsInPath, DeleteObject, PutObject, GetObjectInfo, GetObject, CreateDirectory, ListContainers, GetAccountInfo, Ping, CopyObject, Command}
import gr.grnet.pithosj.core.{???, MetaData, Helpers, ConnectionInfo, Pithos}
import java.io.{File, OutputStream}
import java.net.URLConnection
import java.util.concurrent.Future
import org.slf4j.LoggerFactory

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final class AsyncHttpPithosClient(http: AsyncHttpClient) extends Pithos {
  private[this] val logger = LoggerFactory.getLogger(this.getClass)
  private[this] val executor = new AsyncHttpCommandExecutor(http)

  def ping(connectionInfo: ConnectionInfo) = {
    call(Ping(connectionInfo))
  }

  def getAccountInfo(connectionInfo: ConnectionInfo) = {
    call(GetAccountInfo(connectionInfo))
  }

  def replaceAccountMeta(connectionInfo: ConnectionInfo, meta: MetaData) = ???

  def deleteAccountMeta(connectionInfo: ConnectionInfo, metaKey: String) = ???

  def listContainers(connectionInfo: ConnectionInfo) = {
    call(ListContainers(connectionInfo))
  }

  def createContainer(connectionInfo: ConnectionInfo, container: String) = ???

  def getContainerInfo(connectionInfo: ConnectionInfo, container: String) = ???

  def deleteContainer(connectionInfo: ConnectionInfo, container: String) = ???

  def createDirectory(connectionInfo: ConnectionInfo, container: String, path: String) = {
    call(CreateDirectory(connectionInfo, container, path))
  }

  def getObjectMeta(connectionInfo: ConnectionInfo, path: String) = ???

  def deleteObjectMeta(connectionInfo: ConnectionInfo, path: String, metaKey: String) = ???

  def replaceObjectMeta(connectionInfo: ConnectionInfo, path: String, meta: MetaData) = ???

  def getObject(connectionInfo: ConnectionInfo, container: String, path: String, version: String, out: OutputStream) = {
    call(GetObject(connectionInfo, container, path, version, out))
  }

  def getObjectInfo(connectionInfo: ConnectionInfo, container: String, path: String) = {
    call(GetObjectInfo(connectionInfo, container, path))
  }

  def putObject(
      connectionInfo: ConnectionInfo,
      container: String,
      path: String,
      file: File,
      _contentType: String
  ) = {
    val contentType = _contentType match {
      case null ⇒
        URLConnection.guessContentTypeFromName(path)
      case contentType ⇒
        contentType
    }

    call(PutObject(connectionInfo, container, path, file, contentType))
  }

  def deleteObject(connectionInfo: ConnectionInfo, container: String, path: String) = {
    call(DeleteObject(connectionInfo, container, path))
  }

  def copyObject(
      connectionInfo: ConnectionInfo,
      fromContainer: String,
      fromPath: String,
      _toContainer: String,
      _toPath: String
  ) = {
    val toPath = Helpers.ifNull(_toPath, fromPath)
    val toContainer = Helpers.ifNull(_toContainer, fromContainer)

    fromContainer.charAt(0)
    fromPath.charAt(0)
    toContainer.charAt(0)
    toPath.charAt(0)

    call(CopyObject(connectionInfo, fromContainer, fromPath, toContainer, toPath))
  }

  def moveObject(
      connectionInfo: ConnectionInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String
  ) = ???

  def listObjectsInContainer(connectionInfo: ConnectionInfo, container: String) = {
    listObjectsInPath(connectionInfo, container, "")
  }

  def listObjectsInPath(
      connectionInfo: ConnectionInfo,
      container: String,
      path: String
  ) = {
    require(path ne null, "path ne null")

    call(ListObjectsInPath(connectionInfo, container, path))
  }

  def call[R](command: Command[R]): Future[R] = {
    try {
      command.validate match {
        case Some(error) ⇒
          Helpers.knownBadFuture(error)

        case None ⇒
          executor.execute(command)
      }
    }
    catch {
      case e: Throwable ⇒
        Helpers.knownBadFuture(e, "Internal error")
    }
  }
}
