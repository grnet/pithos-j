/*
 * Copyright 2012 GRNET S.A. All rights reserved.
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
public final class Const {
  private Const() {}

  public static final class Headers {
    private Headers() {}

    public static final String Content_Type = "Content-Type";
    public static final String Date = "Date"; // Wed, 05 Dec 2012 14:29:28 GMT
    public static final String Content_Language= "Content-Language";
    public static final String Last_Modified = "Last-Modified";
    public static final String Server = "Server";

    public static final class Pithos {
      private Pithos() {}

      public static final String X_Auth_Token = "X_Auth_Token";
      public static final String X_Account_Bytes_Used = "X_Account_Bytes_Used";
      public static final String X_Account_Container_Count = "X-Account-Container-Count";
      public static final String X_Account_Policy_Quota = "X-Account-Policy-Quota";
      public static final String X_Account_Policy_Versioning = "X-Account-Policy-Versioning";
    }
  }
}
