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