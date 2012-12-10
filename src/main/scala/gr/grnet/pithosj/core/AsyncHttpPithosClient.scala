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

import com.ning.http.client.AsyncHttpClient
import gr.grnet.pithosj.core.result.Result
import gr.grnet.pithosj.core.result.info.{NoInfo, ObjectInfo, ContainersInfo, AccountInfo, ContainerInfo}
import gr.grnet.pithosj.core.Const.Headers
import gr.grnet.pithosj.core.Const.Headers
import java.io.{OutputStream, InputStream}
import org.slf4j.LoggerFactory
import scala.xml.XML
import com.ning.http.client.AsyncHandler.STATE

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final class AsyncHttpPithosClient(http: AsyncHttpClient) extends Pithos {
  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  def ping(connInfo: ConnectionInfo) = ???

  def getAccountInfo(connInfo: ConnectionInfo) = {
    val reqBuilder = Helpers.prepareHead(http, connInfo, connInfo.userID)

    Helpers.execAsyncCompletionHandler(reqBuilder)(){ (response, baseResult) =>
      def h(name: String) = baseResult.getHeader(name)

      val accountInfo = AccountInfo(
        h(Headers.Pithos.X_Account_Bytes_Used.header).toLong,
        h(Headers.Pithos.X_Account_Container_Count.header).toInt,
        h(Headers.Pithos.X_Account_Policy_Quota.header).toLong,
        h(Headers.Pithos.X_Account_Policy_Versioning.header)
      )

      Result(accountInfo, baseResult)
    }
  }

  def replaceAccountMeta(connInfo: ConnectionInfo, meta: MetaData) = ???

  def deleteAccountMeta(connInfo: ConnectionInfo, metaKey: String) = ???

  def listContainers(connInfo: ConnectionInfo) = {
    val reqBuilder = Helpers.
      prepareGet(http, connInfo, connInfo.userID).
      addQueryParameter(Const.Params.format, ResponseFormat.XML.parameterValue)

    Helpers.execAsyncCompletionHandler(reqBuilder)() { (response, baseResult) =>
      val body = response.getResponseBody
      val xml = XML.loadString(body)

      val containerInfos = for {
        container          <- xml \ "container"
        count              <- container \ "count"
        last_modified      <- container \ "last_modified"
        bytes              <- container \ "bytes"
        name               <- container \ "name"
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
        val (keys_i, values_i) = kvPairs.zipWithIndex.partition { case (s, index) => index % 2 == 0}
        val keys = keys_i.map(_._1) // throw away the index
        val values = values_i.map(_._1)

        val policy = new MetaData
        for((k, v) <- keys.zip(values)) {
          policy.setOne(k,v)
        }

        ContainerInfo(
          name.text,
          count.text.toInt,
          Const.Dates.Format1.parse(last_modified.text),
          bytes.text.toLong,
          policy
        )
      }

      Result(ContainersInfo(containerInfos.toList), baseResult)
    }
  }

  def createContainer(connInfo: ConnectionInfo, container: String) = ???

  def getContainerInfo(connInfo: ConnectionInfo, container: String) = ???

  def deleteContainer(connInfo: ConnectionInfo, container: String) = ???

  def createDirectory(connInfo: ConnectionInfo, directory: String) = ???

  def getObjectMeta(connInfo: ConnectionInfo, obj: String) = ???

  def deleteObjectMeta(connInfo: ConnectionInfo, obj: String, metaKey: String) = ???

  def replaceObjectMeta(connInfo: ConnectionInfo, obj: String, meta: MetaData) = ???

  def getObject(connInfo: ConnectionInfo, container: String, obj: String, out: OutputStream) = {
    val reqBuilder = Helpers.prepareGet(http, connInfo, connInfo.userID, container, obj)
    Helpers.execAsyncCompletionHandler(reqBuilder) { bodyPart =>
      bodyPart.writeTo(out)
      STATE.CONTINUE;
    }{ (response, baseResult) =>
      def h(name: String) = baseResult.getHeader(name)

      val objectInfo = ObjectInfo(
        h(Headers.Standard.Content_Type.header),
        h(Headers.Standard.Content_Length.header).toLong,
        // Wed, 19 Sep 2012 08:18:23 GMT
        Const.Dates.Format2.parse(h(Headers.Standard.Last_Modified.header)),
        h(Headers.Pithos.X_Object_Hash.header),
        h(Headers.Pithos.X_Object_Modified_By.header),
        Const.Dates.Format2.parse(h(Headers.Pithos.X_Object_Version_Timestamp.header)),
        h(Headers.Pithos.X_Object_UUID.header),
        h(Headers.Pithos.X_Object_Version.header).toInt,
        h(Headers.Standard.ETag.header),
        container,
        obj
      )
      
      Result(objectInfo, baseResult)
    }
  }

  def getObjectInfo(connInfo: ConnectionInfo, container: String, obj: String) = {
    val reqBuilder = Helpers.prepareHead(http, connInfo, connInfo.userID, container, obj)

    Helpers.execAsyncCompletionHandler(reqBuilder)() { (response, baseResult) =>
      def h(name: String) = baseResult.getHeader(name)

      val objectInfo = ObjectInfo(
        h(Headers.Standard.Content_Type.header),
        h(Headers.Standard.Content_Length.header).toLong,
        // Wed, 19 Sep 2012 08:18:23 GMT
        Const.Dates.Format2.parse(h(Headers.Standard.Last_Modified.header)),
        h(Headers.Pithos.X_Object_Hash.header),
        h(Headers.Pithos.X_Object_Modified_By.header),
        Const.Dates.Format2.parse(h(Headers.Pithos.X_Object_Version_Timestamp.header)),
        h(Headers.Pithos.X_Object_UUID.header),
        h(Headers.Pithos.X_Object_Version.header).toInt,
        h(Headers.Standard.ETag.header),
        container,
        obj
      )

      Result(objectInfo, baseResult)
    }
  }

  def uploadObject(connInfo: ConnectionInfo, obj: String, in: InputStream, size: Long) = ???

  def deleteObject(connInfo: ConnectionInfo, obj: String) = ???

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

  def listObjects(connInfo: ConnectionInfo) = ???

  def listObjectsInPath(connInfo: ConnectionInfo, pathPrefix: String) {}
}
