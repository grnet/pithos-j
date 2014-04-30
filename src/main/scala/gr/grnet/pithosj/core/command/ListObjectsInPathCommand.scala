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

import gr.grnet.common.date.DateParsers
import gr.grnet.common.http.Method
import gr.grnet.common.keymap.KeyMap
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.command.result.ObjectInPathData
import gr.grnet.pithosj.core.http.ResponseFormats
import gr.grnet.pithosj.core.keymap.{PithosHeaderKeys, PithosResultKeys, PithosRequestParamKeys}
import scala.xml.XML

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
case class ListObjectsInPathCommand(
    serviceInfo: ServiceInfo,
    container: String,
    path: String
) extends PithosCommandSkeleton[ListObjectsInPathResultData] {
  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod = Method.GET

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  def successCodes = Set(200)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverURLPathElements = Seq(serviceInfo.uuid, container)


  override val queryParameters = {
    newQueryParameters.
      set(PithosRequestParamKeys.Format, ResponseFormats.XML.responseFormat()).
      set(PithosRequestParamKeys.Path, path)
  }

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

  /**
   * Parse a response header that is specific to this command and whose value must be of non-String type.
   *
   * Returns `true` iff the header is parsed.
   *
   * The parsed [[gr.grnet.common.keymap.HeaderKey]]
   * and its associated non-String value are recorded in the provided `keyMap`.
   */
  override protected def tryParseNonStringResponseHeader(
      keyMap: KeyMap,
      name: String,
      value: String
  ) = {
    name match {
      case PithosHeaderKeys.Pithos.X_Container_Block_Hash.name ⇒
        keyMap.set(PithosHeaderKeys.Pithos.X_Container_Block_Hash, value)
        true

      case PithosHeaderKeys.Pithos.X_Container_Block_Size.name ⇒
        keyMap.set(PithosHeaderKeys.Pithos.X_Container_Block_Size, value.toLong)
        true

      case PithosHeaderKeys.Pithos.X_Container_Object_Meta.name ⇒
        keyMap.set(PithosHeaderKeys.Pithos.X_Container_Object_Meta, value)
        true

      case PithosHeaderKeys.Pithos.X_Container_Object_Count.name ⇒
        keyMap.set(PithosHeaderKeys.Pithos.X_Container_Object_Count, value.toInt)
        true

      case PithosHeaderKeys.Pithos.X_Container_Bytes_Used.name ⇒
        keyMap.set(PithosHeaderKeys.Pithos.X_Container_Bytes_Used, value.toLong)
        true

      case _ ⇒
        false
    }
  }

  override def buildResultData(
    responseHeaders: KeyMap,
    statusCode: Int,
    statusText: String,
    startMillis: Long,
    stopMillis: Long,
    getResponseBody: () => String
  ) = {

    val body = getResponseBody()
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
