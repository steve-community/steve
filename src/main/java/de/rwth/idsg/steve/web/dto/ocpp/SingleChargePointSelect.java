/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;

/**
 * Why a list, if the list size == 1?
 * To keep the method calls and data types (for ex: tasks api) consistent for both cases.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.12.2014
 */
@Getter
@Setter
public class SingleChargePointSelect implements ChargePointSelection {

    @NotNull(message = "Charge point selection is required")
    @Size(min = 1, max = 1, message = "It is required to select exactly 1 charge point")
    private List<ChargePointSelect> chargePointSelectList = Collections.emptyList();
}
