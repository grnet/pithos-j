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

package gr.grnet.pithosj.api

import gr.grnet.common.keymap.KeyMap
import gr.grnet.pithosj.core.ServiceInfo
import java.io.{File, OutputStream}

/**
 * Provides the Pithos API.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
class SingleServicePithosApi(serviceInfo: ServiceInfo, pithos: PithosApi) {
  def ping() = pithos.ping(serviceInfo)

  def getAccountInfo() = pithos.getAccountInfo(serviceInfo)

  def replaceAccountMeta(meta: KeyMap) = pithos.replaceAccountMeta(serviceInfo, meta)

  def deleteAccountMeta(metaKey: String) = pithos.deleteAccountMeta(serviceInfo, metaKey)

  def listContainers() = pithos.listContainers(serviceInfo)

  def createContainer(container: String) = pithos.createContainer(serviceInfo, container)

  def getContainerInfo(container: String) = pithos.getContainerInfo(serviceInfo, container)

  def deleteContainer(container: String) = pithos.deleteContainer(serviceInfo, container)

  def createDirectory(container: String, path: String) = pithos.createDirectory(serviceInfo, container, path)

  def getObjectMeta(path: String) = pithos.getObjectMeta(serviceInfo, path)

  def deleteObjectMeta(path: String, metaKey: String) = pithos.deleteObjectMeta(serviceInfo, path, metaKey)

  def replaceObjectMeta(path: String, meta: KeyMap) = pithos.replaceObjectMeta(serviceInfo, path, meta)

  def getObject(
    container: String,
    path: String,
    version: String,
    out: OutputStream
  ) = pithos.getObject(serviceInfo, container, path, version, out)

  def getObjectInfo(container: String, path: String) = pithos.getObjectInfo(serviceInfo, container, path)

  def putObject(
    container: String,
    path: String,
    in: File,
    contentType: String
  ) = pithos.putObject(serviceInfo, container, path, in, contentType)

  /**
   * Delete a file or folder.
   */
  def deleteObject(container: String, path: String) = pithos.deleteObject(serviceInfo, container, path)

  def copyObject(
    fromContainer: String,
    fromPath: String,
    toContainer: String,
    toPath: String
  ) = pithos.copyObject(serviceInfo, fromContainer, fromPath, toContainer, toPath)

  def moveObject(
    fromContainer: String,
    fromPath: String,
    toContainer: String,
    toPath: String
  ) = pithos.moveObject(serviceInfo, fromContainer, fromPath, toContainer, toPath)

  def listObjectsInContainer(container: String) = pithos.listObjectsInContainer(serviceInfo, container)

  def listObjectsInPath(container: String, path: String) = pithos.listObjectsInPath(serviceInfo, container, path)
}
