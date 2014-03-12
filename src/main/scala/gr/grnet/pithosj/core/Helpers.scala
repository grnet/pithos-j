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

package gr.grnet.pithosj.core

import com.ning.http.client.AsyncHttpClient
import gr.grnet.common.date.DateParsers
import gr.grnet.pithosj.core.keymap.PithosHeaderKeys
import java.util
import java.util.concurrent.{TimeUnit, Future}
import org.slf4j.LoggerFactory
import gr.grnet.common.keymap.{HeaderKey, KeyMap}

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
sealed class Helpers {


  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  @inline def jListOne[T](item: T): util.List[T] = {
    val list = new util.ArrayList[T]()
    list.add(item)
    list
  }

  @inline final def ifNull(value: String, other: String): String = {
    if(value ne null) value else other
  }

  final def knownGoodFuture[V](value: V): Future[V] = {
    new Future[V] {
      def isCancelled = false

      def get(timeout: Long, unit: TimeUnit) = value

      def get() = value

      def cancel(mayInterruptIfRunning: Boolean) = false

      def isDone = true
    }
  }

  final def parseGenericResponseHeader(
      keyMap: KeyMap,
      name: String,
      values: List[String]
  ): KeyMap = {
    values match {
      case value :: _ ⇒
        name match {
          case PithosHeaderKeys.Standard.Content_Length.name ⇒
            keyMap.set(PithosHeaderKeys.Standard.Content_Length, value.toLong)

          case PithosHeaderKeys.Standard.Last_Modified.name ⇒
            val parsedDate = DateParsers.parse(value, DateParsers.Format2Parser)
            keyMap.set(PithosHeaderKeys.Standard.Last_Modified, parsedDate)

          case PithosHeaderKeys.Standard.Date.name ⇒
            val parsedDate = DateParsers.parse(value, DateParsers.Format2Parser)
            keyMap.set(PithosHeaderKeys.Standard.Date, parsedDate)

          case name ⇒
            keyMap.set(HeaderKey[String](name), value)
        }

      case _ ⇒
    }

    keyMap
  }
}

final object Helpers extends Helpers {
  type RequestBuilder = AsyncHttpClient#BoundRequestBuilder
}
