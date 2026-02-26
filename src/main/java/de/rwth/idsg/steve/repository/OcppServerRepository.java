/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
import jooq.steve.db.tables.records.TransactionRecord;
import ocpp.cs._2015._10.MeterValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.08.2014
 */
public interface OcppServerRepository {

    void updateChargebox(UpdateChargeboxParams params);
    void updateOcppProtocol(@NotNull String chargeBoxId, @NotNull OcppProtocol protocol);
    void updateEndpointAddress(@NotNull String chargeBoxId, @NotNull String endpointAddress);
    void updateChargeboxFirmwareStatus(@NotNull String chargeBoxId, @NotNull String firmwareStatus);
    void updateChargeboxDiagnosticsStatus(@NotNull String chargeBoxId, @NotNull String status);
    void updateChargeboxHeartbeat(@NotNull String chargeBoxId, @NotNull DateTime ts);

    void insertConnectorStatus(InsertConnectorStatusParams params);

    void insertMeterValues(@NotNull String chargeBoxId, List<MeterValue> list, int connectorId, Integer transactionId);
    void insertMeterValues(@NotNull String chargeBoxId, List<MeterValue> list, @Nullable TransactionRecord transaction);

    @Nullable TransactionRecord getTransaction(@NotNull String chargeBoxId, int transactionId);
    int insertTransaction(InsertTransactionParams params);
    void updateTransaction(UpdateTransactionParams params);
    void updateTransactionAsFailed(UpdateTransactionParams params, Exception exception);
}
