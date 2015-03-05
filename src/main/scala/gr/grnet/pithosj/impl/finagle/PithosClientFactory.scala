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

import gr.grnet.pithosj.api.PithosApi
import gr.grnet.pithosj.core.ServiceInfo

/**
 *
 */
object PithosClientFactory {
  def newClient(host: String, port: Int, useTls: Boolean): PithosApi =
    new PithosClient(FinagleClientFactory.newClient(host, port, useTls))

  def newClient(serverURL: URL): PithosApi =
    new PithosClient(FinagleClientFactory.newClient(serverURL))

  def newClient(serverURL: String): PithosApi =
    new PithosClient(FinagleClientFactory.newClient(serverURL))

  def newClient(serviceInfo: ServiceInfo): PithosApi = {
    val host = serviceInfo.serverHost
    val port = serviceInfo.serverPort
    val useTls = serviceInfo.isHttps

    newClient(host, port, useTls)
  }
}
