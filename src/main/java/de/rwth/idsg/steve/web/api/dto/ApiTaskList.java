/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2023 SteVe Community Team
 * All Rights Reserved.
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.web.api.dto;

import de.rwth.idsg.steve.repository.dto.TaskOverview;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Frank Brosi
 * @since 18.10.2023
 */

@Getter
@Setter
//@RequiredArgsConstructor
public class ApiTaskList {
    @ApiModelProperty(value = "List of tasks")
    List<TaskOverview> tasks;
}
