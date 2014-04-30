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

package gr.grnet.common.date

import java.util.Date

/**
 * A [[java.util.Date]] parser.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
trait DateParser {
  /**
   * The description of this parser. This can be either a free text description or,
   * in case of a [[java.text.DateFormat]]-based implementation, the format string.
   */
  def description: String

  /**
   * Tries to parse the given date.
   * The implementation must not throw an [[java.lang.Exception]]. In particular,
   * it must not throw a [[java.text.ParseException]], which is common in the case of a
   * [[java.text.SimpleDateFormat]].
   */
  def parse(source: String): Option[Date]
}
