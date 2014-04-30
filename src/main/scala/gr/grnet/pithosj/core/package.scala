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

package gr.grnet.pithosj

import scala.collection.mutable
import scala.collection.{JavaConversions => JC}
import com.ckkloverdos.key.TKey
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration.Inf

package object core {

  /**
   * This is to overcome a bug in our usage of the typedkey library.
   * In particular it was compiled with a previous (< 2.10) version of Scala
   * and the `toString` method fails miserably.
   *
   * The error is of the form
   * <p/>
   * `java.lang.NoSuchMethodError: scala.Predef$.augmentString(Ljava/lang/String;)Lscala/collection/immutable/StringOps;`
   *
   * @param tkey
   * @tparam T
   */
  implicit class RichTKey[T](val tkey: TKey[T]) extends AnyVal {
    def s = s"${tkey.getClass.getSimpleName}[${tkey.keyType}](${tkey.name})"
  }

  // Anti-pattern. I use this in tests
  implicit class BadFuture[T](val future: Future[T]) extends AnyVal {
    def get(): T = Await.result(future, Inf)
  }

  @inline
  final def asScala[K, V](jmap: java.util.Map[K, V]): mutable.Map[K, V] = {
    jmap match {
      case null ⇒ mutable.Map[K, V]()
      case jmap ⇒ JC.mapAsScalaMap(jmap)
    }
  }

  @inline
  final def asScala[T](jlist: java.util.List[T]): List[T] = {
    jlist match {
      case null ⇒ Nil
      case jlist ⇒ JC.asScalaBuffer(jlist).toList
    }
  }

  @inline
  final def asFullScala[A, B](jlmap: java.util.Map[A, java.util.List[B]]): mutable.Map[A, List[B]] = {
    asScala(jlmap).map { case (k, v) ⇒
      (k, asScala(v))
    }
  }
}