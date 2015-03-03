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

package gr.grnet.pithosj.core.keymap

import gr.grnet.common.date.ParsedDate
import gr.grnet.common.http.StdHeader
import gr.grnet.common.key.HeaderKey
import gr.grnet.pithosj.core.http
import gr.grnet.pithosj.core.http.PithosHeader

object PithosHeaderKeys {
  final val AllKeys = Standard.AllKeys ++ Pithos.AllKeys

  object Standard {
    final val Content_Type = HeaderKey[String](StdHeader.Content_Type)
    final val Content_Length = HeaderKey[Long](StdHeader.Content_Length)
    final val Content_Encoding = HeaderKey[String](StdHeader.Content_Encoding)
    final val Content_Disposition = HeaderKey[String](StdHeader.Content_Disposition)
    final val Content_Language = HeaderKey[String](StdHeader.Content_Language)
    final val Date = HeaderKey[ParsedDate](StdHeader.Date)
    final val Last_Modified = HeaderKey[ParsedDate](StdHeader.Last_Modified)
    final val ETag = HeaderKey[String](StdHeader.ETag)
    final val Server = HeaderKey[String](StdHeader.Server)

    final val AllKeys = Set[HeaderKey[_]](
      Content_Type,
      Content_Length,
      Content_Encoding,
      Content_Disposition,
      Content_Language,
      Date,
      ETag,
      Server
    )
  }

  object Pithos {
    final val Destination = HeaderKey[String](PithosHeader.Destination)

    final val X_Auth_Token = HeaderKey[String](http.PithosHeader.X_Auth_Token)

    final val X_Copy_From = HeaderKey[String](http.PithosHeader.X_Copy_From)

    final val X_Container_Block_Hash = HeaderKey[String](http.PithosHeader.X_Container_Block_Hash)
    final val X_Container_Block_Size = HeaderKey[Long](http.PithosHeader.X_Container_Block_Size)
    final val X_Container_Object_Meta = HeaderKey[String](http.PithosHeader.X_Container_Object_Meta)
    final val X_Container_Object_Count = HeaderKey[Int](http.PithosHeader.X_Container_Object_Count)
    final val X_Container_Bytes_Used = HeaderKey[Long](http.PithosHeader.X_Container_Bytes_Used)

    final val X_Account_Bytes_Used = HeaderKey[Long](http.PithosHeader.X_Account_Bytes_Used)
    final val X_Account_Container_Count = HeaderKey[Int](http.PithosHeader.X_Account_Container_Count)
    final val X_Account_Policy_Quota = HeaderKey[Long](http.PithosHeader.X_Account_Policy_Quota)
    final val X_Account_Policy_Versioning = HeaderKey[String](http.PithosHeader.X_Account_Policy_Versioning)

    final val X_Object_Hash = HeaderKey[String](http.PithosHeader.X_Object_Hash)
    final val X_Object_UUID = HeaderKey[String](http.PithosHeader.X_Object_UUID)
    final val X_Object_Version = HeaderKey[String](http.PithosHeader.X_Object_Version)
    final val X_Object_Version_Timestamp = HeaderKey[ParsedDate](http.PithosHeader.X_Object_Version_Timestamp)
    final val X_Object_Modified_By = HeaderKey[String](http.PithosHeader.X_Object_Modified_By)
    final val X_Object_Manifest = HeaderKey[String](http.PithosHeader.X_Object_Manifest)
    final val X_Object_Sharing = HeaderKey[String](http.PithosHeader.X_Object_Sharing)
    final val X_Object_Shared_By = HeaderKey[String](http.PithosHeader.X_Object_Shared_By)
    final val X_Object_Allowed_To = HeaderKey[String](http.PithosHeader.X_Object_Allowed_To)
    final val X_Object_Public = HeaderKey[String](http.PithosHeader.X_Object_Public)
    final val X_Object_Meta_Star = HeaderKey[String](http.PithosHeader.X_Object_Meta_Star)
    final val X_Source_Account = HeaderKey[String](http.PithosHeader.X_Source_Account)

    final val AllKeys = Set[HeaderKey[_]](
      Destination,
      X_Auth_Token,
      X_Copy_From,
      X_Container_Block_Hash,
      X_Container_Block_Size,
      X_Container_Bytes_Used,
      X_Container_Object_Count,
      X_Container_Object_Meta,
      X_Account_Bytes_Used,
      X_Account_Container_Count,
      X_Account_Policy_Quota,
      X_Account_Policy_Versioning,
      X_Object_Hash,
      X_Object_UUID,
      X_Object_Version,
      X_Object_Version_Timestamp,
      X_Object_Modified_By,
      X_Object_Manifest,
      X_Object_Sharing,
      X_Object_Shared_By,
      X_Object_Allowed_To,
      X_Object_Public,
      X_Object_Meta_Star,
      X_Source_Account
    )
  }
}
