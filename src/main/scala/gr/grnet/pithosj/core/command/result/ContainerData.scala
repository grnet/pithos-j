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

package gr.grnet.pithosj.core.command.result

import gr.grnet.common.date.ParsedDate
import typedkey.env.ImEnv

/**
 * Holds parsed result data for the [[gr.grnet.pithosj.core.command.ListContainersCommand]] command.
 */

case class ContainerData(
  container: String,
  count: Int,
  lastModified: ParsedDate,
  bytes: Long,
  policy: ImEnv // Use PithosResultKeys.ContainerQuota to get "quota"
)
