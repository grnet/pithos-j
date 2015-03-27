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

import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Response, Request}
import com.twitter.util.{Return, Throw, Await, Future}
import gr.grnet.common.http.TResult

/**
 *
 */
trait TestBase {
  final type Client = Service[Request, Response]

  final def assertResult[T](what: T): Unit = {
    what match {
      case f: Future[_] ⇒
        val r = Await.result(f)
        assertResult(r)

      case tr: TResult[_] ⇒
        assert(tr.isSuccess)

      case p: Product ⇒
        p.productIterator.foreach(assertResult)

      case b: Boolean ⇒
        assert(b)

      case r: Response ⇒
        assert(assertion = true)

      case _ ⇒
        throw new IllegalArgumentException(s"Unexpected assertion input: $what")
    }
  }

  def assertResultX[T](future: Future[T])(f: (T) ⇒ Unit = (_:T) ⇒ {}): Unit = {
    val transformed =
      future.transform {
        case Throw(t) ⇒
          t.printStackTrace(System.err)
          Future.exception(t)

        case Return(result) ⇒
          f(result)
          println(result)
          Future.value(result)
      }

    assertResult(transformed)
  }

  def assertFuture[T](future: Future[TResult[T]]): Unit = assertResultX(future)()
}
