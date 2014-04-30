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

package gr.grnet.cdmi

import com.twitter.finagle.http.Response
import com.twitter.util.Future
import com.twitter.app.Flaggable
import java.net.URL

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
package object service {
  implicit class FinagleResponseToFuture(val response: Response) extends AnyVal {
    final def future: Future[Response] = Future.value(response)
  }
}
