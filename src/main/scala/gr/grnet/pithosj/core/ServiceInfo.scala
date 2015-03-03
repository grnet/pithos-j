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

import java.net.URL
import java.util.Locale

case class ServiceInfo(
  serverURL: URL,
  rootPath: String,
  uuid: String,
  token: String
) {
  val (serverHost, serverPort, isHttps) = {
    val protocol = serverURL.getProtocol.toLowerCase(Locale.ENGLISH)
    val isHttps = protocol match {
      case "https" ⇒ true
      case "http" ⇒ false
      case _ ⇒
        throw new Exception(s"Bad protocol $protocol")
    }
    val host = serverURL.getHost
    val port =
      serverURL.getPort match {
        case -1 ⇒ if(isHttps) 443 else 80
        case p ⇒ p
      }

    (host, port, isHttps)
  }

  def hostAndPort: String = s"$serverHost:$serverPort"
}
