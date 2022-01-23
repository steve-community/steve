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
package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.repository.dto.ChargingProfileAssignment;
import de.rwth.idsg.steve.web.dto.ChargingProfileAssignmentQueryForm;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import de.rwth.idsg.steve.web.dto.ChargingProfileQueryForm;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.11.2018
 */
public interface ChargingProfileRepository {

    // -------------------------------------------------------------------------
    // OCPP operations
    // -------------------------------------------------------------------------

    void setProfile(int chargingProfilePk, String chargeBoxId, int connectorId);

    void clearProfile(int chargingProfilePk, String chargeBoxId);

    void clearProfile(@NotNull String chargeBoxId,
                      @Nullable Integer connectorId,
                      @Nullable ChargingProfilePurposeType purpose,
                      @Nullable Integer stackLevel);

    // -------------------------------------------------------------------------
    // CRUD stuff
    // -------------------------------------------------------------------------

    List<ChargingProfileAssignment> getAssignments(ChargingProfileAssignmentQueryForm query);

    List<ChargingProfile.BasicInfo> getBasicInfo();

    List<ChargingProfile.Overview> getOverview(ChargingProfileQueryForm form);

    ChargingProfile.Details getDetails(int chargingProfilePk);

    int add(ChargingProfileForm form);

    void update(ChargingProfileForm form);

    void delete(int chargingProfilePk);
}
