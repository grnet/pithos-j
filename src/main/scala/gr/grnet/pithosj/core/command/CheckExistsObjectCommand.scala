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

package gr.grnet.pithosj.core.command

import com.twitter.finagle.httpx.Method.Head
import com.twitter.finagle.httpx.{Response, Status}
import gr.grnet.common.http.StdMediaType
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.keymap.PithosHeaderKeys

/**
 * Checks that a directory exists.
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
  def httpMethod = Head

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  // Note that if path == "" then for the container the server will send 204 instead of 200
  // Note how 404 is actually a success status code, indicating a "false" to the original question
  def successStatuses = Set(200, 204, 404).map(Status.fromCode)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverRootPathElements = Seq(serviceInfo.rootPath, serviceInfo.uuid, container, path)

  def buildResultData(response: Response, startMillis: Long, stopMillis: Long): CheckExistsObjectResultData = {
    val status = response.status
    val statusCode = status.code
    val responseHeaders = response.headerMap
    val exists = statusCode != 404
    val isContainer = statusCode == 204 && path.isEmpty
    CheckExistsObjectResultData(
      exists = exists,
      isContainer = isContainer,
      container = container,
      path = path,
      contentType = if(exists && !isContainer) responseHeaders.get(PithosHeaderKeys.Standard.Content_Type.name) else None,
      contentTypeIsDirectory = contentTypeIsDirectory
    )
  }
}

object CheckExistsObjectCommand {
  final val StdContentTypeIsDirectory: (String) ⇒ Boolean = contentType ⇒ {
    StdMediaType.Application_Directory.is(contentType) ||
    StdMediaType.Application_Folder.is(contentType) ||
    contentType.startsWith(StdMediaType.Application_Directory.value()) ||
    contentType.startsWith(StdMediaType.Application_Folder.value())
  }
}
