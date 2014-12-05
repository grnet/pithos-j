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

import gr.grnet.common.Paths
import gr.grnet.common.http.Method
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.keymap.PithosHeaderKeys
import typedkey.env.ImEnv

/**
 * Copies an object around.
 */
case class CopyObjectCommand(
  serviceInfo: ServiceInfo,
  fromContainer: String,
  fromPath: String,
  toContainer: String,
  toPath: String
) extends PithosCommandSkeleton[Unit] {

  /**
   * The HTTP method by which the command is implemented.
   */
  val httpMethod = Method.COPY

  /**
   * A set of all the HTTP status codes that are considered a success for this command.
   */
  val successCodes = Set(201)

  /**
   * The HTTP request headers that are set by this command.
   */
  override val requestHeaders =
    newDefaultRequestHeaders.
      update(PithosHeaderKeys.Pithos.Destination, "/" + Paths.build(toContainer, toPath)).
      toImmutable

  def serverURLPathElements = Seq(account, fromContainer, fromPath)


  override def buildResultData(
    responseHeaders: ImEnv, statusCode: Int, statusText: String, startMillis: Long, stopMillis: Long,
    getResponseBody: () => String
  ): Unit = {}
}
