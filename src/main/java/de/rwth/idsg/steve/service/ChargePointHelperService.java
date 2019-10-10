/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2019 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.web.dto.OcppJsonStatus;
import de.rwth.idsg.steve.web.dto.Statistics;
import ocpp.cs._2015._10.RegistrationStatus;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.03.2015
 */
public interface ChargePointHelperService {
    RegistrationStatus getRegistrationStatus(String chargeBoxId);

    Statistics getStats();
    List<OcppJsonStatus> getOcppJsonStatus();
    List<ChargePointSelect> getChargePointsV12();
    List<ChargePointSelect> getChargePointsV15();
    List<ChargePointSelect> getChargePointsV16();

    List<UnidentifiedIncomingObject> getUnknownChargePoints();
    void removeUnknown(String chargeBoxId);
    void removeUnknown(List<String> chargeBoxIdList);
}
