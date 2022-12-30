/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
package net.parkl.ocpp.service.cs;


import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import net.parkl.ocpp.entities.OcppChargeBox;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface ChargePointService {
    Optional<String> getRegistrationStatus(String chargeBoxId);

    List<ChargePointSelect> getChargePointSelect(OcppProtocol protocol, List<String> statusFilter);

    List<String> getChargeBoxIds();

    Map<String, Integer> getChargeBoxIdPkPair(List<String> chargeBoxIdList);

    List<ChargePoint.Overview> getOverview(ChargePointQueryForm form);

    ChargePoint.Details getDetails(int chargeBoxPk);

    default List<ConnectorStatus> getChargePointConnectorStatus() {
        return getChargePointConnectorStatus(null);
    }

    List<ConnectorStatus> getChargePointConnectorStatus(@Nullable ConnectorStatusForm form);

    List<Integer> getNonZeroConnectorIds(String chargeBoxId);

    void addChargePoint(ChargePointForm form);

    void updateChargePoint(ChargePointForm form);

    void deleteChargePoint(int chargeBoxPk);

    void addChargePointList(List<String> chargeBoxIdList);


    void updateChargeboxHeartbeat(String chargeBoxId, DateTime now);

    void updateEndpointAddress(String chargeBoxId, String endpointAddress);

    void updateChargebox(UpdateChargeboxParams params);

    void updateChargeboxFirmwareStatus(String chargeBoxIdentity, String status);

    void updateChargeboxDiagnosticsStatus(String chargeBoxIdentity, String status);

    List<OcppChargeBox> findAllChargePoints();

    OcppChargeBox findByChargeBoxId(String chargeBoxId);

    boolean shouldInsertConnectorStatusAfterTransactionMsg(String chargeBoxId);

    Map<String, OcppChargeBox> getIdChargeBoxMap();

    List<OcppChargeBox> getAllChargeBoxes();

    void updateOcppProtocol(String chargeBoxId, OcppProtocol protocol);
}
