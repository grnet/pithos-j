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

package gr.grnet.pithosj.core.command

import gr.grnet.common.http.{StdContentType, Method}
import gr.grnet.common.keymap.KeyMap
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.keymap.PithosHeaderKeys

/**
 * Checks that a directory exists.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
case class CheckExistsObjectCommand(
  serviceInfo: ServiceInfo,
  container: String,
  path: String,
  contentTypeIsDirectory: (String) ⇒ Boolean = CheckExistsObjectCommand.StdContentTypeIsDirectory
) extends PithosCommandSkeleton[CheckExistsObjectResultData] {
  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod = Method.HEAD

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  // Note that if path == "" then for the container the server will send 204 instead of 200
  // Note how 404 is actually a success status code, indicating a "false" to the original question
  def successCodes = Set(200, 204, 404)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverURLPathElements = Seq(serviceInfo.uuid, container, path)

  override def buildResultData(
    responseHeaders: KeyMap, statusCode: Int, statusText: String, startMillis: Long, stopMillis: Long,
    getResponseBody: () => String
  ) = {
    val exists = statusCode != 404
    val isContainer = statusCode == 204 && path.isEmpty
    CheckExistsObjectResultData(
      exists = exists,
      isContainer = isContainer,
      container = container,
      path = path,
      contentType = if(exists && !isContainer) responseHeaders.get(PithosHeaderKeys.Standard.Content_Type) else None,
      contentTypeIsDirectory = contentTypeIsDirectory
    )
  }
}

object CheckExistsObjectCommand {
  final val StdContentTypeIsDirectory: (String) ⇒ Boolean = contentType ⇒ {
    StdContentType.Application_Directory.is(contentType) ||
    StdContentType.Application_Folder.is(contentType) ||
    contentType.startsWith(StdContentType.Application_Directory.contentType()) ||
    contentType.startsWith(StdContentType.Application_Folder.contentType())
  }
}