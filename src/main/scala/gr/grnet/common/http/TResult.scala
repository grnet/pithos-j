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

package gr.grnet.common.http

import gr.grnet.common.keymap.KeyMap

/**
 * The result of a [[gr.grnet.common.http.Command]].
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait TResult[+T] {
  val originator: CommandDescriptor

  val statusCode: Int

  val statusText: String

  val startMillis: Long

  val stopMillis: Long

  val responseHeaders: KeyMap

  val successData: Option[T]

  def errorDetails: Option[String]

  def completionMillis = stopMillis - startMillis

  def isSuccess: Boolean = originator.successCodes(statusCode)

  def is200 = statusCode == 200

  def is201 = statusCode == 201

  def is204 = statusCode == 204

  def is(code: Int) = this.statusCode == code
}
