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

package gr.grnet.pithosj.core.command

import gr.grnet.common.date.DateParsers
import gr.grnet.common.http.Method
import gr.grnet.common.keymap.KeyMap
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.command.result.ObjectInPathResultData
import gr.grnet.pithosj.core.http.ResponseFormats
import gr.grnet.pithosj.core.keymap.{HeaderKeys, ResultKeys, RequestParamKeys}
import scala.xml.XML

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
case class ListObjectsInPath(
    serviceInfo: ServiceInfo,
    container: String,
    path: String
) extends PithosCommandSkeleton {
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
      set(RequestParamKeys.Format, ResponseFormats.XML.responseFormat()).
      set(RequestParamKeys.Path, path)
  }

  override val responseHeaderKeys = Seq(
    HeaderKeys.Pithos.X_Container_Block_Hash,
    HeaderKeys.Pithos.X_Container_Block_Size,
    HeaderKeys.Pithos.X_Container_Object_Meta,
    HeaderKeys.Pithos.X_Container_Object_Count,
    HeaderKeys.Pithos.X_Container_Bytes_Used
  )

  override val resultDataKeys = Seq(
    ResultKeys.ListObjectsInPath
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
      case HeaderKeys.Pithos.X_Container_Block_Hash.name ⇒
        keyMap.set(HeaderKeys.Pithos.X_Container_Block_Hash, value)
        true

      case HeaderKeys.Pithos.X_Container_Block_Size.name ⇒
        keyMap.set(HeaderKeys.Pithos.X_Container_Block_Size, value.toLong)
        true

      case HeaderKeys.Pithos.X_Container_Object_Meta.name ⇒
        keyMap.set(HeaderKeys.Pithos.X_Container_Object_Meta, value)
        true

      case HeaderKeys.Pithos.X_Container_Object_Count.name ⇒
        keyMap.set(HeaderKeys.Pithos.X_Container_Object_Count, value.toInt)
        true

      case HeaderKeys.Pithos.X_Container_Bytes_Used.name ⇒
        keyMap.set(HeaderKeys.Pithos.X_Container_Bytes_Used, value.toLong)
        true

      case _ ⇒
        false
    }
  }

  override def buildResult(
    responseHeaders: KeyMap,
    statusCode: Int,
    statusText: String,
    startMillis: Long,
    stopMillis: Long,
    getResponseBody: () => String,
    resultData: KeyMap
  ) = {

    if(successCodes(statusCode)) {
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
        ObjectInPathResultData(
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

      resultData.set(ResultKeys.ListObjectsInPath, objectsInPath.toList)
    }

    super.buildResult(
      responseHeaders,
      statusCode,
      statusText,
      startMillis,
      stopMillis,
      getResponseBody,
      resultData
    )
  }
}
