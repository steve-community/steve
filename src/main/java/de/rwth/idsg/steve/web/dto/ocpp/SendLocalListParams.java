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

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 03.01.2015
 */
@Setter
@Getter
public class SendLocalListParams extends MultipleChargePointSelect {

    @NotNull(message = "List version is required")
    private Integer listVersion;

    @NotNull(message = "Update Type is required")
    private SendLocalListUpdateType updateType = SendLocalListUpdateType.FULL;

    @NotNull
    private Boolean sendEmptyListWhenFull = Boolean.FALSE;

    private List<String> deleteList;
    private List<String> addUpdateList;

    @AssertTrue(message = "When Update Type is DIFFERENTIAL, either Add/Update or Delete list should not be empty")
    public boolean isValidWhenDifferential() {
        return SendLocalListUpdateType.FULL.equals(updateType) || !getDeleteList().isEmpty() || !getAddUpdateList().isEmpty();
    }

    @AssertTrue(message = "The Add/Update and Delete lists should have no elements in ocpp")
    public boolean isDisjoint() {
        return Collections.disjoint(getDeleteList(), getAddUpdateList());
    }

    public List<String> getDeleteList() {
        if (deleteList == null) {
            deleteList = new ArrayList<>();
        }
        return deleteList;
    }

    public List<String> getAddUpdateList() {
        if (addUpdateList == null) {
            addUpdateList = new ArrayList<>();
        }
        return addUpdateList;
    }
}
