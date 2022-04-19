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
package de.rwth.idsg.steve.repository.impl;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Striped;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.TransactionStatusUpdate;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import jooq.steve.db.enums.TransactionStopEventActor;
import jooq.steve.db.enums.TransactionStopFailedEventActor;
import jooq.steve.db.tables.records.ConnectorMeterValueRecord;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.MeterValue;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.ConnectorMeterValue.CONNECTOR_METER_VALUE;
import static jooq.steve.db.tables.ConnectorStatus.CONNECTOR_STATUS;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.TransactionStart.TRANSACTION_START;
import static jooq.steve.db.tables.TransactionStop.TRANSACTION_STOP;
import static jooq.steve.db.tables.TransactionStopFailed.TRANSACTION_STOP_FAILED;

/**
 * This class has methods for database access that are used by the OCPP service.
 *
 * http://www.jooq.org/doc/3.4/manual/sql-execution/transaction-management/
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 *
 */
@Slf4j
@Repository
public class OcppServerRepositoryImpl implements OcppServerRepository {

    @Autowired private DSLContext ctx;
    @Autowired private ReservationRepository reservationRepository;

    private final Striped<Lock> transactionTableLocks = Striped.lock(16);

    @Override
    public void updateChargebox(UpdateChargeboxParams p) {
        ctx.update(CHARGE_BOX)
           .set(CHARGE_BOX.OCPP_PROTOCOL, p.getOcppProtocol().getCompositeValue())
           .set(CHARGE_BOX.CHARGE_POINT_VENDOR, p.getVendor())
           .set(CHARGE_BOX.CHARGE_POINT_MODEL, p.getModel())
           .set(CHARGE_BOX.CHARGE_POINT_SERIAL_NUMBER, p.getPointSerial())
           .set(CHARGE_BOX.CHARGE_BOX_SERIAL_NUMBER, p.getBoxSerial())
           .set(CHARGE_BOX.FW_VERSION, p.getFwVersion())
           .set(CHARGE_BOX.ICCID, p.getIccid())
           .set(CHARGE_BOX.IMSI, p.getImsi())
           .set(CHARGE_BOX.METER_TYPE, p.getMeterType())
           .set(CHARGE_BOX.METER_SERIAL_NUMBER, p.getMeterSerial())
           .set(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP, p.getHeartbeatTimestamp())
           .where(CHARGE_BOX.CHARGE_BOX_ID.equal(p.getChargeBoxId()))
           .execute();
    }

    @Override
    public void updateOcppProtocol(String chargeBoxIdentity, OcppProtocol protocol) {
        ctx.update(CHARGE_BOX)
            .set(CHARGE_BOX.OCPP_PROTOCOL, protocol.getCompositeValue())
            .where(CHARGE_BOX.CHARGE_BOX_ID.equal(chargeBoxIdentity))
            .execute();
    }

    @Override
    public void updateEndpointAddress(String chargeBoxIdentity, String endpointAddress) {
        ctx.update(CHARGE_BOX)
           .set(CHARGE_BOX.ENDPOINT_ADDRESS, endpointAddress)
           .where(CHARGE_BOX.CHARGE_BOX_ID.equal(chargeBoxIdentity))
           .execute();
    }

    @Override
    public void updateChargeboxFirmwareStatus(String chargeBoxIdentity, String firmwareStatus) {
        ctx.update(CHARGE_BOX)
           .set(CHARGE_BOX.FW_UPDATE_STATUS, firmwareStatus)
           .set(CHARGE_BOX.FW_UPDATE_TIMESTAMP, DateTime.now())
           .where(CHARGE_BOX.CHARGE_BOX_ID.equal(chargeBoxIdentity))
           .execute();
    }

    @Override
    public void updateChargeboxDiagnosticsStatus(String chargeBoxIdentity, String status) {
        ctx.update(CHARGE_BOX)
           .set(CHARGE_BOX.DIAGNOSTICS_STATUS, status)
           .set(CHARGE_BOX.DIAGNOSTICS_TIMESTAMP, DateTime.now())
           .where(CHARGE_BOX.CHARGE_BOX_ID.equal(chargeBoxIdentity))
           .execute();
    }

    @Override
    public void updateChargeboxHeartbeat(String chargeBoxIdentity, DateTime ts) {
        ctx.update(CHARGE_BOX)
           .set(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP, ts)
           .where(CHARGE_BOX.CHARGE_BOX_ID.equal(chargeBoxIdentity))
           .execute();
    }

    @Override
    public void insertConnectorStatus(InsertConnectorStatusParams p) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);

            // Step 1
            insertIgnoreConnector(ctx, p.getChargeBoxId(), p.getConnectorId());

            // -------------------------------------------------------------------------
            // Step 2: We store a log of connector statuses
            // -------------------------------------------------------------------------

            ctx.insertInto(CONNECTOR_STATUS)
               .set(CONNECTOR_STATUS.CONNECTOR_PK, DSL.select(CONNECTOR.CONNECTOR_PK)
                                                      .from(CONNECTOR)
                                                      .where(CONNECTOR.CHARGE_BOX_ID.equal(p.getChargeBoxId()))
                                                      .and(CONNECTOR.CONNECTOR_ID.equal(p.getConnectorId()))
               )
               .set(CONNECTOR_STATUS.STATUS_TIMESTAMP, p.getTimestamp())
               .set(CONNECTOR_STATUS.STATUS, p.getStatus())
               .set(CONNECTOR_STATUS.ERROR_CODE, p.getErrorCode())
               .set(CONNECTOR_STATUS.ERROR_INFO, p.getErrorInfo())
               .set(CONNECTOR_STATUS.VENDOR_ID, p.getVendorId())
               .set(CONNECTOR_STATUS.VENDOR_ERROR_CODE, p.getVendorErrorCode())
               .execute();

            log.debug("Stored a new connector status for {}/{}.", p.getChargeBoxId(), p.getConnectorId());
        });
    }

    @Override
    public void insertMeterValues(String chargeBoxIdentity, List<MeterValue> list, int connectorId, Integer transactionId) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        ctx.transaction(configuration -> {
            try {
                DSLContext ctx = DSL.using(configuration);

                insertIgnoreConnector(ctx, chargeBoxIdentity, connectorId);
                int connectorPk = getConnectorPkFromConnector(ctx, chargeBoxIdentity, connectorId);
                batchInsertMeterValues(ctx, list, connectorPk, transactionId);
            } catch (Exception e) {
                log.error("Exception occurred", e);
            }
        });
    }

    @Override
    public void insertMeterValues(String chargeBoxIdentity, List<MeterValue> list, int transactionId) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        ctx.transaction(configuration -> {
            try {
                DSLContext ctx = DSL.using(configuration);

                // First, get connector primary key from transaction table
                int connectorPk = ctx.select(TRANSACTION_START.CONNECTOR_PK)
                                     .from(TRANSACTION_START)
                                     .where(TRANSACTION_START.TRANSACTION_PK.equal(transactionId))
                                     .fetchOne()
                                     .value1();

                batchInsertMeterValues(ctx, list, connectorPk, transactionId);
            } catch (Exception e) {
                log.error("Exception occurred", e);
            }
        });
    }

    @Override
    public int insertTransaction(InsertTransactionParams p) {

        SelectConditionStep<Record1<Integer>> connectorPkQuery =
                DSL.select(CONNECTOR.CONNECTOR_PK)
                   .from(CONNECTOR)
                   .where(CONNECTOR.CHARGE_BOX_ID.equal(p.getChargeBoxId()))
                   .and(CONNECTOR.CONNECTOR_ID.equal(p.getConnectorId()));

        // -------------------------------------------------------------------------
        // Step 1: Insert connector and idTag, if they are new to us
        // -------------------------------------------------------------------------

        insertIgnoreConnector(ctx, p.getChargeBoxId(), p.getConnectorId());

        // it is important to insert idTag before transaction, since the transaction table references it
        boolean unknownTagInserted = insertIgnoreIdTag(ctx, p);

        // -------------------------------------------------------------------------
        // Step 2: Insert transaction if it does not exist already
        // -------------------------------------------------------------------------

        TransactionDataHolder data = insertIgnoreTransaction(p, connectorPkQuery);
        int transactionId = data.transactionId;

        if (data.existsAlready) {
            return transactionId;
        }

        if (unknownTagInserted) {
            log.warn("The transaction '{}' contains an unknown idTag '{}' which was inserted into DB "
                    + "to prevent information loss and has been blocked", transactionId, p.getIdTag());
        }

        // -------------------------------------------------------------------------
        // Step 3 for OCPP >= 1.5: A startTransaction may be related to a reservation
        // -------------------------------------------------------------------------

        if (p.isSetReservationId()) {
            reservationRepository.used(connectorPkQuery, p.getIdTag(), p.getReservationId(), transactionId);
        }

        // -------------------------------------------------------------------------
        // Step 4: Set connector status
        // -------------------------------------------------------------------------

        if (shouldInsertConnectorStatusAfterTransactionMsg(p.getChargeBoxId())) {
            insertConnectorStatus(ctx, connectorPkQuery, p.getStartTimestamp(), p.getStatusUpdate());
        }

        return transactionId;
    }

    @Override
    public void updateTransaction(UpdateTransactionParams p) {

        // -------------------------------------------------------------------------
        // Step 1: insert transaction stop data
        // -------------------------------------------------------------------------

        // JOOQ will throw an exception, if something goes wrong
        try {
            ctx.insertInto(TRANSACTION_STOP)
               .set(TRANSACTION_STOP.TRANSACTION_PK, p.getTransactionId())
               .set(TRANSACTION_STOP.EVENT_TIMESTAMP, p.getEventTimestamp())
               .set(TRANSACTION_STOP.EVENT_ACTOR, p.getEventActor())
               .set(TRANSACTION_STOP.STOP_TIMESTAMP, p.getStopTimestamp())
               .set(TRANSACTION_STOP.STOP_VALUE, p.getStopMeterValue())
               .set(TRANSACTION_STOP.STOP_REASON, p.getStopReason())
               .execute();
        } catch (Exception e) {
            log.error("Exception occurred", e);
            tryInsertingFailed(p, e);
        }

        // -------------------------------------------------------------------------
        // Step 2: Set connector status back. We do this even in cases where step 1
        // fails. It probably and hopefully makes sense.
        // -------------------------------------------------------------------------

        if (shouldInsertConnectorStatusAfterTransactionMsg(p.getChargeBoxId())) {
            SelectConditionStep<Record1<Integer>> connectorPkQuery =
                    DSL.select(TRANSACTION_START.CONNECTOR_PK)
                       .from(TRANSACTION_START)
                       .where(TRANSACTION_START.TRANSACTION_PK.equal(p.getTransactionId()));

            insertConnectorStatus(ctx, connectorPkQuery, p.getStopTimestamp(), p.getStatusUpdate());
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class TransactionDataHolder {
        final boolean existsAlready;
        final int transactionId;
    }

    /**
     * Use case: If the station sends identical StartTransaction messages multiple times (e.g. due to connection
     * problems the response of StartTransaction could not be delivered and station tries again later), we do not want
     * to insert this into database multiple times.
     */
    private TransactionDataHolder insertIgnoreTransaction(InsertTransactionParams p,
                                                          SelectConditionStep<Record1<Integer>> connectorPkQuery) {
        Lock l = transactionTableLocks.get(p.getChargeBoxId());
        l.lock();
        try {
            Record1<Integer> r = ctx.select(TRANSACTION_START.TRANSACTION_PK)
                                    .from(TRANSACTION_START)
                                    .where(TRANSACTION_START.CONNECTOR_PK.eq(connectorPkQuery))
                                    .and(TRANSACTION_START.ID_TAG.eq(p.getIdTag()))
                                    .and(TRANSACTION_START.START_TIMESTAMP.eq(p.getStartTimestamp()))
                                    .and(TRANSACTION_START.START_VALUE.eq(p.getStartMeterValue()))
                                    .fetchOne();

            if (r != null) {
                return new TransactionDataHolder(true, r.value1());
            }

            Integer transactionId = ctx.insertInto(TRANSACTION_START)
                                       .set(TRANSACTION_START.EVENT_TIMESTAMP, p.getEventTimestamp())
                                       .set(TRANSACTION_START.CONNECTOR_PK, connectorPkQuery)
                                       .set(TRANSACTION_START.ID_TAG, p.getIdTag())
                                       .set(TRANSACTION_START.START_TIMESTAMP, p.getStartTimestamp())
                                       .set(TRANSACTION_START.START_VALUE, p.getStartMeterValue())
                                       .returning(TRANSACTION_START.TRANSACTION_PK)
                                       .fetchOne()
                                       .getTransactionPk();

            // Actually unnecessary, because JOOQ will throw an exception, if something goes wrong
            if (transactionId == null) {
                throw new SteveException("Failed to INSERT transaction into database");
            }

            return new TransactionDataHolder(false, transactionId);
        } finally {
            l.unlock();
        }
    }

    /**
     * After a transaction start/stop event, a charging station _might_ send a connector status notification, but it is
     * not required. With this, we make sure that the status is updated accordingly. Since we use the timestamp of the
     * transaction data, we do not necessarily insert a "most recent" status.
     *
     * If the station sends a notification, we will have a more recent timestamp, and therefore the status of the
     * notification will be used as current. Or, if this transaction data was sent to us for a failed push from the past
     * and we have a "more recent" status, it will still be the current status.
     */
    private void insertConnectorStatus(DSLContext ctx,
                                       SelectConditionStep<Record1<Integer>> connectorPkQuery,
                                       DateTime timestamp,
                                       TransactionStatusUpdate statusUpdate) {
        try {
            ctx.insertInto(CONNECTOR_STATUS)
               .set(CONNECTOR_STATUS.CONNECTOR_PK, connectorPkQuery)
               .set(CONNECTOR_STATUS.STATUS_TIMESTAMP, timestamp)
               .set(CONNECTOR_STATUS.STATUS, statusUpdate.getStatus())
               .set(CONNECTOR_STATUS.ERROR_CODE, statusUpdate.getErrorCode())
               .execute();
        } catch (Exception e) {
            log.error("Exception occurred", e);
        }
    }

    /**
     * If the connector information was not received before, insert it. Otherwise, ignore.
     */
    private void insertIgnoreConnector(DSLContext ctx, String chargeBoxIdentity, int connectorId) {
        int count = ctx.insertInto(CONNECTOR,
                            CONNECTOR.CHARGE_BOX_ID, CONNECTOR.CONNECTOR_ID)
                       .values(chargeBoxIdentity, connectorId)
                       .onDuplicateKeyIgnore() // Important detail
                       .execute();

        if (count == 1) {
            log.info("The connector {}/{} is NEW, and inserted into DB.", chargeBoxIdentity, connectorId);
        }
    }

    /**
     * Use case: An offline charging station decides to allow an unknown idTag to start a transaction. Later, when it
     * is online, it sends a StartTransactionRequest with this idTag. If we do not insert this idTag, the transaction
     * details will not be inserted into DB and we will lose valuable information.
     */
    private boolean insertIgnoreIdTag(DSLContext ctx, InsertTransactionParams p) {
        String note = "This unknown idTag was used in a transaction that started @ " + p.getStartTimestamp()
                + ". It was reported @ " + DateTime.now() + ".";

        int count = ctx.insertInto(OCPP_TAG)
                       .set(OCPP_TAG.ID_TAG, p.getIdTag())
                       .set(OCPP_TAG.NOTE, note)
                       .set(OCPP_TAG.MAX_ACTIVE_TRANSACTION_COUNT, 0)
                       .onDuplicateKeyIgnore() // Important detail
                       .execute();

        return count == 1;
    }

    private boolean shouldInsertConnectorStatusAfterTransactionMsg(String chargeBoxId) {
        Record1<Integer> r = ctx.selectOne()
                                .from(CHARGE_BOX)
                                .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxId))
                                .and(CHARGE_BOX.INSERT_CONNECTOR_STATUS_AFTER_TRANSACTION_MSG.isTrue())
                                .fetchOne();

        return (r != null) && (r.value1() == 1);
    }

    private int getConnectorPkFromConnector(DSLContext ctx, String chargeBoxIdentity, int connectorId) {
        return ctx.select(CONNECTOR.CONNECTOR_PK)
                  .from(CONNECTOR)
                  .where(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxIdentity))
                  .and(CONNECTOR.CONNECTOR_ID.equal(connectorId))
                  .fetchOne()
                  .value1();
    }

    private void batchInsertMeterValues(DSLContext ctx, List<MeterValue> list, int connectorPk, Integer transactionId) {
        List<ConnectorMeterValueRecord> batch =
                list.stream()
                    .flatMap(t -> t.getSampledValue()
                                   .stream()
                                   .map(k -> ctx.newRecord(CONNECTOR_METER_VALUE)
                                                .setConnectorPk(connectorPk)
                                                .setTransactionPk(transactionId)
                                                .setValueTimestamp(t.getTimestamp())
                                                .setValue(k.getValue())
                                                // The following are optional fields!
                                                .setReadingContext(k.isSetContext() ? k.getContext().value() : null)
                                                .setFormat(k.isSetFormat() ? k.getFormat().value() : null)
                                                .setMeasurand(k.isSetMeasurand() ? k.getMeasurand().value() : null)
                                                .setLocation(k.isSetLocation() ? k.getLocation().value() : null)
                                                .setUnit(k.isSetUnit() ? k.getUnit().value() : null)
                                                .setPhase(k.isSetPhase() ? k.getPhase().value() : null)))
                    .collect(Collectors.toList());

        ctx.batchInsert(batch).execute();
    }

    private void tryInsertingFailed(UpdateTransactionParams p, Exception e) {
        try {
            ctx.insertInto(TRANSACTION_STOP_FAILED)
               .set(TRANSACTION_STOP_FAILED.TRANSACTION_PK, p.getTransactionId())
               .set(TRANSACTION_STOP_FAILED.EVENT_TIMESTAMP, p.getEventTimestamp())
               .set(TRANSACTION_STOP_FAILED.EVENT_ACTOR, mapActor(p.getEventActor()))
               .set(TRANSACTION_STOP_FAILED.STOP_TIMESTAMP, p.getStopTimestamp())
               .set(TRANSACTION_STOP_FAILED.STOP_VALUE, p.getStopMeterValue())
               .set(TRANSACTION_STOP_FAILED.STOP_REASON, p.getStopReason())
               .set(TRANSACTION_STOP_FAILED.FAIL_REASON, Throwables.getStackTraceAsString(e))
               .execute();
        } catch (Exception ex) {
            // This is where we give up and just log
            log.error("Exception occurred", e);
        }
    }

    private static TransactionStopFailedEventActor mapActor(TransactionStopEventActor a) {
        for (TransactionStopFailedEventActor b : TransactionStopFailedEventActor.values()) {
            if (b.getLiteral().equalsIgnoreCase(a.getLiteral())) {
                return b;
            }
        }
        // if unknown, do not throw exceptions. just insert manual.
        return TransactionStopFailedEventActor.manual;
    }
}
