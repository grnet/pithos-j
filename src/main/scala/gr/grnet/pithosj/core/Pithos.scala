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

import java.io.InputStream
import java.util.concurrent.Future
import gr.grnet.pithosj.core.result.{ListContainersResult, AccountInfoResult, MetaDataResult}


/**
 * Provides the Pithos API.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait Pithos {
  def ping(connInfo: ConnectionInfo): Future[MetaDataResult]

  def getAccountInfo(connInfo: ConnectionInfo): Future[AccountInfoResult]

  def replaceAccountMeta(connInfo: ConnectionInfo, meta: MetaData): Future[MetaDataResult]

  def deleteAccountMeta(connInfo: ConnectionInfo, metaKey: String): Future[MetaDataResult]

  def listContainers(connInfo: ConnectionInfo): Future[ListContainersResult]

  def createContainer(connInfo: ConnectionInfo, container: String): Future[MetaDataResult]

  def getContainerInfo(connInfo: ConnectionInfo, container: String): Future[MetaDataResult]

  def deleteContainer(connInfo: ConnectionInfo, container: String): Future[MetaDataResult]

  def createDirectory(connInfo: ConnectionInfo, directory: String):Future[MetaDataResult]

  def getObjectMeta(connInfo: ConnectionInfo, obj: String): Future[MetaDataResult]

  def deleteObjectMeta(connInfo: ConnectionInfo, obj: String, metaKey: String): Future[MetaDataResult]

  def replaceObjectMeta(connInfo: ConnectionInfo, obj: String, meta: MetaData): Future[MetaDataResult]

  def getObjectInfo(connInfo: ConnectionInfo, obj: String): Future[MetaDataResult]

  def getObject(connInfo: ConnectionInfo, obj: String): Future[MetaDataResult]

  def uploadObject(connInfo: ConnectionInfo, obj: String, in: InputStream, size: Long): Future[MetaDataResult]

  def deleteObject(connInfo: ConnectionInfo, obj: String): Future[MetaDataResult]

  def copyObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String): Future[MetaDataResult]

  def moveObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String): Future[MetaDataResult]

  def listObjects(connInfo: ConnectionInfo): Future[MetaDataResult]

  def listObjectsInPath(connInfo: ConnectionInfo, pathPrefix: String)
}
