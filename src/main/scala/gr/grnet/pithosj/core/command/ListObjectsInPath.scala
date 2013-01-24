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

import gr.grnet.pithosj.core.command.result.{ObjectInPathResultData, ListObjectsInPathResultData, ListObjectsInPathResult}
import gr.grnet.pithosj.core.http.HTTPMethod
import gr.grnet.pithosj.core.{Const, MetaData, ConnectionInfo}
import java.util.Date
import scala.xml.XML

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
case class ListObjectsInPath(
    connectionInfo: ConnectionInfo,
    container: String,
    path: String
) extends CommandSkeleton[ListObjectsInPathResult] {
  /**
   * The HTTP method by which the command is implemented.
   */
  def httpMethod = HTTPMethod.GET

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  def successCodes = Set(200)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  def serverURLPathElements = Seq(connectionInfo.userID, container, path)


  override val queryParameters = {
    val qp = newQueryParameters.
      setOne(Const.RequestParams.Format.requestParam(), Const.ResponseFormats.XML.responseFormat())

    path match {
      case null | "" ⇒
        qp
      case path ⇒
        qp.setOne(Const.RequestParams.Path.requestParam(), path)
    }
  }

  def buildResult(
      responseHeaders: MetaData,
      statusCode: Int,
      statusText: String,
      completionMillis: Long,
      getResponseBody: () => String
  ) = {
    val resultDataOpt = successCodes(statusCode) match {
      case false ⇒
        None
      case true ⇒
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
            lastModified = Const.Dates.Format1.parse(last_modified.text),
            xObjectHash = x_object_hash.text,
            xObjectModifiedBy = x_object_modified_by.text,
            xObjectVersionTimestamp = new Date(x_object_version_timestamp.text.toDouble * 1000 toLong),
            xObjectUUID = x_object_uuid.text,
            xObjectVersion = x_object_version.text,
            eTag = None
          )
        }

        Some(ListObjectsInPathResultData(objectsInPath.toList))
    }

    ListObjectsInPathResult(
      this,
      responseHeaders,
      statusCode,
      statusText,
      completionMillis,
      resultDataOpt
    )
  }
}
