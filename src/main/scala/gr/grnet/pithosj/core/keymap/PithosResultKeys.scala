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

import gr.grnet.common.keymap.ResultKey
import gr.grnet.pithosj.core.command.result.{ObjectInPathData, ContainerData}

/**
 * Miscellaneous type-safe keys.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
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