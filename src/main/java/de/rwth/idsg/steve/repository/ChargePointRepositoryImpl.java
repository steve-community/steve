package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.repository.dto.Heartbeat;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ChargeBoxForm;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.TableLike;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.ConnectorStatus.CONNECTOR_STATUS;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.08.2014
 */
@Slf4j
@Repository
public class ChargePointRepositoryImpl implements ChargePointRepository {

    @Autowired
    @Qualifier("jooqConfig")
    private Configuration config;

    @Override
    public boolean isRegistered(String chargeBoxId) {
        Integer r = DSL.using(config)
                       .selectOne()
                       .from(CHARGE_BOX)
                       .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxId))
                       .fetchOne()
                       .value1();

        return (r != null) && (r == 1);
    }

    @Override
    public List<ChargePointSelect> getChargePointSelect(OcppProtocol protocol) {
        final OcppTransport transport = protocol.getTransport();

        return DSL.using(config)
                  .select(CHARGE_BOX.CHARGE_BOX_ID, CHARGE_BOX.ENDPOINT_ADDRESS)
                  .from(CHARGE_BOX)
                  .where(CHARGE_BOX.OCPP_PROTOCOL.equal(protocol.getCompositeValue()))
                  .and(CHARGE_BOX.ENDPOINT_ADDRESS.isNotNull())
                  .fetch()
                  .map(r -> new ChargePointSelect(transport, r.value1(), r.value2()));
    }

    @Override
    public List<String> getChargeBoxIds() {
        return DSL.using(config)
                  .select(CHARGE_BOX.CHARGE_BOX_ID)
                  .from(CHARGE_BOX)
                  .fetch(CHARGE_BOX.CHARGE_BOX_ID);
    }

    @Override
    public ChargePoint getDetails(String chargeBoxId) {
        ChargeBoxRecord r = DSL.using(config)
                               .selectFrom(CHARGE_BOX)
                               .where(CHARGE_BOX.CHARGE_BOX_ID.equal(chargeBoxId))
                               .fetchOne();

        // TODO: Sweet baby jesus. Is there a better way?
        return ChargePoint.builder()
                          .chargeBoxId(r.getChargeBoxId())
                          .endpointAddress(r.getEndpointAddress())
                          .ocppProtocol(r.getOcppProtocol())
                          .chargePointVendor(r.getChargePointVendor())
                          .chargePointModel(r.getChargePointModel())
                          .chargePointSerialNumber(r.getChargePointSerialNumber())
                          .chargeBoxSerialNumber(r.getChargeBoxSerialNumber())
                          .firewireVersion(r.getFwVersion())
                          .firewireUpdateStatus(r.getFwUpdateStatus())
                          .firewireUpdateTimestamp(DateTimeUtils.humanize(r.getFwUpdateTimestamp()))
                          .iccid(r.getIccid())
                          .imsi(r.getImsi())
                          .meterType(r.getMeterType())
                          .meterSerialNumber(r.getMeterSerialNumber())
                          .diagnosticsStatus(r.getDiagnosticsStatus())
                          .diagnosticsTimestamp(DateTimeUtils.humanize(r.getDiagnosticsTimestamp()))
                          .lastHeartbeatTimestamp(DateTimeUtils.humanize(r.getLastHeartbeatTimestamp()))
                          .note(r.getNote())
                          .build();
    }

    @Override
    public ChargePoint getDetailsForUpdate(String chargeBoxId) {
        String note = DSL.using(config)
                         .select(CHARGE_BOX.NOTE)
                         .from(CHARGE_BOX)
                         .where(CHARGE_BOX.CHARGE_BOX_ID.equal(chargeBoxId))
                         .fetchOne(CHARGE_BOX.NOTE);

        return ChargePoint.builder()
                          .note(note)
                          .build();
    }

    @Override
    public List<Heartbeat> getChargePointHeartbeats() {
        return DSL.using(config)
                  .select(CHARGE_BOX.CHARGE_BOX_ID, CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP)
                  .from(CHARGE_BOX)
                  .orderBy(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP.desc())
                  .fetch()
                  .map(r -> Heartbeat.builder()
                                     .chargeBoxId(r.value1())
                                     .lastTimestamp(DateTimeUtils.humanize(r.value2()))
                                     .build()
                  );
    }

    @Override
    public List<ConnectorStatus> getChargePointConnectorStatus() {
        // Prepare for the inner select of the second join
        Field<Integer> t1Pk = CONNECTOR_STATUS.CONNECTOR_PK.as("t1_pk");
        Field<DateTime> t1Max = DSL.max(CONNECTOR_STATUS.STATUS_TIMESTAMP).as("t1_max");
        TableLike<?> t1 = DSL.select(t1Pk, t1Max)
                             .from(CONNECTOR_STATUS)
                             .groupBy(CONNECTOR_STATUS.CONNECTOR_PK)
                             .asTable("t1");

        return DSL.using(config)
                  .select(CONNECTOR.CHARGE_BOX_ID, CONNECTOR.CONNECTOR_ID,
                          CONNECTOR_STATUS.STATUS_TIMESTAMP, CONNECTOR_STATUS.STATUS, CONNECTOR_STATUS.ERROR_CODE)
                  .from(CONNECTOR_STATUS)
                  .join(CONNECTOR)
                  .onKey()
                  .join(t1)
                  .on(CONNECTOR_STATUS.CONNECTOR_PK.equal(t1.field(t1Pk)))
                  .and(CONNECTOR_STATUS.STATUS_TIMESTAMP.equal(t1.field(t1Max)))
                  .orderBy(CONNECTOR_STATUS.STATUS_TIMESTAMP.desc())
                  .fetch()
                  .map(r -> ConnectorStatus.builder()
                                           .chargeBoxId(r.value1())
                                           .connectorId(r.value2())
                                           .timeStamp(DateTimeUtils.humanize(r.value3()))
                                           .status(r.value4())
                                           .errorCode(r.value5())
                                           .build()
                  );
    }

    @Override
    public List<Integer> getConnectorIds(String chargeBoxId) {
        return DSL.using(config)
                  .select(CONNECTOR.CONNECTOR_ID)
                  .from(CONNECTOR)
                  .where(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxId))
                  .fetch(CONNECTOR.CONNECTOR_ID);
    }

    @Override
    public void addChargePoint(ChargeBoxForm form) {
        try {
            int count = DSL.using(config)
                           .insertInto(CHARGE_BOX,
                                   CHARGE_BOX.CHARGE_BOX_ID, CHARGE_BOX.NOTE)
                           .values(form.getChargeBoxId(), form.getNote())
                           .onDuplicateKeyIgnore() // Important detail
                           .execute();

            if (count == 0) {
                throw new SteveException("A charge point with chargeBoxId '%s' already exists.",
                        form.getChargeBoxId());
            }
        } catch (DataAccessException e) {
            throw new SteveException("The charge point with chargeBoxId '%s' could NOT be added.",
                    form.getChargeBoxId(), e);
        }
    }

    @Override
    public void updateChargePoint(ChargeBoxForm form) {
        try {
            DSL.using(config)
               .update(CHARGE_BOX)
               .set(CHARGE_BOX.NOTE, form.getNote())
               .where(CHARGE_BOX.CHARGE_BOX_ID.equal(form.getChargeBoxId()))
               .execute();
        } catch (DataAccessException e) {
            throw new SteveException("The charge point with chargeBoxId '%s' could NOT be updated.",
                    form.getChargeBoxId(), e);
        }
    }

    @Override
    public void deleteChargePoint(String chargeBoxId) {
        try {
            DSL.using(config)
               .delete(CHARGE_BOX)
               .where(CHARGE_BOX.CHARGE_BOX_ID.equal(chargeBoxId))
               .execute();
        } catch (DataAccessException e) {
            throw new SteveException("The charge point with chargeBoxId '%s' could NOT be deleted.", chargeBoxId, e);
        }
    }
}
