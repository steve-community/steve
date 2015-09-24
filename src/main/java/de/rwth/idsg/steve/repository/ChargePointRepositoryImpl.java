package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.repository.dto.Heartbeat;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import jooq.steve.db.tables.records.ChargeboxRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.RecordMapper;
import org.jooq.TableLike;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static jooq.steve.db.tables.Chargebox.CHARGEBOX;
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
                       .from(CHARGEBOX)
                       .where(CHARGEBOX.CHARGEBOXID.eq(chargeBoxId))
                       .fetchOne()
                       .value1();

        return (r != null) && (r == 1);
    }

    @Override
    public List<ChargePointSelect> getChargePointSelect(OcppProtocol protocol) {
        return DSL.using(config)
                  .select(CHARGEBOX.CHARGEBOXID, CHARGEBOX.ENDPOINT_ADDRESS)
                  .from(CHARGEBOX)
                  .where(CHARGEBOX.OCPPPROTOCOL.equal(protocol.getCompositeValue()))
                  .and(CHARGEBOX.ENDPOINT_ADDRESS.isNotNull())
                  .fetch()
                  .map(new ChargePointSelectMapper(protocol.getTransport()));
    }

    @Override
    public List<String> getChargeBoxIds() {
        return DSL.using(config)
                  .select(CHARGEBOX.CHARGEBOXID)
                  .from(CHARGEBOX)
                  .fetch(CHARGEBOX.CHARGEBOXID);
    }

    @Override
    public ChargePoint getChargePointDetails(String chargeBoxId) {
        ChargeboxRecord r = DSL.using(config)
                               .selectFrom(CHARGEBOX)
                               .where(CHARGEBOX.CHARGEBOXID.equal(chargeBoxId))
                               .fetchOne();

        // TODO: Sweet baby jesus. Is there a better way?
        return ChargePoint.builder()
                          .chargeBoxId(r.getChargeboxid())
                          .endpointAddress(r.getEndpointAddress())
                          .ocppProtocol(r.getOcppprotocol())
                          .chargePointVendor(r.getChargepointvendor())
                          .chargePointModel(r.getChargepointmodel())
                          .chargePointSerialNumber(r.getChargepointserialnumber())
                          .chargeBoxSerialNumber(r.getChargeboxserialnumber())
                          .firewireVersion(r.getFwversion())
                          .firewireUpdateStatus(r.getFwupdatestatus())
                          .firewireUpdateTimestamp(DateTimeUtils.humanize(r.getFwupdatetimestamp()))
                          .iccid(r.getIccid())
                          .imsi(r.getImsi())
                          .meterType(r.getMetertype())
                          .meterSerialNumber(r.getMeterserialnumber())
                          .diagnosticsStatus(r.getDiagnosticsstatus())
                          .diagnosticsTimestamp(DateTimeUtils.humanize(r.getDiagnosticstimestamp()))
                          .lastHeartbeatTimestamp(DateTimeUtils.humanize(r.getLastheartbeattimestamp()))
                          .build();
    }

    @Override
    public List<Heartbeat> getChargePointHeartbeats() {
        return DSL.using(config)
                  .select(CHARGEBOX.CHARGEBOXID, CHARGEBOX.LASTHEARTBEATTIMESTAMP)
                  .from(CHARGEBOX)
                  .orderBy(CHARGEBOX.LASTHEARTBEATTIMESTAMP.desc())
                  .fetch()
                  .map(new HeartbeatMapper());
    }

    @Override
    public List<ConnectorStatus> getChargePointConnectorStatus() {
        // Prepare for the inner select of the second join
        Field<Integer> t1_pk = CONNECTOR_STATUS.CONNECTOR_PK.as("t1_pk");
        Field<Timestamp> t1_max = DSL.max(CONNECTOR_STATUS.STATUSTIMESTAMP).as("t1_max");
        TableLike<?> t1 = DSL.select(t1_pk, t1_max)
                             .from(CONNECTOR_STATUS)
                             .groupBy(CONNECTOR_STATUS.CONNECTOR_PK)
                             .asTable("t1");

        return DSL.using(config)
                  .select(CONNECTOR.CHARGEBOXID, CONNECTOR.CONNECTORID,
                          CONNECTOR_STATUS.STATUSTIMESTAMP, CONNECTOR_STATUS.STATUS, CONNECTOR_STATUS.ERRORCODE)
                  .from(CONNECTOR_STATUS)
                  .join(CONNECTOR)
                    .onKey()
                  .join(t1)
                    .on(CONNECTOR_STATUS.CONNECTOR_PK.equal(t1.field(t1_pk)))
                    .and(CONNECTOR_STATUS.STATUSTIMESTAMP.equal(t1.field(t1_max)))
                  .orderBy(CONNECTOR_STATUS.STATUSTIMESTAMP.desc())
                  .fetch()
                  .map(new ConnectorStatusMapper());
    }

    @Override
    public void addChargePoint(String chargeBoxId) {
        try {
            int count = DSL.using(config)
                           .insertInto(CHARGEBOX,
                                   CHARGEBOX.CHARGEBOXID)
                           .values(chargeBoxId)
                           .onDuplicateKeyIgnore() // Important detail
                           .execute();

            if (count == 0) {
                throw new SteveException("A charge point with chargeBoxId '" + chargeBoxId +"' already exists.");
            }
        } catch (DataAccessException e) {
            throw new SteveException("The charge point with chargeBoxId '" + chargeBoxId + "' could NOT be added.", e);
        }
    }

    @Override
    public void deleteChargePoint(String chargeBoxId) {
        try {
            DSL.using(config)
               .delete(CHARGEBOX)
               .where(CHARGEBOX.CHARGEBOXID.equal(chargeBoxId))
               .execute();
        } catch (DataAccessException e) {
            throw new SteveException("The charge point with chargeBoxId '" + chargeBoxId + "' could NOT be deleted.", e);
        }
    }

    @Override
    public List<Integer> getConnectorIds(String chargeBoxId) {
        return DSL.using(config)
                  .select(CONNECTOR.CONNECTORID)
                  .from(CONNECTOR)
                  .where(CONNECTOR.CHARGEBOXID.equal(chargeBoxId))
                  .fetch(CONNECTOR.CONNECTORID);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    @RequiredArgsConstructor
    private class ChargePointSelectMapper implements RecordMapper<Record2<String, String>, ChargePointSelect> {
        private final OcppTransport transport;

        @Override
        public ChargePointSelect map(Record2<String, String> record) {
            return new ChargePointSelect(transport, record.value1(), record.value2());
        }
    }

    private class HeartbeatMapper implements RecordMapper<Record2<String, Timestamp>, Heartbeat> {
        @Override
        public Heartbeat map(Record2<String, Timestamp> r) {
            return Heartbeat.builder()
                            .chargeBoxId(r.value1())
                            .lastTimestamp(DateTimeUtils.humanize(r.value2()))
                            .build();
        }
    }

    private class ConnectorStatusMapper implements
            RecordMapper<Record5<String, Integer, Timestamp, String, String>, ConnectorStatus> {
        @Override
        public ConnectorStatus map(Record5<String, Integer, Timestamp, String, String> r) {
            return ConnectorStatus.builder()
                                  .chargeBoxId(r.value1())
                                  .connectorId(r.value2())
                                  .timeStamp(DateTimeUtils.humanize(r.value3()))
                                  .status(r.value4())
                                  .errorCode(r.value5())
                                  .build();
        }
    }
}