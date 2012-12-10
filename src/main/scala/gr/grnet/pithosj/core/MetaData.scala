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

package gr.grnet.pithosj.core

import com.ning.http.client.FluentCaseInsensitiveStringsMap
import com.ning.http.client.simple.HeaderMap
import java.util

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final class MetaData {
  private[this] val map = new FluentCaseInsensitiveStringsMap()

  def isEmpty = map.isEmpty

  def setOne(key: String, value: String) {
    require(key ne null, "key ne null")
    if(value ne null) {
      map.put(key, Helpers.jListOne(value))
    }
  }

  def set(key: String, value: util.List[String]) {
    require(key ne null, "key ne null")
    if((value ne null) && !value.isEmpty) {
      map.put(key, value)
    }
  }

  def has(key: String): Boolean = {
    require(key ne null, "key ne null")
    map.containsKey(key)
  }

  def get(key: String): util.List[String] = {
    require(key ne null, "key ne null")
    if(!has(key)) {
      throw new PithosException("Key '%s' does not exist", key)
    }
    val list = map.get(key)
    new util.ArrayList[String](list)
  }

  def getOne(key: String): String = {
    get(key).get(0)
  }

  def keys(): util.Set[String] = {
    val set = new util.HashSet[String](map.keySet())
    set
  }

  def size = map.size()

  override def toString = {
    "MetaData(%s)".format(asScala(map).mkString(", "))
  }
}
