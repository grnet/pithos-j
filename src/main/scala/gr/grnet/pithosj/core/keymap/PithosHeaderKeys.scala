/*
 * Copyright 2012-2014 GRNET S.A. All rights reserved.
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

package gr.grnet.pithosj.core.keymap

import gr.grnet.common.date.ParsedDate
import gr.grnet.common.http.StdHeader
import gr.grnet.common.keymap.{HeaderKey, KeyMap}
import gr.grnet.pithosj.core.http
import gr.grnet.pithosj.core.http.PithosHeader

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
object PithosHeaderKeys {
  def setFromHeader[T: Manifest](
      key: HeaderKey[T],
      input: KeyMap,
      output: KeyMap,
      f: String ⇒ T
  ) {
    for(value ← input.getOneForName(key.name)) {
      output.set(key, f(value.toString))
    }
  }

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