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

package gr.grnet.common

import java.io.{File, Closeable}

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
package object io {
  implicit class CloseAnyway(val io: Closeable) extends AnyVal {
    def closeAnyway(): Unit = {
      try io.close()
      catch {
        case _: Exception ⇒
      }
    }
  }

  implicit class DeleteAnyway(val file: File) extends AnyVal {
    def deleteAnyway(): Unit =
      try file.delete() catch { case _: Exception ⇒ }
  }
}
