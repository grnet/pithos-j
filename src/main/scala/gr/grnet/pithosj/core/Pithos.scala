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

package gr.grnet.pithosj.core

import gr.grnet.pithosj.core.command.result.Result
import gr.grnet.pithosj.core.keymap.KeyMap
import java.io.{File, OutputStream}
import scala.concurrent.Future

/**
 * Provides the Pithos API.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait Pithos {
  def ping(serviceInfo: ServiceInfo): Future[Result]

  def getAccountInfo(serviceInfo: ServiceInfo): Future[Result]

  def replaceAccountMeta(serviceInfo: ServiceInfo, meta: KeyMap): Future[Result]

  def deleteAccountMeta(serviceInfo: ServiceInfo, metaKey: String): Future[Result]

  def listContainers(serviceInfo: ServiceInfo): Future[Result]

  def createContainer(serviceInfo: ServiceInfo, container: String): Future[Result]

  def getContainerInfo(serviceInfo: ServiceInfo, container: String): Future[Result]

  def deleteContainer(serviceInfo: ServiceInfo, container: String): Future[Result]

  def createDirectory(
      serviceInfo: ServiceInfo,
      container: String,
      path: String
  ): Future[Result]

  def getObjectMeta(serviceInfo: ServiceInfo, path: String): Future[Result]

  def deleteObjectMeta(
      serviceInfo: ServiceInfo,
      path: String,
      metaKey: String
  ): Future[Result]

  def replaceObjectMeta(
      serviceInfo: ServiceInfo,
      path: String,
      meta: KeyMap
  ): Future[Result]

  def getObject(
      serviceInfo: ServiceInfo,
      container: String,
      path: String,
      version: String,
      out: OutputStream
  ): Future[Result]

  def getObjectInfo(
      serviceInfo: ServiceInfo,
      container: String,
      path: String
  ): Future[Result]

  def putObject(
      serviceInfo: ServiceInfo,
      container: String,
      path: String,
      in: File,
      contentType: String
  ): Future[Result]

  /**
   * Delete a file or folder.
   */
  def deleteObject(
      serviceInfo: ServiceInfo,
      container: String,
      path: String
  ): Future[Result]

  def copyObject(
      serviceInfo: ServiceInfo,
      fromContainer: String,
      fromPath: String,
      toContainer: String,
      toPath: String
  ): Future[Result]

  def moveObject(
      serviceInfo: ServiceInfo,
      fromContainer: String,
      fromPath: String,
      toContainer: String,
      toPath: String
  ): Future[Result]

  def listObjectsInContainer(
      serviceInfo: ServiceInfo,
      container: String
  ): Future[Result]

  def listObjectsInPath(
      serviceInfo: ServiceInfo,
      container: String,
      path: String
  ): Future[Result]
}
