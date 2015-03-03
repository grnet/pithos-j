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
import java.util.Locale

import com.twitter.finagle.Httpx
import gr.grnet.pithosj.api.PithosApi
import gr.grnet.pithosj.core.ServiceInfo

/**
 *
 */
object PithosClientFactory {
  def newClient(serviceInfo: ServiceInfo): PithosApi = {
    val serviceURL = serviceInfo.serverURL
    val protocol = serviceURL.getProtocol.toLowerCase(Locale.ENGLISH)
    val host = serviceURL.getHost
    val port =
      serviceURL.getPort match {
        case -1 ⇒
          protocol match {
            case "http" ⇒ 80
            case "https" ⇒ 443
            case _ ⇒
              throw new Exception(s"Bad protocol $protocol")
          }

        case p ⇒
          p
      }

    val client = {
      val client = if(protocol == "https") Httpx.client.withTls(host) else Httpx.client
      client.newClient(s"$host:$port")
    }
    val service = client.toService
    new PithosClient(service)
  }

}
