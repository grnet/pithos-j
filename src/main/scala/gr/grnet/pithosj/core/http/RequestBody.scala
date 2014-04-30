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

package gr.grnet.pithosj.core.http

import java.io.{InputStream, File}
import org.jboss.netty.buffer.ChannelBuffer

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
sealed trait RequestBody

case class FileRequestBody(body: File) extends RequestBody
case class BytesRequestBody(body: Array[Byte]) extends RequestBody
case class StringRequestBody(body: String) extends RequestBody
case class InputStreamRequestBody(body: InputStream) extends RequestBody
case class ChannelBufferRequestBody(body: ChannelBuffer) extends RequestBody
