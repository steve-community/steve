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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import jooq.steve.db.enums.TransactionStopEventActor;
import jooq.steve.db.tables.records.TransactionRecord;
import ocpp.cs._2015._10.MeterValue;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.TransactionStart.TRANSACTION_START;

public class OcppServerRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private OcppServerRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void updateChargebox() {
        assertNoDatabaseException(() -> repository.updateChargebox(updateChargeboxParams()));
    }

    @Test
    public void updateOcppProtocol() {
        assertNoDatabaseException(() -> repository.updateOcppProtocol(KNOWN_CHARGE_BOX_ID, OcppProtocol.V_16_JSON));
    }

    @Test
    public void updateEndpointAddress() {
        assertNoDatabaseException(() -> repository.updateEndpointAddress(KNOWN_CHARGE_BOX_ID, "ws://example"));
    }

    @Test
    public void updateChargeboxFirmwareStatus() {
        assertNoDatabaseException(() -> repository.updateChargeboxFirmwareStatus(KNOWN_CHARGE_BOX_ID, "Downloading"));
    }

    @Test
    public void updateChargeboxDiagnosticsStatus() {
        assertNoDatabaseException(() -> repository.updateChargeboxDiagnosticsStatus(KNOWN_CHARGE_BOX_ID, "Idle"));
    }

    @Test
    public void updateChargeboxHeartbeat() {
        assertNoDatabaseException(() -> repository.updateChargeboxHeartbeat(KNOWN_CHARGE_BOX_ID, DateTime.now()));
    }

    @Test
    public void insertConnectorStatus() {
        assertNoDatabaseException(() -> repository.insertConnectorStatus(connectorStatusParams()));
    }

    @Test
    public void insertMeterValuesByConnectorAndTransactionId() {
        assertNoDatabaseException(() -> repository.insertMeterValues(KNOWN_CHARGE_BOX_ID, List.of(new MeterValue()), 1, 1));
    }

    @Test
    public void insertMeterValuesByTransactionRecord() {
        TransactionRecord transaction = new TransactionRecord();
        transaction.setConnectorPk(1);
        transaction.setTransactionPk(1);
        assertNoDatabaseException(() -> repository.insertMeterValues(KNOWN_CHARGE_BOX_ID, List.of(new MeterValue()), transaction));
    }

    @Test
    public void getTransaction() {
        assertNoDatabaseException(() -> repository.getTransaction(KNOWN_CHARGE_BOX_ID, 1));
    }

    @Test
    public void insertTransaction() {
        assertNoDatabaseException(() -> repository.insertTransaction(insertTransactionParams()));
    }

    @Test
    public void updateTransaction() {
        seedTransactionStart(1);
        assertNoDatabaseException(() -> repository.updateTransaction(updateTransactionParams(1)));
    }

    @Test
    public void updateTransactionAsFailed() {
        seedTransactionStart(1);
        assertNoDatabaseException(() -> repository.updateTransactionAsFailed(updateTransactionParams(1), new RuntimeException("it")));
    }

    private void seedTransactionStart(int transactionId) {
        Integer connectorPk = dslContext.select(CONNECTOR.CONNECTOR_PK)
            .from(CONNECTOR)
            .where(CONNECTOR.CHARGE_BOX_ID.eq(KNOWN_CHARGE_BOX_ID))
            .and(CONNECTOR.CONNECTOR_ID.eq(1))
            .fetchOne(CONNECTOR.CONNECTOR_PK);

        dslContext.insertInto(TRANSACTION_START)
            .set(TRANSACTION_START.TRANSACTION_PK, transactionId)
            .set(TRANSACTION_START.EVENT_TIMESTAMP, DateTime.now())
            .set(TRANSACTION_START.CONNECTOR_PK, connectorPk)
            .set(TRANSACTION_START.ID_TAG, KNOWN_OCPP_TAG)
            .set(TRANSACTION_START.START_TIMESTAMP, DateTime.now())
            .set(TRANSACTION_START.START_VALUE, "1000")
            .execute();
    }

    private static UpdateChargeboxParams updateChargeboxParams() {
        return UpdateChargeboxParams.builder()
            .chargeBoxId(KNOWN_CHARGE_BOX_ID)
            .ocppProtocol(OcppProtocol.V_16_JSON)
            .heartbeatTimestamp(DateTime.now())
            .vendor("vendor")
            .model("model")
            .build();
    }

    private static InsertConnectorStatusParams connectorStatusParams() {
        return InsertConnectorStatusParams.builder()
            .chargeBoxId(KNOWN_CHARGE_BOX_ID)
            .connectorId(1)
            .timestamp(DateTime.now())
            .status("Available")
            .errorCode("NoError")
            .build();
    }

    private static InsertTransactionParams insertTransactionParams() {
        return InsertTransactionParams.builder()
            .chargeBoxId(KNOWN_CHARGE_BOX_ID)
            .connectorId(1)
            .idTag(KNOWN_OCPP_TAG)
            .startTimestamp(DateTime.now())
            .startMeterValue("1000")
            .eventTimestamp(DateTime.now())
            .build();
    }

    private static UpdateTransactionParams updateTransactionParams(int transactionId) {
        return UpdateTransactionParams.builder()
            .chargeBoxId(KNOWN_CHARGE_BOX_ID)
            .transactionId(transactionId)
            .stopTimestamp(DateTime.now())
            .stopMeterValue("1100")
            .stopReason("Local")
            .eventActor(TransactionStopEventActor.station)
            .eventTimestamp(DateTime.now())
            .build();
    }
}
