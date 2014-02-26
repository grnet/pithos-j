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