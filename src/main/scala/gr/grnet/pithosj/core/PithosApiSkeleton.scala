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

import com.twitter.io.Buf
import com.twitter.logging.Logger
import com.twitter.util.Future
import gr.grnet.common.http.TResult
import gr.grnet.common.io.BufHelpers
import gr.grnet.pithosj.api.PithosApi
import gr.grnet.pithosj.core.command._
import typedkey.env.immutable.Env

/**
 * Skeleton implementation of [[gr.grnet.pithosj.api.PithosApi]].
 */
trait PithosApiSkeleton extends PithosApi {
  val log = Logger(getClass)

  protected def call[T](command: PithosCommand[T]): Future[TResult[T]] = {
    try {
      command.validate match {
        case Some(error) ⇒
          val name = command.commandName
          Future.rawException(new RuntimeException(s"Could not validate $name: $error"))

        case None ⇒
          callImpl(command)
      }
    }
    catch {
      case e: Throwable ⇒
        Future.rawException(new RuntimeException("Internal error", e))
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

  def createDirectory(
    serviceInfo: ServiceInfo,
    _container: String,
    _path: String
  ) = {
    val (container, path) = PithosApi.containerAndPath(_container, PithosApi.normalizeDirectoryPath(_path))
    call(CreateDirectoryCommand(serviceInfo, container, path))
  }

  def getObjectMeta(serviceInfo: ServiceInfo, path: String) = ???

  def deleteObjectMeta(serviceInfo: ServiceInfo, path: String, metaKey: String) = ???

  def replaceObjectMeta(serviceInfo: ServiceInfo, path: String, meta: Env) = ???

  def getObject(
    serviceInfo: ServiceInfo,
    _container: String,
    _path: String,
    version: String,
    out: OutputStream
  ) = {
    val (container, path) = PithosApi.containerAndPath(_container, PithosApi.normalizeDirectoryPath(_path))
    call(GetObjectCommand(serviceInfo, container, path, version, out))
  }

  def getObject2(
    serviceInfo: ServiceInfo,
    _container: String,
    _path: String,
    version: String
  ): Future[TResult[GetObject2ResultData]] = {
    val (container, path) = PithosApi.containerAndPath(_container, PithosApi.normalizeDirectoryPath(_path))
    call(GetObject2Command(serviceInfo, container, path, version))
  }

  def getObjectInfo(
    serviceInfo: ServiceInfo,
    _container: String,
    _path: String
  ) = {
    val (container, path) = PithosApi.containerAndPath(_container, PithosApi.normalizeDirectoryPath(_path))
    call(GetObjectInfoCommand(serviceInfo, container, path))
  }

  def putObject(
    serviceInfo: ServiceInfo,
    _container: String,
    _path: String,
    file: File,
    _contentType: String
  ) = {
    val (container, path) = PithosApi.containerAndPath(_container, PithosApi.normalizeDirectoryPath(_path))
    val contentType = _contentType match {
      case null | "" ⇒ PithosApi.guessContentTypeFromPath(path)
      case _ ⇒ _contentType
    }

    call(PutObjectCommand(serviceInfo, container, path, BufHelpers.bufOfFile(file), contentType))
  }

  def putObject(
    serviceInfo: ServiceInfo,
    _container: String,
    _path: String,
    bytes: Array[Byte],
    _contentType: String
  ) = {
    val (container, path) = PithosApi.containerAndPath(_container, PithosApi.normalizeDirectoryPath(_path))
    val contentType = _contentType match {
      case null | "" ⇒ PithosApi.guessContentTypeFromPath(path)
      case _ ⇒ _contentType
    }
    call(PutObjectCommand(serviceInfo, container, path, Buf.ByteArray.Owned(bytes), contentType))
  }

  def putObject(
    serviceInfo: ServiceInfo,
    _container: String,
    _path: String,
    payload: Buf,
    _contentType: String
  ) = {
    val (container, path) = PithosApi.containerAndPath(_container, PithosApi.normalizeDirectoryPath(_path))
    val contentType = _contentType match {
      case null | "" ⇒ PithosApi.guessContentTypeFromPath(path)
      case _ ⇒ _contentType
    }
    call(PutObjectCommand(serviceInfo, container, path, payload, contentType))
  }

  def deleteFile(
    serviceInfo: ServiceInfo,
    _container: String,
    _path: String
  ) = {
    val (container, path) = PithosApi.containerAndPath(_container, PithosApi.normalizeDirectoryPath(_path))
    call(DeleteFileCommand(serviceInfo, container, path))
  }

  def deleteDirectory(
    serviceInfo: ServiceInfo,
    _container: String,
    _path: String
  ) = {
    val (container, path) = PithosApi.containerAndPath(_container, PithosApi.normalizeDirectoryPath(_path))
    call(DeleteDirectoryCommand(serviceInfo, container, path))
  }

  def copyObject(
    serviceInfo: ServiceInfo,
    _fromContainer: String,
    _fromPath: String,
    _toContainer: String,
    _toPath: String
  ) = {
    val (fromContainer, fromPath) = PithosApi.containerAndPath(_fromContainer, PithosApi.normalizeDirectoryPath(_fromPath))
    val (toContainer,     toPath) = PithosApi.containerAndPath(_toContainer,   PithosApi.normalizeDirectoryPath(_toPath))

    call(
      CopyObjectCommand(serviceInfo, fromContainer, fromPath, toContainer, toPath)
    )
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
    _container: String,
    _path: String
  ) = {
    val (container, path) = PithosApi.containerAndPath(_container, PithosApi.normalizeDirectoryPath(_path))
    call(ListObjectsInPathCommand(serviceInfo, container, path))
  }

  def checkExistsObject(
    serviceInfo: ServiceInfo,
    _container: String,
    _path: String
  ) = {
    val (container, path) = PithosApi.containerAndPath(_container, PithosApi.normalizeDirectoryPath(_path))
    call(CheckExistsObjectCommand(serviceInfo, container, path))
  }
}
