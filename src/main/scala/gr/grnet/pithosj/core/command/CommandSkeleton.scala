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

import com.ning.http.client.AsyncHandler.STATE
import com.ning.http.client.HttpResponseBodyPart
import gr.grnet.pithosj.core.Const.Headers
import gr.grnet.pithosj.core.MetaData
import gr.grnet.pithosj.core.http.RequestBody
import org.slf4j.LoggerFactory

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait CommandSkeleton[R] extends Command[R] {
  protected val logger = LoggerFactory.getLogger(this.getClass)

  def onBodyPartReceivedOpt: Option[HttpResponseBodyPart ⇒ STATE] = None

  /**
   * The HTTP query parameters that are set by this command.
   */
  val queryParameters = MetaData.Empty

  /**
   * The HTTP request headers that are set by this command.
   */
  val requestHeaders = newDefaultRequestHeaders

  /**
   * Provides the HTTP request body, if any.
   */
  val requestBodyOpt: Option[RequestBody] = None

  protected def newDefaultRequestHeaders: MetaData = {
    val metadata = new MetaData()
    metadata.setOne(Headers.Pithos.X_Auth_Token, connectionInfo.userToken)
  }

  protected def newQueryParameters: MetaData = {
    new MetaData()
  }
}
