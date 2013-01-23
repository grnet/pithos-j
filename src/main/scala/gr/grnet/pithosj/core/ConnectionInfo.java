/*
 * Copyright 2012-2013 GRNET S.A. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 *   1. Redistributions of source code must retain the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials
 *      provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY GRNET S.A. ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GRNET S.A OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and
 * documentation are those of the authors and should not be
 * interpreted as representing official policies, either expressed
 * or implied, of GRNET S.A.
 */

package gr.grnet.pithosj.core;

/**
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
public final class ConnectionInfo {
  public final String baseURL;
  public final String userID;
  public final String userToken;

  public ConnectionInfo(String baseURL, String userID, String userToken) {
    if(baseURL == null) {
      throw new IllegalArgumentException("null baseURL");
    }
    if(userID == null) {
      throw new IllegalArgumentException("null userID");
    }
    if(userToken == null) {
      throw new IllegalArgumentException("null userToken");
    }
    this.baseURL = baseURL;
    this.userID = userID;
    this.userToken = userToken;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) return true;
    if(o == null || getClass() != o.getClass()) return false;

    final ConnectionInfo that = (ConnectionInfo) o;

    if(!baseURL.equals(that.baseURL)) return false;
    if(!userID.equals(that.userID)) return false;
    if(!userToken.equals(that.userToken)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = baseURL.hashCode();
    result = 31 * result + userID.hashCode();
    result = 31 * result + userToken.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return String.format("ConnectionInfo(%s, %s)", baseURL, userID);
  }
}
