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

package gr.grnet.pithosj.impl.finagle

import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Request, RequestBuilder, Response}
import com.twitter.util.{Future, Promise, Return, Throw}
import gr.grnet.common.http.TResult
import gr.grnet.pithosj.core.PithosApiSkeleton
import gr.grnet.pithosj.core.command.PithosCommand

/**
 *
 */
class PithosClient(svc: Service[Request, Response]) extends PithosApiSkeleton {
  protected def callImpl[T](command: PithosCommand[T]): Future[TResult[T]] = {
    val promise = Promise[TResult[T]]()
    val request =
      RequestBuilder().
        url(command.serverURLExcludingParameters).
        addHeaders(command.requestHeaders.toMapByName.map { case (k, v) ⇒ (k, s"$v") }.toMap).
        build(command.httpMethod, command.requestBodyOpt)

    val startMillis = System.currentTimeMillis()
    val responseF = svc.apply(request)
    responseF.respond {
      case Return(response) ⇒
        val stopMillis = System.currentTimeMillis()

        for {
          bodyHandler ← command.onResponseOpt
        } {
          val body = response.content
          bodyHandler(body)
        }

        val result = command.buildResult(response, startMillis, stopMillis)

        promise.setValue(result)

      case Throw(t) ⇒
        promise.setException(t)
    }

    promise
  }
}
