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

package gr.grnet.pithosj.core.keymap

import gr.grnet.pithosj.core.date.ParsedDate
import gr.grnet.pithosj.core.http.Headers

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
object HeaderKeys {
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
    final val Content_Type = HeaderKey[String](Headers.Standard.Content_Type)
    final val Content_Length = HeaderKey[Long](Headers.Standard.Content_Length)
    final val Content_Encoding = HeaderKey[String](Headers.Standard.Content_Encoding)
    final val Content_Disposition = HeaderKey[String](Headers.Standard.Content_Disposition)
    final val Content_Language = HeaderKey[String](Headers.Standard.Content_Language)
    final val Date = HeaderKey[ParsedDate](Headers.Standard.Date)
    final val Last_Modified = HeaderKey[ParsedDate](Headers.Standard.Last_Modified)
    final val ETag = HeaderKey[String](Headers.Standard.ETag)
    final val Server = HeaderKey[String](Headers.Standard.Server)

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
    final val Destination = HeaderKey[String](Headers.Pithos.Destination)

    final val X_Auth_Token = HeaderKey[String](Headers.Pithos.X_Auth_Token)

    final val X_Copy_From = HeaderKey[String](Headers.Pithos.X_Copy_From)

    final val X_Container_Block_Hash = HeaderKey[String](Headers.Pithos.X_Container_Block_Hash)
    final val X_Container_Block_Size = HeaderKey[Long](Headers.Pithos.X_Container_Block_Size)
    final val X_Container_Object_Meta = HeaderKey[String](Headers.Pithos.X_Container_Object_Meta)
    final val X_Container_Object_Count = HeaderKey[Int](Headers.Pithos.X_Container_Object_Count)
    final val X_Container_Bytes_Used = HeaderKey[Long](Headers.Pithos.X_Container_Bytes_Used)

    final val X_Account_Bytes_Used = HeaderKey[Long](Headers.Pithos.X_Account_Bytes_Used)
    final val X_Account_Container_Count = HeaderKey[Int](Headers.Pithos.X_Account_Container_Count)
    final val X_Account_Policy_Quota = HeaderKey[Long](Headers.Pithos.X_Account_Policy_Quota)
    final val X_Account_Policy_Versioning = HeaderKey[String](Headers.Pithos.X_Account_Policy_Versioning)

    final val X_Object_Hash = HeaderKey[String](Headers.Pithos.X_Object_Hash)
    final val X_Object_UUID = HeaderKey[String](Headers.Pithos.X_Object_UUID)
    final val X_Object_Version = HeaderKey[String](Headers.Pithos.X_Object_Version)
    final val X_Object_Version_Timestamp = HeaderKey[ParsedDate](Headers.Pithos.X_Object_Version_Timestamp)
    final val X_Object_Modified_By = HeaderKey[String](Headers.Pithos.X_Object_Modified_By)
    final val X_Object_Manifest = HeaderKey[String](Headers.Pithos.X_Object_Manifest)
    final val X_Object_Sharing = HeaderKey[String](Headers.Pithos.X_Object_Sharing)
    final val X_Object_Shared_By = HeaderKey[String](Headers.Pithos.X_Object_Shared_By)
    final val X_Object_Allowed_To = HeaderKey[String](Headers.Pithos.X_Object_Allowed_To)
    final val X_Object_Public = HeaderKey[String](Headers.Pithos.X_Object_Public)
    final val X_Object_Meta_Star = HeaderKey[String](Headers.Pithos.X_Object_Meta_Star)
    final val X_Source_Account = HeaderKey[String](Headers.Pithos.X_Source_Account)

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