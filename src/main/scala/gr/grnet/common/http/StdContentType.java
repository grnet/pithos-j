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
 * Provides <code>HTTP</code> content types used by the library.
 *
 * @author Christos KK Loverdos <loverdos@gmail.com>
 */
public enum StdContentType implements IContentType {
    Application_Directory("application/directory"),
    Application_Folder("application/folder"),
    Text_Plain("text/plain"),
    Text_Html("text/html"),
    Application_Json("application/json");

    private final String contentType;

    StdContentType(String contentType) {
        this.contentType = contentType;
    }

    public String contentType() {
        return contentType;
    }

    public boolean is(String contentType) {
        return this.contentType.equals(contentType);
    }
}
