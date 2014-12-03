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

package gr.grnet.pithosj.core.http;

import gr.grnet.common.http.IHeader;

public enum PithosHeader implements IHeader {
  Destination("Destination"),

  X_Auth_Token("X-Auth-Token"),

  X_Copy_From("X-Copy-From"),

  X_Container_Block_Hash("X-Container-Block-Hash"),
  X_Container_Object_Meta("X-Container-Object-Meta"),
  X_Container_Object_Count("X-Container-Object-Count"),
  X_Container_Block_Size("X-Container-Block-Size"),
  X_Container_Bytes_Used("X-Container-Bytes-Used"),

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

  private final String headerName;

  PithosHeader(String headerName) {
    this.headerName = headerName;
  }

  public String headerName() {
    return headerName;
  }
}
