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
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import ocpp.cs._2015._10.MeterValue;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.08.2014
 */
public interface OcppServerRepository {

    void updateChargebox(UpdateChargeboxParams params);
    void updateOcppProtocol(String chargeBoxId, OcppProtocol protocol);
    void updateEndpointAddress(String chargeBoxIdentity, String endpointAddress);
    void updateChargeboxFirmwareStatus(String chargeBoxIdentity, String firmwareStatus);
    void updateChargeboxDiagnosticsStatus(String chargeBoxIdentity, String status);
    void updateChargeboxHeartbeat(String chargeBoxIdentity, DateTime ts);

    void insertConnectorStatus(InsertConnectorStatusParams params);

    void insertMeterValues(String chargeBoxIdentity, List<MeterValue> list, int connectorId, Integer transactionId);
    void insertMeterValues(String chargeBoxIdentity, List<MeterValue> list, int transactionId);

    int insertTransaction(InsertTransactionParams params);
    void updateTransaction(UpdateTransactionParams params);
}
