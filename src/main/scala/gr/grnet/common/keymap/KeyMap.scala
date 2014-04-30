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

package gr.grnet.common.keymap

import com.ckkloverdos.env.MutableEnv
import com.ckkloverdos.key.{TKeyOnly, TKey}
import gr.grnet.common.http.IHeader

/**
 * A generic dictionary that can be accessed with type-safe keys.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
final class KeyMap private[keymap](private val env: MutableEnv) {
  require(env ne null)

  def set[T: Manifest](key: TKey[T], value: T): this.type = {
    env +(key, value)
    this
  }

  def setString(keyName: String, value: String): this.type = {
    set(new TKeyOnly[String](keyName), value)
  }

  def get[T: Manifest](key: TKey[T]): Option[T] = {
    env.get(key)
  }

  def getEx[T: Manifest](key: TKey[T]): T = {
    env.getEx[T](key)
  }

  def get[T: Manifest](key: TKey[T], value: T): T = {
    env.getOrElse(key, value)
  }

  def contains[T: Manifest](key: TKey[T]): Boolean = {
    env.contains(key)
  }

  /**
   * Returns all the values with keys of the given name.
   */
  def getForName(keyName: String): Seq[Any] = {
    env.toMap.filter {
      case (key, value) ⇒
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
      env.toMap.map {
        case (k, v) ⇒
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