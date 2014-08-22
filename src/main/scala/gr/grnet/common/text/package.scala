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


/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
package object text {
  implicit class RemovePrefix(val s: String) extends AnyVal {
    def removePrefix(prefix: String): String =
      if(s.startsWith(prefix)) s.substring(prefix.length)
      else s
  }

  implicit class NoLeadingSlash(val s: String) extends AnyVal {
    def noLeadingSlash: String =
      if(s.length > 0 && s.charAt(0) == '/') s.substring(1).noLeadingSlash
      else s
  }

  implicit class NoTrailingSlash(val s: String) extends AnyVal {
    def noTrailingSlash: String =
      if(s.length > 0 && s.charAt(s.length - 1) == '/') s.substring(0, s.length - 1).noTrailingSlash
      else s
  }

  implicit class NormalizePath(val path: String) extends AnyVal {
    def normalizePath: String = path.replaceAll("/+", "/")
  }

  implicit class PathToList(val path: String) extends AnyVal {
    def pathToList: List[String] = path.split("/").toList
  }

  implicit class ParentPath(val path: String) extends AnyVal {
    // Note that the parent path of "/" is "".
    // This is OK for the Pithos backend, since a Pithos container path is always a prefix to any requested URI
    def parentPath: String = path.substring(0, path.noTrailingSlash.lastIndexOf('/') + 1)
  }
}
