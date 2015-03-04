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

import com.twitter.finagle.httpx.Method.Get
import com.twitter.finagle.httpx.{Response, Status}
import gr.grnet.common.date.DateParsers
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.command.result.ObjectInPathData
import gr.grnet.pithosj.core.http.ResponseFormats
import gr.grnet.pithosj.core.keymap.{PithosHeaderKeys, PithosRequestParamKeys, PithosResultKeys}

import scala.xml.XML

case class ListObjectsInPathCommand(
  serviceInfo: ServiceInfo,
  container: String,
  path: String
) extends PithosCommandSkeleton[ListObjectsInPathResultData] {
  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod = Get

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  def successStatuses = Set(200).map(Status.fromCode)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverRootPathElements = Seq(serviceInfo.rootPath, serviceInfo.uuid, container)

  override def queryParameters =
    Map(
      PithosRequestParamKeys.Format.name → ResponseFormats.XML.responseFormat(),
      PithosRequestParamKeys.Path.name   → path
    )

  override val responseHeaderKeys = Seq(
    PithosHeaderKeys.Pithos.X_Container_Block_Hash,
    PithosHeaderKeys.Pithos.X_Container_Block_Size,
    PithosHeaderKeys.Pithos.X_Container_Object_Meta,
    PithosHeaderKeys.Pithos.X_Container_Object_Count,
    PithosHeaderKeys.Pithos.X_Container_Bytes_Used
  )

  override val resultDataKeys = Seq(
    PithosResultKeys.ListObjectsInPath
  )

  def buildResultData(response: Response, startMillis: Long, stopMillis: Long): ListObjectsInPathResultData = {
    val body = response.contentString
    val xml = XML.loadString(body)

    val objectsInPath = for {
      obj   <- xml \ "object"
      hash  <- obj \ "hash"
      name  <- obj \ "name"
      bytes <- obj \ "bytes"
      x_object_version_timestamp <- obj \ "x_object_version_timestamp"
      x_object_uuid <- obj \ "x_object_uuid"
      last_modified <- obj \ "last_modified"
      content_type  <- obj \ "content_type"
      x_object_hash <- obj \ "x_object_hash"
      x_object_version     <- obj \ "x_object_version"
      x_object_modified_by <- obj \ "x_object_modified_by"
    } yield {
      ObjectInPathData(
        container = container,
        path = name.text,
        contentType = content_type.text,
        contentLength = bytes.text.toLong,
        lastModified = DateParsers.parse(last_modified.text, DateParsers.Format1Parser),
        xObjectHash = x_object_hash.text,
        xObjectModifiedBy = x_object_modified_by.text,
        xObjectVersionTimestamp = DateParsers.parse(x_object_version_timestamp.text, DateParsers.Format3Parser),
        xObjectUUID = x_object_uuid.text,
        xObjectVersion = x_object_version.text,
        eTag = None
      )
    }

    ListObjectsInPathResultData(
      objects = objectsInPath
    )
  }
}
