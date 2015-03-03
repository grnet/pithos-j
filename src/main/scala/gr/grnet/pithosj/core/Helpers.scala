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

package gr.grnet.pithosj.core

import java.util
import java.util.concurrent.{Future, TimeUnit}

import gr.grnet.common.date.DateParsers
import gr.grnet.common.key.HeaderKey
import gr.grnet.pithosj.core.keymap.PithosHeaderKeys
import org.slf4j.LoggerFactory
import typedkey.env.MEnv

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
      env: MEnv,
      name: String,
      values: List[String]
  ): Unit = {
    values match {
      case value :: _ ⇒
        name match {
          case PithosHeaderKeys.Standard.Content_Length.name ⇒
            env.update(PithosHeaderKeys.Standard.Content_Length, value.toLong)

          case PithosHeaderKeys.Standard.Last_Modified.name ⇒
            val parsedDate = DateParsers.parse(value, DateParsers.Format2Parser)
            env.update(PithosHeaderKeys.Standard.Last_Modified, parsedDate)

          case PithosHeaderKeys.Standard.Date.name ⇒
            val parsedDate = DateParsers.parse(value, DateParsers.Format2Parser)
            env.update(PithosHeaderKeys.Standard.Date, parsedDate)

          case _ ⇒
            env.update(HeaderKey[String](name), value)
        }

      case _ ⇒
    }
  }
}

object Helpers extends Helpers
