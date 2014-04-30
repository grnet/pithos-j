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

package gr.grnet.pithosj.impl.asynchttp;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import gr.grnet.pithosj.api.PithosApi;

/**
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
public final class PithosClientFactory {
  private PithosClientFactory() {}

  public static AsyncHttpClient newDefaultAsyncHttpClient() {
    final AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder().
      setAllowPoolingConnection(true).
      setAllowSslConnectionPool(true).
      setCompressionEnabled(true).
      setFollowRedirects(true).
      setMaximumConnectionsTotal(20);

    return new AsyncHttpClient(builder.build());
  }

  public static PithosApi newPithosClient(AsyncHttpClient asyncHttp) {
    return new AsyncHttpPithosClient(asyncHttp);
  }

  public static PithosApi newPithosClient() {
    return newPithosClient(newDefaultAsyncHttpClient());
  }
}
