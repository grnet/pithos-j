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

import com.ning.http.client.AsyncHandler.STATE
import com.ning.http.client.HttpResponseBodyPart
import gr.grnet.common.http.Command
import gr.grnet.pithosj.core.ServiceInfo
import gr.grnet.pithosj.core.http.RequestBody

/**
 * A command to be executed via the Pithos+ REST API.
 * Each command specifies its own input data, which will be used
 * to build up an HTTP request.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait PithosCommand[T] extends Command[T] {
  /**
   * The application domain of this command.
   */
  override def appDomain: String = "Pithos"

  /**
   * Specifies the target against which the command will be executed.
   * This includes the Pithos+ server and the Pithos+ user id and token.
   */
  def serviceInfo: ServiceInfo

  /**
   * The account ID for this command. This is the same as `serviceInfo.uuid` and
   * is provided for convenience.
   */
  def account: String = serviceInfo.uuid

  def onBodyPartReceivedOpt: Option[HttpResponseBodyPart ⇒ STATE]

  /**
   * Provides the HTTP request body, if any.
   */
  def requestBodyOpt: Option[RequestBody]

  /**
   * A set of all the HTTP status codes that are considered a failure for this command.
   */
  override def failureCodes: Set[Int] = Set() // FIXME implement
}
