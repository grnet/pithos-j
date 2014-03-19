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

package gr.grnet.common


/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
package object text {
  implicit class RemovePrefix(val s: String) extends AnyVal {
    def removePrefix(prefix: String): String =
      if(s.startsWith(prefix)) s.substring(prefix.length)
      else s
  }

  implicit class NoLeadingSlash(val s: String) extends AnyVal {
    def noLeadingSlash: String =
      if(s.length > 0 && s.charAt(0) == '/') s.substring(1).noLeadingSlash
      else s
  }

  implicit class NoTrailingSlash(val s: String) extends AnyVal {
    def noTrailingSlash: String =
      if(s.length > 0 && s.charAt(s.length - 1) == '/') s.substring(0, s.length - 1).noTrailingSlash
      else s
  }

  implicit class NormalizeUri(val uri: String) extends AnyVal {
    def normalizeUri: String = uri.replaceAll("/+", "/")
  }

  implicit class UriToList(val uri: String) extends AnyVal {
    def uriToList: List[String] = uri.split("/").toList
  }

  implicit class ParentUri(val uri: String) extends AnyVal {
    def parentUri: String = uri.substring(0, uri.noTrailingSlash.lastIndexOf('/') + 1)
  }
}