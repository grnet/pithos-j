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

package gr.grnet.pithosj.core

import com.ning.http.client.FluentCaseInsensitiveStringsMap
import gr.grnet.pithosj.core.Const.IHeader
import java.util.{List ⇒ JList, Map ⇒ JMap, Collection ⇒ JCollection, ArrayList ⇒ JArrayList}
import java.util.{Set ⇒ JSet, HashSet ⇒ JHashSet, HashMap ⇒ JHashMap}

/**
 * Abstraction for HTTP headers and parameters.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final class MetaData private(_map: FluentCaseInsensitiveStringsMap) {
  _map.isEmpty // Check NPE

  private val map = _map

  def this() = this(new FluentCaseInsensitiveStringsMap())

  def isEmpty = map.isEmpty

  def setOne(header: IHeader, value: String): this.type = {
    setOne(header.header(), value)
    this
  }

  def setOne(key: String, value: String): this.type =  {
    require(key ne null, "key ne null")
    if(value ne null) {
      map.put(key, Helpers.jListOne(value))
    }
    this
  }

  def set(key: String, value: JList[String]): this.type =  {
    require(key ne null, "key ne null")
    if((value ne null) && !value.isEmpty) {
      map.put(key, value)
    }
    this
  }

  def has(key: String): Boolean = {
    require(key ne null, "key ne null")
    map.containsKey(key)
  }

  def getJList(key: String): JList[String] = {
    require(key ne null, "key ne null")
    if(!has(key)) {
      throw new PithosException("Key '%s' does not exist", key)
    }
    val list = map.get(key)
    new JArrayList[String](list)
  }

  def getOne(key: String): String = {
    getJList(key).get(0)
  }

  def getJList(header: IHeader): JList[String] = {
    this getJList header.header()
  }

  def getOne(header: IHeader): String = {
    this getOne header.header()
  }

  def keysJSet(): JSet[String] = {
    val set = new JHashSet[String](map.keySet())
    set
  }

  def size = map.size()

  def foreach[U](f: (String, JList[String]) ⇒ U) {
    val entries = map.entrySet().iterator()
    while(entries.hasNext) {
      val entry = entries.next()
      val key = entry.getKey
      val value = entry.getValue
      f(key, value)
    }
  }

//  def filter(p: (String, JList[String]) ⇒ Boolean): MetaData = {
//    val newmd = new MetaData
//    val entries = map.entrySet().iterator()
//    while(entries.hasNext) {
//      val entry = entries.next()
//      val key = entry.getKey
//      val value = entry.getValue
//      if(p(key, value)) {
//        newmd.set(key, value)
//      }
//    }
//    newmd
//  }

  def toJMap: JMap[String, JCollection[String]] = {
    val jmap = new JHashMap[String, JCollection[String]]()
    this.foreach { case (headerName, headerValues) ⇒
      jmap.put(headerName, headerValues)
    }
    jmap
  }

//  def newAdded(other: MetaData): MetaData = {
//    val newmd = new MetaData(this.map)
//    newmd.map.addAll(other.map)
//    newmd
//  }
//
//  def ++(other: MetaData): MetaData = newAdded(other)

  override def toString = {
    "MetaData(%s)".format(asScala(map).mkString(", "))
  }
}

object MetaData {
  final val Empty = new MetaData
}
