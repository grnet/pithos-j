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

import com.ckkloverdos.env.MutableEnv
import gr.grnet.pithosj.core.http.IHeader

/**
 * A generic dictionary that can be accessed with type-safe keys.
 *
 * @see [[gr.grnet.pithosj.core.keymap.PithosKey]]
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final class KeyMap private[keymap](private val env: MutableEnv) {
  require(env ne null)

  def set[T: Manifest](key: PithosKey[T], value: T): this.type = {
    env + (key, value)
    this
  }

  def setString(keyName: String, value: String): this.type = {
    set(new PithosKey[String](keyName), value)
  }

  def get[T: Manifest](key: PithosKey[T]): Option[T] = {
    env.get(key)
  }

  def getEx[T: Manifest](key: PithosKey[T]): T = {
    env.getEx[T](key)
  }

  def get[T: Manifest](key: PithosKey[T], value: T): T = {
    env.getOrElse(key, value)
  }

  def contains[T: Manifest](key: PithosKey[T]): Boolean = {
    env.contains(key)
  }

  /**
   * Returns all the values with keys of the given name.
   */
  def getForName(keyName: String): Seq[Any] = {
    env.toMap.filter { case (key, value) ⇒
      key == keyName
    }.values.toSeq
  }

  def getOneForName(keyName: String): Option[Any] = {
    val unknown = getForName(keyName)
    if(unknown.size == 0) {
      return None
    }

    Some(unknown.apply(0))
  }

  def getOneString(header: IHeader): Option[String] = {
    getOneString(header.headerName())
  }

  def getOneString(keyName: String): Option[String] = {
    getOneForName(keyName) map {
      case null ⇒ null
      case s ⇒ String.valueOf(s)
    }
  }

  def toMap: Map[String, Any] = {
    env.toMap
  }

  override def toString = {
    "KeyMap(%s)".format(
      env.toMap.map { case (k, v) ⇒
        val vt = v match {
          case null ⇒ ""
          case anyRef: AnyRef ⇒ " [:%s]".format(anyRef.getClass.getSimpleName)
        }
        (k, "%s%s".format(v, vt))
      }.mkString(", ")
    )
  }
}

object KeyMap {
  def apply(): KeyMap = new KeyMap(MutableEnv())
  def apply(other: KeyMap): KeyMap = new KeyMap(MutableEnv() ++ other.env)
}