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

import java.util.Locale
import java.util.concurrent.atomic.AtomicLong

import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Request, RequestBuilder, Response}
import com.twitter.util._
import gr.grnet.common.http.TResult
import gr.grnet.pithosj.core.PithosApiSkeleton
import gr.grnet.pithosj.core.command.PithosCommand
import gr.grnet.pithosj.core.http.PithosHeader

/**
 *
 */
class PithosClient(service: Service[Request, Response]) extends PithosApiSkeleton {
  private[this] val successCallCounter = new AtomicLong()
  private[this] val failureCallCounter = new AtomicLong()

  protected def callImpl[T](command: PithosCommand[T]): Future[TResult[T]] = {
    log.ifDebug({
      val what = command.commandName
      val method = command.httpMethod.toString.toUpperCase(Locale.ENGLISH)
      val url = command.callURL
      val headers = command.requestHeaders.updated(PithosHeader.X_Auth_Token.headerName(), "***").mkString("{", ", ", "}")
      s"[$what] ==> $method $url, using headers: $headers"
    })

    val request =
      RequestBuilder().
        url(command.callURL).
        addHeaders(command.requestHeaders).
        build(command.httpMethod, command.requestBodyOpt)

    val startMillis = System.currentTimeMillis()

    val responseF = service(request)
    responseF.transform[TResult[T]] {
      case Return(response) ⇒
        val stopMillis = System.currentTimeMillis()
        successCallCounter.incrementAndGet()

        log.ifDebug({
          val what = command.commandName
          val status = response.status
          s"[$what] <== $status"
        })

        for {
          bodyHandler ← command.onResponseOpt
        } {
          val body = response.content
          bodyHandler(body)
        }

        val result = command.buildResult(response, startMillis, stopMillis)
        Future.value(result)

      case Throw(t) ⇒
        failureCallCounter.incrementAndGet()

        log.ifDebug({
          val what = command.commandName
          val error = t.toString
          s"[$what] <== ERROR $error"
        })

        Future.rawException(t)
    }
  }

  def successCallCount = this.successCallCounter.get()
  def failureCallCount = this.failureCallCounter.get()
  def callCount = successCallCount + failureCallCount
}
