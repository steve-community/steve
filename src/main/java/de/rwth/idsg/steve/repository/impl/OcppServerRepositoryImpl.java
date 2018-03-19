package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.TransactionStatusUpdate;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import de.rwth.idsg.steve.utils.CustomDSL;
import jooq.steve.db.tables.records.ConnectorMeterValueRecord;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.MeterValue;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.ConnectorMeterValue.CONNECTOR_METER_VALUE;
import static jooq.steve.db.tables.ConnectorStatus.CONNECTOR_STATUS;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.Transaction.TRANSACTION;

/**
 * This class has methods for database access that are used by the OCPP service.
 *
 * http://www.jooq.org/doc/3.4/manual/sql-execution/transaction-management/
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Slf4j
@Repository
public class OcppServerRepositoryImpl implements OcppServerRepository {

    @Autowired private DSLContext ctx;
    @Autowired private ReservationRepository reservationRepository;

    @Override
    public boolean updateChargebox(UpdateChargeboxParams p) {
        int count = ctx.update(CHARGE_BOX)
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

        boolean isRegistered = false;

        if (count == 1) {
            log.info("The chargebox '{}' is registered and its boot acknowledged.", p.getChargeBoxId());
            isRegistered = true;
        } else {
            log.error("The chargebox '{}' is NOT registered and its boot NOT acknowledged.", p.getChargeBoxId());
        }
        return isRegistered;
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
           .set(CHARGE_BOX.FW_UPDATE_TIMESTAMP, CustomDSL.utcTimestamp())
           .where(CHARGE_BOX.CHARGE_BOX_ID.equal(chargeBoxIdentity))
           .execute();
    }

    @Override
    public void updateChargeboxDiagnosticsStatus(String chargeBoxIdentity, String status) {
        ctx.update(CHARGE_BOX)
           .set(CHARGE_BOX.DIAGNOSTICS_STATUS, status)
           .set(CHARGE_BOX.DIAGNOSTICS_TIMESTAMP, CustomDSL.utcTimestamp())
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
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);

            insertIgnoreConnector(ctx, chargeBoxIdentity, connectorId);
            int connectorPk = getConnectorPkFromConnector(ctx, chargeBoxIdentity, connectorId);
            batchInsertMeterValues(ctx, list, connectorPk, transactionId);
        });
    }

    @Override
    public void insertMeterValues(String chargeBoxIdentity, List<MeterValue> list, int transactionId) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);

            // First, get connector primary key from transaction table
            int connectorPk = ctx.select(TRANSACTION.CONNECTOR_PK)
                                 .from(TRANSACTION)
                                 .where(TRANSACTION.TRANSACTION_PK.equal(transactionId))
                                 .fetchOne()
                                 .value1();

            batchInsertMeterValues(ctx, list, connectorPk, transactionId);
        });
    }

    @Override
    public Integer insertTransaction(InsertTransactionParams p) {
        return ctx.transactionResult(configuration -> {
            DSLContext ctx = DSL.using(configuration);

            insertIgnoreConnector(ctx, p.getChargeBoxId(), p.getConnectorId());

            // it is important to insert idTag before transaction, since the transaction table references it
            boolean unknownTagInserted = insertIgnoreIdTag(ctx, p);

            SelectConditionStep<Record1<Integer>> connectorPkQuery =
                    DSL.select(CONNECTOR.CONNECTOR_PK)
                       .from(CONNECTOR)
                       .where(CONNECTOR.CHARGE_BOX_ID.equal(p.getChargeBoxId()))
                       .and(CONNECTOR.CONNECTOR_ID.equal(p.getConnectorId()));

            // -------------------------------------------------------------------------
            // Step 1: Insert transaction
            // -------------------------------------------------------------------------

            int transactionId = ctx.insertInto(TRANSACTION)
                                   .set(CONNECTOR_STATUS.CONNECTOR_PK, connectorPkQuery)
                                   .set(TRANSACTION.ID_TAG, p.getIdTag())
                                   .set(TRANSACTION.START_TIMESTAMP, p.getStartTimestamp())
                                   .set(TRANSACTION.START_VALUE, p.getStartMeterValue())
                                   .returning(TRANSACTION.TRANSACTION_PK)
                                   .fetchOne()
                                   .getTransactionPk();

            if (unknownTagInserted) {
                log.warn("The transaction '{}' contains an unknown idTag '{}' which was inserted into DB as to prevent information loss and has been blocked", transactionId, p.getIdTag());
            }

            // -------------------------------------------------------------------------
            // Step 2 for OCPP 1.5: A startTransaction may be related to a reservation
            // -------------------------------------------------------------------------

            if (p.isSetReservationId()) {
                reservationRepository.used(p.getReservationId(), transactionId);
            }

            // -------------------------------------------------------------------------
            // Step 3: Set connector status to "Occupied"
            // -------------------------------------------------------------------------

            insertConnectorStatus(ctx, connectorPkQuery, p.getStartTimestamp(), p.getStatusUpdate());

            return transactionId;
        });
    }

    @Override
    public void updateTransaction(UpdateTransactionParams p) {

        // -------------------------------------------------------------------------
        // Step 1: Update transaction table
        //
        // After update, a DB trigger sets the user.inTransaction field to 0
        // -------------------------------------------------------------------------

        ctx.update(TRANSACTION)
           .set(TRANSACTION.STOP_TIMESTAMP, p.getStopTimestamp())
           .set(TRANSACTION.STOP_VALUE, p.getStopMeterValue())
           .set(TRANSACTION.STOP_REASON, p.getStopReason())
           .where(TRANSACTION.TRANSACTION_PK.equal(p.getTransactionId()))
           .and(TRANSACTION.STOP_TIMESTAMP.isNull())
           .and(TRANSACTION.STOP_VALUE.isNull())
           .execute();

        // -------------------------------------------------------------------------
        // Step 2: Set connector status back to "Available" again
        // -------------------------------------------------------------------------

        SelectConditionStep<Record1<Integer>> connectorPkQuery =
                DSL.select(TRANSACTION.CONNECTOR_PK)
                   .from(TRANSACTION)
                   .where(TRANSACTION.TRANSACTION_PK.equal(p.getTransactionId()));

        insertConnectorStatus(ctx, connectorPkQuery, p.getStopTimestamp(), p.getStatusUpdate());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

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
        ctx.insertInto(CONNECTOR_STATUS)
           .set(CONNECTOR_STATUS.CONNECTOR_PK, connectorPkQuery)
           .set(CONNECTOR_STATUS.STATUS_TIMESTAMP, timestamp)
           .set(CONNECTOR_STATUS.STATUS, statusUpdate.getStatus())
           .set(CONNECTOR_STATUS.ERROR_CODE, statusUpdate.getErrorCode())
           .execute();
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
                       .set(OCPP_TAG.BLOCKED, true)
                       .onDuplicateKeyIgnore() // Important detail
                       .execute();

        return count == 1;
    }

    private int getConnectorPkFromConnector(DSLContext ctx, String chargeBoxIdentity, int connectorId) {
        return ctx.select(CONNECTOR.CONNECTOR_PK)
                  .from(CONNECTOR)
                  .where(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxIdentity)
                    .and(CONNECTOR.CONNECTOR_ID.equal(connectorId)))
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
}
