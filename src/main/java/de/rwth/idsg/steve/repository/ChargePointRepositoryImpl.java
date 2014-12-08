package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.OcppVersion;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.repository.dto.Heartbeat;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import jooq.steve.db.tables.records.ChargeboxRecord;
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
import java.util.Map;

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
    public Map<String, String> getChargePointsV12() {
        return this.internalGetChargePoints(OcppVersion.V_12);
    }

    @Override
    public Map<String, String> getChargePointsV15() {
        return this.internalGetChargePoints(OcppVersion.V_15);
    }

    /**
     * SELECT chargeBoxId, endpoint_address
     * FROM chargebox
     * WHERE ocppVersion = ?
     */
    private Map<String,String> internalGetChargePoints(OcppVersion version) {
        return DSL.using(config)
                  .selectFrom(CHARGEBOX)
                  .where(CHARGEBOX.OCPPVERSION.equal(version.getValue()))
                  .fetchMap(CHARGEBOX.CHARGEBOXID, CHARGEBOX.ENDPOINT_ADDRESS);
    }

    /**
     * SELECT chargeBoxId
     * FROM chargebox
     */
    @Override
    public List<String> getChargeBoxIds() {
        return DSL.using(config)
                  .select()
                  .from(CHARGEBOX)
                  .fetch(CHARGEBOX.CHARGEBOXID);
    }

    /**
     * SELECT *
     * FROM chargebox
     * WHERE chargeBoxId = ?
     */
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
                .ocppVersion(r.getOcppversion())
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

    /**
     * SELECT chargeBoxId, lastHeartbeatTimestamp
     * FROM chargebox
     * ORDER BY lastHeartbeatTimestamp DESC
     */
    @Override
    public List<Heartbeat> getChargePointHeartbeats() {
        return DSL.using(config)
                  .select(CHARGEBOX.CHARGEBOXID, CHARGEBOX.LASTHEARTBEATTIMESTAMP)
                  .from(CHARGEBOX)
                  .orderBy(CHARGEBOX.LASTHEARTBEATTIMESTAMP.desc())
                  .fetch()
                  .map(new HeartbeatMapper());
    }

    /**
     * SELECT c.chargeBoxId, c.connectorId, cs.statustimeStamp, cs.status, cs.errorCode
     * FROM connector_status cs
     * INNER JOIN connector c
     *      ON cs.connector_pk = c.connector_pk
     * INNER JOIN (SELECT connector_pk, MAX(statusTimestamp) AS Max FROM connector_status GROUP BY connector_pk) AS t1
     *      ON cs.connector_pk = t1.connector_pk AND cs.statusTimestamp = t1.Max
     * ORDER BY cs.statustimeStamp DESC
     */
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

    /**
     * INSERT IGNORE INTO chargebox (chargeBoxId) VALUES (?)
     */
    @Override
    public void addChargePoint(String chargeBoxId) throws SteveException {
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

    /**
     * DELETE FROM chargebox
     * WHERE chargeBoxId = ?
     */
    @Override
    public void deleteChargePoint(String chargeBoxId) throws SteveException {
        try {
            DSL.using(config)
               .delete(CHARGEBOX)
               .where(CHARGEBOX.CHARGEBOXID.equal(chargeBoxId))
               .execute();
        } catch (DataAccessException e) {
            throw new SteveException("The charge point with chargeBoxId '" + chargeBoxId + "' could NOT be deleted.", e);
        }
    }

    /**
     * SELECT connectorId
     * FROM connector
     * WHERE chargeBoxId = ?
     */
    @Override
    public List<Integer> getConnectorIds(String chargeBoxId) {
        return DSL.using(config)
                  .select()
                  .from(CONNECTOR)
                  .where(CONNECTOR.CHARGEBOXID.equal(chargeBoxId))
                  .fetch(CONNECTOR.CONNECTORID);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

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