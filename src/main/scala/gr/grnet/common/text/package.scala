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

  implicit class NormalizeUri(val uri: String) extends AnyVal {
    def normalizeUri: String = uri.replaceAll("/+", "/")
  }

  implicit class UriToList(val uri: String) extends AnyVal {
    def uriToList: List[String] = uri.split("/").toList
  }

  implicit class ParentUri(val uri: String) extends AnyVal {
    def parentUri: String = uri.substring(0, uri.noTrailingSlash.lastIndexOf('/') + 1)
  }
}
