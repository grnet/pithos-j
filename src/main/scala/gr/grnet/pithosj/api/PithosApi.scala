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

package gr.grnet.pithosj.api

import gr.grnet.common.http.TResult
import gr.grnet.common.keymap.KeyMap
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.command.{CheckExistsObjectResultData, ListObjectsInPathResultData, GetObjectInfoResultData, GetObjectResultData, ListContainersResultData, GetAccountInfoResultData}
import java.io.{File, OutputStream}
import scala.concurrent.Future

/**
 * Provides the Pithos API.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait PithosApi {
  def ping(serviceInfo: ServiceInfo): Future[TResult[Unit]]

  def getAccountInfo(serviceInfo: ServiceInfo): Future[TResult[GetAccountInfoResultData]]

  def replaceAccountMeta(serviceInfo: ServiceInfo, meta: KeyMap): Future[TResult[Unit]]

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

  def getObjectMeta(serviceInfo: ServiceInfo, path: String): Future[TResult[Unit]]

  def deleteObjectMeta(
      serviceInfo: ServiceInfo,
      path: String,
      metaKey: String
  ): Future[TResult[Unit]]

  def replaceObjectMeta(
      serviceInfo: ServiceInfo,
      path: String,
      meta: KeyMap
  ): Future[TResult[Unit]]

  def getObject(
      serviceInfo: ServiceInfo,
      container: String,
      path: String,
      version: String,
      out: OutputStream
  ): Future[TResult[GetObjectResultData]]

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

  /**
   * Delete a file.
   */
  def deleteFile(
      serviceInfo: ServiceInfo,
      container: String,
      path: String
  ): Future[TResult[Unit]]

  /**
   * Delete a directory.
   */
  def deleteDirectory(
    serviceInfo: ServiceInfo,
    container: String,
    path: String
  ): Future[TResult[Unit]]

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

  def checkExistsObject(
    serviceInfo: ServiceInfo,
    container: String,
    path: String
  ): Future[TResult[CheckExistsObjectResultData]]
}
