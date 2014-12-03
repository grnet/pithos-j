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

case class CheckExistsObjectResultData(
  exists: Boolean,
  isContainer: Boolean,
  container: String,
  path: String,
  contentType: Option[String],
  contentTypeIsDirectory: (String) ⇒ Boolean
) {
  def isDirectory =
    exists && !isContainer &&
      (contentType match {
        case Some(ct) if contentTypeIsDirectory(ct) ⇒
          true
        case _ ⇒
          false
      })
}
