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

/**
 * Provides [[gr.grnet.common.date.DateParser]]s for dates returned from
 * Pithos+ REST API calls.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
object DateParsers {
  /**
   * Parses a date according to ISO 8601:2004 extended representation.
   * This is used for all dates in the CDMI protocol.
   *
   */
  final val CdmiDateParser = new SimpleDateFormatParser("yyyy-mm-dd'T'hh:mm:ss.ssssssZ")

  /**
   * Parses a date according to the `"yyyy-MM-dd'T'HH:mm:ss.SSSSSSX"` format.
   * Note that we need at least `JDK 1.7` for the `X` in the end.
   */
  final val Format1Parser = new SimpleDateFormatParser("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")

  /**
   * Parses a date according to the `"EEE, d MMM yyyy HH:mm:ss z"` format.
   */
  final val Format2Parser = new SimpleDateFormatParser("EEE, d MMM yyyy HH:mm:ss z")

  final val Format3Parser = new DateParser {
    /**
     * The description of this parser. This can be either a free text description or,
     * in case of a [[java.text.DateFormat]]-based implementation, the format string.
     */
    def description = "Parses dates from milliseconds that represented as a double, e.g. 1223.11"

    /**
     * Tries to parse the given date.
     * The implementation must not throw an [[java.lang.Exception]]. In particular,
     * it must not throw a [[java.text.ParseException]], which is common in the case of a
     * [[java.text.SimpleDateFormat]].
     */
    def parse(source: String) = {
      try Some(new java.util.Date((source.toDouble * 1000).toLong))
      catch {
        case e: Exception ⇒
          None
      }
    }

    override def toString = "DateParser(millis: Double)"
  }

  /**
   * Tries to parse the given (in string format) date via the provided [[gr.grnet.common.date.DateParser]]s.
   * Returns the [[gr.grnet.common.date.ParsedDate]] for the first parser that succeeded.
   */
  def parse(source: String, parser0: DateParser, parsers: DateParser*): ParsedDate = {
    parser0.parse(source) match {
      case some@Some(_) ⇒
        ParsedDate(some, source, parser0)
      case _ ⇒
        parsers.view.
          map(parser ⇒ ParsedDate(parser.parse(source), source, parser)).
          find(_.isParsed).
          getOrElse(ParsedDate(None, source, parser0))
    }
  }
}
