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

package gr.grnet.pithosj.core

import java.io.{File, OutputStream}
import java.net.URLConnection

import gr.grnet.common.http.{BytesRequestBody, FileRequestBody, TResult}
import gr.grnet.common.keymap.KeyMap
import gr.grnet.pithosj.api.PithosApi
import gr.grnet.pithosj.core.command._
import gr.grnet.pithosj.core.http.ChannelBufferRequestBody
import org.jboss.netty.buffer.ChannelBuffer

import scala.concurrent.{ExecutionContext, Future}

/**
 * Skeleton implementation of [[gr.grnet.pithosj.api.PithosApi]].
 * Concrete implementations are required to provide an instance of [[gr.grnet.pithosj.core.command.CommandExecutor]]
 * as the value of `executor`.
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
