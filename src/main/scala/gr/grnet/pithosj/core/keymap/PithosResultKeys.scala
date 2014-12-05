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

import gr.grnet.common.key.ResultKey
import gr.grnet.pithosj.core.command.result.{ContainerData, ObjectInPathData}

/**
 * Miscellaneous type-safe keys.
 */
object PithosResultKeys {

  private[this] def name(s: String) = "result.successData." + s

  final val ResponseBody = ResultKey[String](name("response.body"))
  final val ListContainers = ResultKey[List[ContainerData]](name("list.containers"))
  final val ListObjectsInPath = ResultKey[List[ObjectInPathData]](name("list.objects.in.path"))
  final val ContainerQuota = ResultKey[Long]("quota") // the name is exactly as it comes from ListContainersCommand command

  /**
   * Keys for data specified in commands.
   */
  object Commands {
    private[this] def name(s: String) = "command.successData." + s

    final val Container = ResultKey[String](name("container"))
    final val Path = ResultKey[String](name("path"))
    final val SourceContainer = ResultKey[String](name("source.container"))
    final val SourcePath = ResultKey[String](name("source.path"))
    final val TargetContainer = ResultKey[String](name("target.container"))
    final val TargetPath = ResultKey[String](name("target.path"))
  }
}
