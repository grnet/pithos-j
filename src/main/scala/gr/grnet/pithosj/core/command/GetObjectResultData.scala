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

package gr.grnet.pithosj.core.command

import gr.grnet.common.date.ParsedDate
import java.io.OutputStream

/**
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
case class GetObjectResultData(
  stream: OutputStream,
  container: String,
  path: String,
  ETag: Option[String],
  Content_Type: Option[String],
  Content_Length: Option[Long],
  Last_Modified: Option[ParsedDate],
  X_Object_Hash: Option[String],
  X_Object_Modified_By: Option[String],
  X_Object_Version_Timestamp: Option[ParsedDate],
  X_Object_UUID: Option[String],
  X_Object_Version: Option[String]
)
