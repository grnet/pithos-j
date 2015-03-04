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

import com.twitter.io.Buf
import com.twitter.util.Future
import gr.grnet.common.http.TResult
import gr.grnet.common.io.BufHelpers
import gr.grnet.pithosj.api.PithosApi
import gr.grnet.pithosj.core.command._
import typedkey.env.immutable.Env

/**
 * Skeleton implementation of [[gr.grnet.pithosj.api.PithosApi]].
 * Concrete implementations are required to provide an instance of [[gr.grnet.pithosj.core.command.CommandExecutor]]
 * as the value of `executor`.
 */
trait PithosApiSkeleton extends PithosApi {
  protected def call[T](command: PithosCommand[T]): Future[TResult[T]] = {
    try {
      command.validate match {
        case Some(error) ⇒
          Future.exception(new RuntimeException("Could not validate %s".format(command)))

        case None ⇒
          callImpl(command)
      }
    }
    catch {
      case e: Throwable ⇒
        Future.exception(new RuntimeException("Internal error", e))
    }
  }

  protected def callImpl[T](command: PithosCommand[T]): Future[TResult[T]]

  def ping(serviceInfo: ServiceInfo) = call(PingCommand(serviceInfo))

  def getAccountInfo(serviceInfo: ServiceInfo) = call(GetAccountInfoCommand(serviceInfo))

  def replaceAccountMeta(serviceInfo: ServiceInfo, meta: Env) = ???

  def deleteAccountMeta(serviceInfo: ServiceInfo, metaKey: String) = ???

  def listContainers(serviceInfo: ServiceInfo) = call(ListContainersCommand(serviceInfo))

  def createContainer(serviceInfo: ServiceInfo, container: String) = ???

  def getContainerInfo(serviceInfo: ServiceInfo, container: String) = ???

  def deleteContainer(serviceInfo: ServiceInfo, container: String) = ???

  def createDirectory(serviceInfo: ServiceInfo, container: String, path: String) =
    call(CreateDirectoryCommand(serviceInfo, container, PithosApi.normalizeDirectoryPath(path)))

  def getObjectMeta(serviceInfo: ServiceInfo, path: String) = ???

  def deleteObjectMeta(serviceInfo: ServiceInfo, path: String, metaKey: String) = ???

  def replaceObjectMeta(serviceInfo: ServiceInfo, path: String, meta: Env) = ???

  def getObject(serviceInfo: ServiceInfo, container: String, path: String, version: String, out: OutputStream) =
    call(GetObjectCommand(serviceInfo, container, PithosApi.normalizeObjectPath(path), version, out))

  def getObject2(
    serviceInfo: ServiceInfo,
    container: String,
    path: String,
    version: String
  ): Future[TResult[GetObject2ResultData]] =
    call(GetObject2Command(serviceInfo, container, PithosApi.normalizeObjectPath(path), version))

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

    call(PutObjectCommand(serviceInfo, container, PithosApi.normalizeObjectPath(path), BufHelpers.bufOfFile(file), contentType))
  }

  def putObject(
    serviceInfo: ServiceInfo,
    container: String,
    path: String,
    bytes: Array[Byte],
    contentType: String
  ) =
    call(PutObjectCommand(serviceInfo, container, PithosApi.normalizeObjectPath(path), Buf.ByteArray.Owned(bytes), contentType))

  def putObject(
    serviceInfo: ServiceInfo,
    container: String,
    path: String,
    payload: Buf,
    contentType: String
  ) =
    call(PutObjectCommand(serviceInfo, container, PithosApi.normalizeObjectPath(path), payload, contentType))

  def deleteFile(serviceInfo: ServiceInfo, container: String, path: String) =
    call(DeleteFileCommand(serviceInfo, container, PithosApi.normalizeObjectPath(path)))

  def deleteDirectory(serviceInfo: ServiceInfo, container: String, path: String) =
    call(DeleteDirectoryCommand(serviceInfo, container, PithosApi.normalizeDirectoryPath(path)))

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
