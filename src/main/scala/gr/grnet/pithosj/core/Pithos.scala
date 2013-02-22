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
import java.util.concurrent.Future

/**
 * Provides the Pithos API.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait Pithos {
  def ping(connInfo: ConnectionInfo): Future[Result]

  def getAccountInfo(connInfo: ConnectionInfo): Future[Result]

  def replaceAccountMeta(connInfo: ConnectionInfo, meta: KeyMap): Future[Result]

  def deleteAccountMeta(connInfo: ConnectionInfo, metaKey: String): Future[Result]

  def listContainers(connInfo: ConnectionInfo): Future[Result]

  def createContainer(connInfo: ConnectionInfo, container: String): Future[Result]

  def getContainerInfo(connInfo: ConnectionInfo, container: String): Future[Result]

  def deleteContainer(connInfo: ConnectionInfo, container: String): Future[Result]

  def createDirectory(
      connInfo: ConnectionInfo,
      container: String,
      path: String
  ): Future[Result]

  def getObjectMeta(connInfo: ConnectionInfo, path: String): Future[Result]

  def deleteObjectMeta(
      connInfo: ConnectionInfo,
      path: String,
      metaKey: String
  ): Future[Result]

  def replaceObjectMeta(
      connInfo: ConnectionInfo,
      path: String,
      meta: KeyMap
  ): Future[Result]

  def getObject(
      connInfo: ConnectionInfo,
      container: String,
      path: String,
      version: String,
      out: OutputStream
  ): Future[Result]

  def getObjectInfo(
      connInfo: ConnectionInfo,
      container: String,
      path: String
  ): Future[Result]

  def putObject(
      connInfo: ConnectionInfo,
      container: String,
      path: String,
      in: File,
      contentType: String
  ): Future[Result]

  /**
   * Delete a file or folder.
   */
  def deleteObject(
      connInfo: ConnectionInfo,
      container: String,
      path: String
  ): Future[Result]

  def copyObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromPath: String,
      toContainer: String,
      toPath: String
  ): Future[Result]

  def moveObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromPath: String,
      toContainer: String,
      toPath: String
  ): Future[Result]

  def listObjectsInContainer(
      connInfo: ConnectionInfo,
      container: String
  ): Future[Result]

  def listObjectsInPath(
      connInfo: ConnectionInfo,
      container: String,
      path: String
  ): Future[Result]
}
