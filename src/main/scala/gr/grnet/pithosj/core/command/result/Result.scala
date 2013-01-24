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

package gr.grnet.pithosj.core.command
package result

import gr.grnet.pithosj.core.Const.IHeader
import gr.grnet.pithosj.core.MetaData
import java.util.{List ⇒ JList}

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait Result {
  def responseHeaders: MetaData

  def statusCode: Int

  def statusText: String

  def completionMillis: Long

  def isSuccess: Boolean

  final def isStatusCode(statusCode: Int): Boolean = this.statusCode == statusCode

  final def is200 = isStatusCode(200)
  final def is201 = isStatusCode(201)
  final def is204 = isStatusCode(204)
  final def is400 = isStatusCode(400)
  final def is401 = isStatusCode(401)
  final def is403 = isStatusCode(403)
  final def is404 = isStatusCode(404)
  final def is503 = isStatusCode(503)

  final def getHeader(name: String): String = responseHeaders.getOne(name)
  final def getIntHeader(name: String): Int = getHeader(name).toInt
  final def getLongHeader(name: String): Long = getHeader(name).toLong
  final def getHeadersJList(name: String): JList[String] = responseHeaders.getJList(name)
  final def getHeader(name: IHeader): String = responseHeaders.getOne(name)
  final def getIntHeader(name: IHeader): Int = getHeader(name).toInt
  final def getLongHeader(name: IHeader): Long = getHeader(name).toLong
  final def getHeadersJList(name: IHeader): JList[String] = responseHeaders.getJList(name)
}

abstract class ResultSkeleton(
  val responseHeaders: MetaData,
  val statusCode: Int,
  val statusText: String,
  val completionMillis: Long
) extends Result

case class SimpleResult(
    command: Command[SimpleResult],
    responseHeaders: MetaData,
    statusCode: Int,
    statusText: String,
    completionMillis: Long,
    isSuccess: Boolean
) extends Result
