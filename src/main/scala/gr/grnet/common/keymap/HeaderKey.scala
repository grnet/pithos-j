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

package gr.grnet.common.keymap

import com.ckkloverdos.key.TKeyOnly
import gr.grnet.common.http.IHeader

/**
 * A typed key for HTTP headers.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final class HeaderKey[T: Manifest] private[keymap](
  override val name: String
) extends TKeyOnly[T](name)

object HeaderKey {
  def apply[T: Manifest](name: String): HeaderKey[T] = new HeaderKey[T](name)

  def apply[T: Manifest](header: IHeader): HeaderKey[T] = new HeaderKey[T](header.headerName())
}
