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

import gr.grnet.pithosj.core.result.Result
import gr.grnet.pithosj.core.result.info.{ObjectInfo, ContainersInfo, AccountInfo, NoInfo}
import java.io.{OutputStream, InputStream}
import java.util.concurrent.Future

/**
 * Provides the Pithos API.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait Pithos {
  def ping(connInfo: ConnectionInfo): Future[Result[NoInfo]]

  def getAccountInfo(connInfo: ConnectionInfo): Future[Result[AccountInfo]]

  def replaceAccountMeta(connInfo: ConnectionInfo, meta: MetaData): Future[Result[NoInfo]]

  def deleteAccountMeta(connInfo: ConnectionInfo, metaKey: String): Future[Result[NoInfo]]

  def listContainers(connInfo: ConnectionInfo): Future[Result[ContainersInfo]]

  def createContainer(connInfo: ConnectionInfo, container: String): Future[Result[NoInfo]]

  def getContainerInfo(connInfo: ConnectionInfo, container: String): Future[Result[NoInfo]]

  def deleteContainer(connInfo: ConnectionInfo, container: String): Future[Result[NoInfo]]

  def createDirectory(connInfo: ConnectionInfo, directory: String): Future[Result[NoInfo]]

  def getObjectMeta(connInfo: ConnectionInfo, obj: String): Future[Result[NoInfo]]

  def deleteObjectMeta(connInfo: ConnectionInfo, obj: String, metaKey: String): Future[Result[NoInfo]]

  def replaceObjectMeta(connInfo: ConnectionInfo, obj: String, meta: MetaData): Future[Result[NoInfo]]

  def getObject(connInfo: ConnectionInfo, container: String, obj: String, version: String, out: OutputStream): Future[Result[ObjectInfo]]

  def getObjectInfo(connInfo: ConnectionInfo, container: String, obj: String): Future[Result[ObjectInfo]]

  def uploadObject(connInfo: ConnectionInfo, obj: String, in: InputStream, size: Long): Future[Result[NoInfo]]

  def deleteObject(connInfo: ConnectionInfo, obj: String): Future[Result[NoInfo]]

  def copyObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String): Future[Result[NoInfo]]

  def moveObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String): Future[Result[NoInfo]]

  def listObjects(connInfo: ConnectionInfo): Future[Result[NoInfo]]

  def listObjectsInPath(connInfo: ConnectionInfo, pathPrefix: String)
}
