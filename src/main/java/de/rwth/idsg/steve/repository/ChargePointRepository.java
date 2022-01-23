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

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.08.2014
 */
public interface ChargePointRepository {
    Optional<String> getRegistrationStatus(String chargeBoxId);
    List<ChargePointSelect> getChargePointSelect(OcppProtocol protocol, List<String> inStatusFilter);
    List<String> getChargeBoxIds();
    Map<String, Integer> getChargeBoxIdPkPair(List<String> chargeBoxIdList);

    List<ChargePoint.Overview> getOverview(ChargePointQueryForm form);
    ChargePoint.Details getDetails(int chargeBoxPk);

    default List<ConnectorStatus> getChargePointConnectorStatus() {
        return getChargePointConnectorStatus(null);
    }

    List<ConnectorStatus> getChargePointConnectorStatus(@Nullable ConnectorStatusForm form);

    List<Integer> getNonZeroConnectorIds(String chargeBoxId);

    void addChargePointList(List<String> chargeBoxIdList);
    int addChargePoint(ChargePointForm form);
    void updateChargePoint(ChargePointForm form);
    void deleteChargePoint(int chargeBoxPk);
}
