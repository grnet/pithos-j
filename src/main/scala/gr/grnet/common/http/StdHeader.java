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

package gr.grnet.common.http;

/**
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
public enum StdHeader implements IHeader {
    Content_Type("Content-Type"),
    Content_Length("Content-Length"),
    Content_Encoding("Content-Encoding"),
    Content_Disposition("Content-Disposition"),
    Content_Language("Content-Language"),
    Date("Date"),
    Last_Modified("Last-Modified"),
    ETag("ETag"),
    Server("Server"),
    WWW_Authenticate("WWW-Authenticate"),
    Accept("Accept");

    private final String headerName;

    StdHeader(String headerName) {
        this.headerName = headerName;
    }

    public String headerName() {
        return headerName;
    }
}
