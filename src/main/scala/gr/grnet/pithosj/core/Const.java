/*
 * Copyright 20122-13 GRNET S.A. All rights reserved.
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

import scala.util.parsing.json.JSON;
import scala.xml.XML;

import java.text.SimpleDateFormat;

/**
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
public final class Const {
  private Const() {}

  public static enum ContentTypes {
    Application_Directory("application/directory");

    private final String contentType;

    private ContentTypes(String contentType) {
      this.contentType = contentType;
    }

    public String contentType() {
      return contentType;
    }
  }

  public static final class Dates {
    private Dates() {}

    public static final SimpleDateFormat Format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX");
    public static final SimpleDateFormat Format2 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
  }

  public static enum RequestParams {
    Format("format"),
    Version("version"),
    Path("path");

    private final String requestParam;

    private RequestParams(String requestParam) {
      this.requestParam = requestParam;
    }

    public String requestParam() {
      return requestParam;
    }
  }

  public static enum ResponseFormats {
    NOFORMAT(""),
    XML("xml"),
    JSON("json");

    private final String responseFormat;

    private ResponseFormats(String responseFormat) {
      this.responseFormat = responseFormat;
    }

    public String responseFormat() {
      return responseFormat;
    }

    public final boolean hasValue() {
      return this != NOFORMAT;
    }

    public final boolean isXML() {
      return this == XML;
    }

    public final boolean isJSON() {
      return this == JSON;
    }
  }

  public static interface IHeader {
    public String header();
  }

  public static final class Headers {
    private Headers() {}

    public static enum Standard implements IHeader {
      Content_Type ("Content-Type"),
      Content_Length("Content-Length"),
      Content_Encoding ("Content-Encoding"),
      Content_Disposition("Content-Disposition"),
      Content_Language("Content-Language"),
      Date("Date"),
      Last_Modified("Last-Modified"),
      ETag("ETag"),
      Server("Server");

      private final String header;

      Standard(String header) {
        this.header = header;
      }

      public String header() {
        return header;
      }
    }

    public static enum Pithos implements IHeader {
      X_Auth_Token("X-Auth-Token"),

      X_Copy_From("X-Copy-From"),

      X_Account_Bytes_Used("X-Account-Bytes-Used"),
      X_Account_Container_Count("X-Account-Container-Count"),
      X_Account_Policy_Quota("X-Account-Policy-Quota"),
      X_Account_Policy_Versioning("X-Account-Policy-Versioning"),

      X_Object_Hash("X-Object-Hash"),
      X_Object_UUID("X-Object-UUID"),
      X_Object_Version("X-Object-Version"),
      X_Object_Version_Timestamp("X-Object-Version-Timestamp"),
      X_Object_Modified_By("X-Object-Modified-By"),
      X_Object_Manifest("X-Object-Manifest"),
      X_Object_Sharing("X-Object-Sharing"),
      X_Object_Shared_By("X-Object-Shared-By"),
      X_Object_Allowed_To("X-Object-Allowed-To"),
      X_Object_Public("X-Object-Public"),
      X_Object_Meta_Star("X-Object-Meta-*"),
      X_Source_Account("X-Source-Account");


      private  final String header;
      Pithos(String header) {
        this.header = header;
      }

      public String header() {
        return header;
      }
    }
  }
}
