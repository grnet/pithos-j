/*
 * Copyright 2012-2014 GRNET S.A. All rights reserved.
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

import gr.grnet.common.http.TResult
import gr.grnet.common.keymap.KeyMap
import gr.grnet.pithosj.api.PithosApi
import gr.grnet.pithosj.core.command._
import gr.grnet.pithosj.core.http.{ChannelBufferRequestBody, BytesRequestBody, FileRequestBody}
import java.io.{File, OutputStream}
import java.net.URLConnection
import org.jboss.netty.buffer.ChannelBuffer
import scala.Some
import scala.concurrent.{ExecutionContext, Future}

/**
 * Skeleton implementation of [[gr.grnet.pithosj.api.PithosApi]].
 * Concrete implementations are required to provide an instance of [[gr.grnet.pithosj.core.command.CommandExecutor]]
 * as the value of `executor`.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait PithosSkeleton extends PithosApi {
  protected val executor: CommandExecutor
  protected implicit val context: ExecutionContext = ExecutionContext.Implicits.global

  protected def call[T](command: PithosCommand[T]): Future[TResult[T]] = {
    try {
      command.validate match {
        case Some(error) ⇒
          Future.failed(new RuntimeException("Could not validate %s".format(command)))

        case None ⇒
          executor.execute(command)
      }
    }
    catch {
      case e: Throwable ⇒
        Future.failed(e)
    }
  }

  def ping(serviceInfo: ServiceInfo) = call(PingCommand(serviceInfo))

  def getAccountInfo(serviceInfo: ServiceInfo) = call(GetAccountInfoCommand(serviceInfo))

  def replaceAccountMeta(serviceInfo: ServiceInfo, meta: KeyMap) = ???

  def deleteAccountMeta(serviceInfo: ServiceInfo, metaKey: String) = ???

  def listContainers(serviceInfo: ServiceInfo) = call(ListContainersCommand(serviceInfo))

  def createContainer(serviceInfo: ServiceInfo, container: String) = ???

  def getContainerInfo(serviceInfo: ServiceInfo, container: String) = ???

  def deleteContainer(serviceInfo: ServiceInfo, container: String) = ???

  def createDirectory(serviceInfo: ServiceInfo, container: String, path: String) =
    call(CreateDirectoryCommand(serviceInfo, container, path))

  def getObjectMeta(serviceInfo: ServiceInfo, path: String) = ???

  def deleteObjectMeta(serviceInfo: ServiceInfo, path: String, metaKey: String) = ???

  def replaceObjectMeta(serviceInfo: ServiceInfo, path: String, meta: KeyMap) = ???

  def getObject(serviceInfo: ServiceInfo, container: String, path: String, version: String, out: OutputStream) =
    call(GetObjectCommand(serviceInfo, container, path, version, out))

  def getObjectInfo(serviceInfo: ServiceInfo, container: String, path: String) =
    call(GetObjectInfoCommand(serviceInfo, container, path))

  def putObject(
      serviceInfo: ServiceInfo,
      container: String,
      path: String,
      file: File,
      _contentType: String
  ) = {
    val contentType = _contentType match {
      case null ⇒
        URLConnection.guessContentTypeFromName(path)

      case _ ⇒
        _contentType
    }

    call(PutObjectCommand(serviceInfo, container, path, FileRequestBody(file), contentType))
  }

  def putObject(
    serviceInfo: ServiceInfo,
    container: String,
    path: String,
    bytes: Array[Byte],
    contentType: String
  ) =
    call(PutObjectCommand(serviceInfo, container, path, BytesRequestBody(bytes), contentType))

  def putObject(
    serviceInfo: ServiceInfo,
    container: String,
    path: String,
    buffer: ChannelBuffer,
    contentType: String
  ) =
    call(PutObjectCommand(serviceInfo, container, path, ChannelBufferRequestBody(buffer), contentType))

  def deleteFile(serviceInfo: ServiceInfo, container: String, path: String) =
    call(DeleteFileCommand(serviceInfo, container, path))

  def deleteDirectory(serviceInfo: ServiceInfo, container: String, path: String) =
    call(DeleteDirectoryCommand(serviceInfo, container, path))

  def copyObject(
      serviceInfo: ServiceInfo,
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

    call(CopyObjectCommand(serviceInfo, fromContainer, fromPath, toContainer, toPath))
  }

  def moveObject(
      serviceInfo: ServiceInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String
  ) = ???

  def listObjectsInContainer(serviceInfo: ServiceInfo, container: String) =
    listObjectsInPath(serviceInfo, container, "")

  def listObjectsInPath(
      serviceInfo: ServiceInfo,
      container: String,
      path: String
  ) = {
    require(path ne null, "path ne null")

    call(ListObjectsInPathCommand(serviceInfo, container, path))
  }

  def checkExistsObject(
    serviceInfo: ServiceInfo,
    container: String,
    path: String
  ) =
    call(CheckExistsObjectCommand(serviceInfo, container, path))
}
