/*
 * Copyright 2012 GRNET S.A. All rights reserved.
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

import com.ning.http.client.AsyncHandler.STATE
import com.ning.http.client.AsyncHttpClient
import gr.grnet.pithosj.core.Const.{IHeader, Headers, ContentTypes}
import gr.grnet.pithosj.core.result.Result
import gr.grnet.pithosj.core.result.info.{ObjectsInfo, NoInfo, ObjectInfo, ContainersInfo, AccountInfo, ContainerInfo}
import java.io.{File, OutputStream}
import java.util.Date
import org.slf4j.LoggerFactory
import scala.xml.XML
import java.net.URLConnection

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final class AsyncHttpPithosClient(http: AsyncHttpClient) extends Pithos {
  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  def ping(connInfo: ConnectionInfo) = {
    val reqBuilder = Helpers.prepareHEAD(http, connInfo, connInfo.userID)

    Helpers.execAsyncCompletionHandler(reqBuilder)(){ (response, baseResult) =>
      val infoOpt = NoInfo.optionBy(baseResult.is204)

      Result(infoOpt, baseResult, Set(204))
    }
  }

  def getAccountInfo(connInfo: ConnectionInfo) = {
    val reqBuilder = Helpers.prepareHEAD(http, connInfo, connInfo.userID)

    Helpers.execAsyncCompletionHandler(reqBuilder)(){ (response, baseResult) =>
      val infoOpt = if(baseResult.is204) {
        def h(header: IHeader) = baseResult.getHeader(header)

        val accountInfo = AccountInfo(
          h(Headers.Pithos.X_Account_Bytes_Used).toLong,
          h(Headers.Pithos.X_Account_Container_Count).toInt,
          h(Headers.Pithos.X_Account_Policy_Quota).toLong,
          h(Headers.Pithos.X_Account_Policy_Versioning)
        )

        Some(accountInfo)
      }
      else {
        None
      }

      Result(infoOpt, baseResult, Set(204))
    }
  }

  def replaceAccountMeta(connInfo: ConnectionInfo, meta: MetaData) = ???

  def deleteAccountMeta(connInfo: ConnectionInfo, metaKey: String) = ???

  def listContainers(connInfo: ConnectionInfo) = {
    val reqBuilder = Helpers.
      prepareGET(http, connInfo, connInfo.userID).
      addQueryParameter(
        Const.RequestParams.Format.requestParam(),
        Const.ResponseFormats.XML.responseFormat())

    Helpers.execAsyncCompletionHandler(reqBuilder)() { (response, baseResult) =>
      val infoOpt = if(baseResult.is200) {
        val body = response.getResponseBody
        val xml = XML.loadString(body)

        val containerInfos = for {
          container <- xml \ "container"
          count <- container \ "count"
          last_modified <- container \ "last_modified"
          bytes <- container \ "bytes"
          name <- container \ "name"
          x_container_policy <- container \ "x_container_policy"
        } yield {
          // parse:
          //  <x_container_policy>
          //    <key>quota</key>
          //    <value>53687091200</value>
          //    <key>versioning</key>
          //
          //    <value>auto</value>
          //  </x_container_policy>
          val kvPairs = for {
            child <- x_container_policy.nonEmptyChildren if Set("key", "value").contains(child.label.toLowerCase())
          } yield {
            child.text
          }

          // A key is at an even index, a value is at an odd index
          val (keys_i, values_i) = kvPairs.zipWithIndex.partition {
            case (s, index) => index % 2 == 0
          }
          val keys = keys_i.map(_._1) // throw away the index
          val values = values_i.map(_._1)

          val policy = new MetaData
          for((k, v) <- keys.zip(values)) {
            policy.setOne(k, v)
          }

          val containerInfo = ContainerInfo(
            name.text,
            count.text.toInt,
            Const.Dates.Format1.parse(last_modified.text),
            bytes.text.toLong,
            policy
          )

          containerInfo
        }

        Some(ContainersInfo(containerInfos.toList))
      }
      else {
        None
      }

      Result(infoOpt, baseResult, Set(200))
    }
  }

  def createContainer(connInfo: ConnectionInfo, container: String) = ???

  def getContainerInfo(connInfo: ConnectionInfo, container: String) = ???

  def deleteContainer(connInfo: ConnectionInfo, container: String) = ???

  def createDirectory(connInfo: ConnectionInfo, container: String, path: String) = {
    val reqBuilder = Helpers.preparePUT(http, connInfo, connInfo.userID, container, path)
    reqBuilder.setHeader(
      Headers.Standard.Content_Type.header(),
      ContentTypes.Application_Directory.contentType()
    )
    reqBuilder.setHeader(
      Headers.Standard.Content_Length.header(),
      0.toString
    )

    Helpers.execAsyncCompletionHandler(reqBuilder)() { (response, baseResult) =>
      val infoOpt = NoInfo.optionBy(baseResult.is201)

      Result(infoOpt, baseResult, Set(201))
    }
  }

  def getObjectMeta(connInfo: ConnectionInfo, path: String) = ???

  def deleteObjectMeta(connInfo: ConnectionInfo, path: String, metaKey: String) = ???

  def replaceObjectMeta(connInfo: ConnectionInfo, path: String, meta: MetaData) = ???

  def getObject(connInfo: ConnectionInfo, container: String, path: String, version: String, out: OutputStream) = {
    val reqBuilder = Helpers.prepareGET(http, connInfo, connInfo.userID, container, path)
    if(version ne null) {
      reqBuilder.addQueryParameter(Const.RequestParams.Version.requestParam(), version)
    }

    Helpers.execAsyncCompletionHandler(reqBuilder) { bodyPart =>
      bodyPart.writeTo(out)
      STATE.CONTINUE;
    }{ (response, baseResult) =>
      val infoOpt = if(baseResult.is200) {
        def h(name: IHeader) = baseResult.getHeader(name)

        val objectInfo = ObjectInfo(
          container = container,
          path = path,
          contentType = h(Headers.Standard.Content_Type),
          contentLength = h(Headers.Standard.Content_Length).toLong,
          // Wed, 19 Sep 2012 08:18:23 GMT
          lastModified = Const.Dates.Format2.parse(h(Headers.Standard.Last_Modified)),
          xObjectHash = h(Headers.Pithos.X_Object_Hash),
          xObjectModifiedBy = h(Headers.Pithos.X_Object_Modified_By),
          xObjectVersionTimestamp = Const.Dates.Format2.parse(h(Headers.Pithos.X_Object_Version_Timestamp)),
          xObjectUUID = h(Headers.Pithos.X_Object_UUID),
          xObjectVersion = h(Headers.Pithos.X_Object_Version),
          eTag = Some(h(Headers.Standard.ETag))
        )

        Some(objectInfo)
      }
      else {
        None
      }

      Result(infoOpt, baseResult, Set(200))
    }
  }

  def getObjectInfo(connInfo: ConnectionInfo, container: String, path: String) = {
    val reqBuilder = Helpers.prepareHEAD(http, connInfo, connInfo.userID, container, path)

    Helpers.execAsyncCompletionHandler(reqBuilder)() { (response, baseResult) =>
      val infoOpt = if(baseResult.is200) {
        def h(name: IHeader) = baseResult.getHeader(name)

        val objectInfo = ObjectInfo(
          container = container,
          path = path,
          contentType = h(Headers.Standard.Content_Type),
          contentLength = h(Headers.Standard.Content_Length).toLong,
          // Wed, 19 Sep 2012 08:18:23 GMT
          lastModified = Const.Dates.Format2.parse(h(Headers.Standard.Last_Modified)),
          xObjectHash = h(Headers.Pithos.X_Object_Hash),
          xObjectModifiedBy = h(Headers.Pithos.X_Object_Modified_By),
          xObjectVersionTimestamp = Const.Dates.Format2.parse(h(Headers.Pithos.X_Object_Version_Timestamp)),
          xObjectUUID = h(Headers.Pithos.X_Object_UUID),
          xObjectVersion = h(Headers.Pithos.X_Object_Version),
          eTag = Some(h(Headers.Standard.ETag))
        )

        Some(objectInfo)
      }
      else {
        None
      }

      Result(infoOpt, baseResult, Set(200))
    }
  }

  def putObject(
      connInfo: ConnectionInfo,
      container: String,
      path: String,
      in: File,
      _contentType: String
  ) = {
    val contentType = _contentType match {
      case null ⇒
        URLConnection.guessContentTypeFromName(path)
      case contentType ⇒
        contentType
    }
    val reqBuilder = Helpers.preparePUT(http, connInfo, connInfo.userID, container, path)
    reqBuilder.setHeader(Headers.Standard.Content_Type.header(), contentType)
    reqBuilder.setBody(in)

    Helpers.execAsyncCompletionHandler(reqBuilder)() { (response, baseResult) =>
      val infoOpt = NoInfo.optionBy(baseResult.is201)

      Result(infoOpt, baseResult, Set(201))
    }
  }

  def deleteObject(connInfo: ConnectionInfo, container: String, path: String) = {
    val reqBuilder = Helpers.prepareDELETE(http, connInfo, connInfo.userID, container, path)

    Helpers.execAsyncCompletionHandler(reqBuilder)() { (response, baseResult) =>
      val infoOpt = NoInfo.optionBy(baseResult.is204)

      Result(infoOpt, baseResult, Set(204))
    }
  }

  def copyObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String
  ) = ???

  def moveObject(
      connInfo: ConnectionInfo,
      fromContainer: String,
      fromObj: String,
      toContainer: String,
      toObj: String
  ) = ???

  def listObjectsInContainer(connInfo: ConnectionInfo, container: String) = {
    listObjectsInPath(connInfo, container, "")
  }

  def listObjectsInPath(
      connInfo: ConnectionInfo,
      container: String,
      path: String
  ) = {
    val reqBuilder = Helpers.prepareGET(http, connInfo, connInfo.userID, container)
    reqBuilder.addQueryParameter(
      Const.RequestParams.Format.requestParam(),
      Const.ResponseFormats.XML.responseFormat()
    )

    path match {
      case null =>
      case "" =>
      case path =>
        reqBuilder.addQueryParameter(Const.RequestParams.Path.requestParam(), path)
    }

    Helpers.execAsyncCompletionHandler(reqBuilder)() { (response, baseResult) =>
      val infoOpt = if(baseResult.is200) {
        val body = XML.loadString(response.getResponseBody)

        val objectInfos = for {
          obj   <- body \ "object"
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
          ObjectInfo(
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

        Some(ObjectsInfo(objectInfos.toList))
      }
      else {
        None
      }
      Result(infoOpt, baseResult, Set(200))
    }
  }
}
