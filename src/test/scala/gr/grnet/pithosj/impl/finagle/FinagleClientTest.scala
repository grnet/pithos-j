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

package gr.grnet.pithosj.impl.finagle

import com.twitter.finagle.httpx.RequestBuilder
import gr.grnet.pithosj.TestBase
import org.junit.Test

class FinagleClientTest extends TestBase {
  def newClient(url: String) = FinagleClientFactory.newClientService(url)
  def newGet(url: String) = RequestBuilder().url(url).buildGet()
  def get(url: String) = newClient(url).apply(newGet(url))

  val httpURL = "http://google.com"
  val httpsURL = "https://google.com"
  val httpClient = newClient(httpURL)
  val httpsClient = newClient(httpsURL)

  @Test
  def oneHttpCall(): Unit = {
    val f = httpClient(newGet(httpURL))
    assertResult(f)
  }

  @Test
  def oneHttpsCall(): Unit = {
    val f = httpsClient(newGet(httpsURL))
    assertResult(f)
  }
}
