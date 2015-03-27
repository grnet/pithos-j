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

import java.net.URL

import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.httpx.{Http, Request, Response}

/**
 *
 */
object FinagleClientFactory {
  def getConnectionInfo(serverURL: URL): (String, Int, Boolean) = {
    val host = serverURL.getHost
    val port = serverURL.getPort match { case -1 ⇒ serverURL.getDefaultPort; case p ⇒ p }
    val useTls = serverURL.getProtocol == "https"

    (host, port, useTls)
  }

  def newClientService(host: String, port: Int, useTls: Boolean, hostConnectionLimit: Int = 64): Service[Request, Response] = {
    val hostport = s"$host:$port"
    val clientBuilder = ClientBuilder().
      codec(Http()).
      name(hostport).
      hostConnectionLimit(hostConnectionLimit).
      hosts(hostport)

    val client =
      if(useTls) clientBuilder.tls(host).build()
      else       clientBuilder.build()

    client
  }

  def newClientService(serverURL: URL): Service[Request, Response] = {
    val (host, port, useTls) = getConnectionInfo(serverURL)
    newClientService(host, port, useTls)
  }

  def newClientService(serverURL: String): Service[Request, Response] = newClientService(new URL(serverURL))
}
