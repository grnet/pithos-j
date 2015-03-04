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

package gr.grnet.pithosj.api

import java.io.{File, OutputStream}

import com.twitter.io.Buf
import com.twitter.util.Future
import gr.grnet.common.http.TResult
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.command._
import typedkey.env.immutable.Env

import scala.annotation.tailrec

/**
 * Provides the Pithos API.
 */
trait PithosApi {
  def ping(serviceInfo: ServiceInfo): Future[TResult[Unit]]

  def getAccountInfo(serviceInfo: ServiceInfo): Future[TResult[GetAccountInfoResultData]]

  def replaceAccountMeta(serviceInfo: ServiceInfo, meta: Env): Future[TResult[Unit]]

  def deleteAccountMeta(serviceInfo: ServiceInfo, metaKey: String): Future[TResult[Unit]]

  def listContainers(serviceInfo: ServiceInfo): Future[TResult[ListContainersResultData]]

  def createContainer(serviceInfo: ServiceInfo, container: String): Future[TResult[Unit]]

  def getContainerInfo(serviceInfo: ServiceInfo, container: String): Future[TResult[Unit]]

  def deleteContainer(serviceInfo: ServiceInfo, container: String): Future[TResult[Unit]]

  def createDirectory(
      serviceInfo: ServiceInfo,
      container: String,
      path: String
  ): Future[TResult[Unit]]

  def createDirectory(
    serviceInfo: ServiceInfo,
    directoryPath: String
  ): Future[TResult[Unit]] = createDirectory(serviceInfo, "", directoryPath)

  def getObjectMeta(serviceInfo: ServiceInfo, path: String): Future[TResult[Unit]]

  def deleteObjectMeta(
      serviceInfo: ServiceInfo,
      path: String,
      metaKey: String
  ): Future[TResult[Unit]]

  def replaceObjectMeta(
      serviceInfo: ServiceInfo,
      path: String,
      meta: Env
  ): Future[TResult[Unit]]

  def getObject(
      serviceInfo: ServiceInfo,
      container: String,
      path: String,
      version: String,
      out: OutputStream
  ): Future[TResult[GetObjectResultData]]

  def getObject(
    serviceInfo: ServiceInfo,
    objectPath: String,
    version: String,
    out: OutputStream
  ): Future[TResult[GetObjectResultData]] = getObject(serviceInfo, "", objectPath, version, out)

  def getObject2(
    serviceInfo: ServiceInfo,
    container: String,
    path: String,
    version: String
  ): Future[TResult[GetObject2ResultData]]

  def getObject2(
    serviceInfo: ServiceInfo,
    objectPath: String,
    version: String
  ): Future[TResult[GetObject2ResultData]] = getObject2(serviceInfo, "", objectPath, version)

  def getObjectInfo(
      serviceInfo: ServiceInfo,
      container: String,
      path: String
  ): Future[TResult[GetObjectInfoResultData]]

  def putObject(
      serviceInfo: ServiceInfo,
      container: String,
      path: String,
      file: File,
      contentType: String
  ): Future[TResult[Unit]]

  def putObject(
    serviceInfo: ServiceInfo,
    container: String,
    path: String,
    bytes: Array[Byte],
    contentType: String
  ): Future[TResult[Unit]]

  def putObject(
    serviceInfo: ServiceInfo,
    container: String,
    path: String,
    payload: Buf,
    contentType: String
  ): Future[TResult[Unit]]

  def putObject(
    serviceInfo: ServiceInfo,
    objectPath: String,
    payload: Buf,
    contentType: String
  ): Future[TResult[Unit]] = putObject(serviceInfo, "", objectPath, payload, contentType)

  def putObject(
    serviceInfo: ServiceInfo,
    objectPath: String,
    payload: File,
    contentType: String
  ): Future[TResult[Unit]] = putObject(serviceInfo, "", objectPath, payload, contentType)

  def putObject(
    serviceInfo: ServiceInfo,
    objectPath: String,
    payload: Array[Byte],
    contentType: String
  ): Future[TResult[Unit]] = putObject(serviceInfo, "", objectPath, payload, contentType)

  /**
   * Delete a file.
   */
  def deleteFile(
      serviceInfo: ServiceInfo,
      container: String,
      path: String
  ): Future[TResult[Unit]]

  def deleteFile(
    serviceInfo: ServiceInfo,
    filePath: String
  ): Future[TResult[Unit]] = deleteFile(serviceInfo, "", filePath)

  /**
   * Delete a directory.
   */
  def deleteDirectory(
    serviceInfo: ServiceInfo,
    container: String,
    path: String
  ): Future[TResult[Unit]]

  def deleteDirectory(
    serviceInfo: ServiceInfo,
    directoryPath: String
  ): Future[TResult[Unit]] = deleteDirectory(serviceInfo, "", directoryPath)

  def copyObject(
      serviceInfo: ServiceInfo,
      fromContainer: String,
      fromPath: String,
      toContainer: String,
      toPath: String
  ): Future[TResult[Unit]]

  def moveObject(
      serviceInfo: ServiceInfo,
      fromContainer: String,
      fromPath: String,
      toContainer: String,
      toPath: String
  ): Future[TResult[Unit]]

  def listObjectsInContainer(
      serviceInfo: ServiceInfo,
      container: String
  ): Future[TResult[ListObjectsInPathResultData]]

  def listObjectsInPath(
      serviceInfo: ServiceInfo,
      container: String,
      path: String
  ): Future[TResult[ListObjectsInPathResultData]]

  def listObjectsInPath(
    serviceInfo: ServiceInfo,
    path: String
  ): Future[TResult[ListObjectsInPathResultData]] = listObjectsInPath(serviceInfo, "", path)

  def checkExistsObject(
    serviceInfo: ServiceInfo,
    container: String,
    path: String
  ): Future[TResult[CheckExistsObjectResultData]]

  def checkExistsContainer(
    serviceInfo: ServiceInfo,
    container: String
  ): Future[TResult[CheckExistsObjectResultData]] = checkExistsObject(serviceInfo, container, "")
}

object PithosApi {
  @tailrec
  final def normalizeObjectPath(p: String): String =
    if(p.startsWith("/")) normalizeObjectPath(p.substring(1)) else p

  @tailrec
  final def normalizeDirectoryPath(p: String): String =
    if(p.startsWith("/")) normalizeDirectoryPath(p.substring(1)) else p

  final def splitToContainerAndPath(p: String): (String, String) = {
    val i = p.indexOf('/')
    val container = p.substring(0, i)
    val path = p.substring(i + 1)

    (container, path)
  }
}
