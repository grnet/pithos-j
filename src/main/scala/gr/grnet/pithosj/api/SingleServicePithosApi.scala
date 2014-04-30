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
  def deleteObject(container: String, path: String) = pithos.deleteFile(serviceInfo, container, path)

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
