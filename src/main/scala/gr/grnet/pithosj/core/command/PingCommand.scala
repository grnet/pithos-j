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

package gr.grnet.pithosj.core.command

import com.twitter.finagle.httpx.Method.Head
import com.twitter.finagle.httpx.{Response, Status}
import gr.grnet.pithosj.core.ServiceInfo

case class PingCommand(serviceInfo: ServiceInfo) extends PithosCommandSkeleton[Unit] {
  val httpMethod = Head

  val successStatuses = Set(204).map(Status.fromCode)

  /**
   * Computes that URL path parts that will follow the Pithos+ server URL
   * in the HTTP call.
   */
  val serverURLPathElements = Seq(serviceInfo.uuid)

  def buildResultData(response: Response, startMillis: Long, stopMillis: Long): Unit = {}
}
