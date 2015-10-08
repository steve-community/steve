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
import jooq.steve.db.tables.records.ChargeboxRecord;
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
        final OcppTransport transport = protocol.getTransport();

        return DSL.using(config)
                  .select(CHARGEBOX.CHARGEBOXID, CHARGEBOX.ENDPOINT_ADDRESS)
                  .from(CHARGEBOX)
                  .where(CHARGEBOX.OCPPPROTOCOL.equal(protocol.getCompositeValue()))
                  .and(CHARGEBOX.ENDPOINT_ADDRESS.isNotNull())
                  .fetch()
                  .map(r -> new ChargePointSelect(transport, r.value1(), r.value2()));
    }

    @Override
    public List<String> getChargeBoxIds() {
        return DSL.using(config)
                  .select(CHARGEBOX.CHARGEBOXID)
                  .from(CHARGEBOX)
                  .fetch(CHARGEBOX.CHARGEBOXID);
    }

    @Override
    public ChargePoint getDetails(String chargeBoxId) {
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
                          .note(r.getNote())
                          .build();
    }

    @Override
    public ChargePoint getDetailsForUpdate(String chargeBoxId) {
        String note = DSL.using(config)
                         .select(CHARGEBOX.NOTE)
                         .from(CHARGEBOX)
                         .where(CHARGEBOX.CHARGEBOXID.equal(chargeBoxId))
                         .fetchOne(CHARGEBOX.NOTE);

        return ChargePoint.builder()
                          .note(note)
                          .build();
    }

    @Override
    public List<Heartbeat> getChargePointHeartbeats() {
        return DSL.using(config)
                  .select(CHARGEBOX.CHARGEBOXID, CHARGEBOX.LASTHEARTBEATTIMESTAMP)
                  .from(CHARGEBOX)
                  .orderBy(CHARGEBOX.LASTHEARTBEATTIMESTAMP.desc())
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
        Field<DateTime> t1Max = DSL.max(CONNECTOR_STATUS.STATUSTIMESTAMP).as("t1_max");
        TableLike<?> t1 = DSL.select(t1Pk, t1Max)
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
                  .on(CONNECTOR_STATUS.CONNECTOR_PK.equal(t1.field(t1Pk)))
                  .and(CONNECTOR_STATUS.STATUSTIMESTAMP.equal(t1.field(t1Max)))
                  .orderBy(CONNECTOR_STATUS.STATUSTIMESTAMP.desc())
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
                  .select(CONNECTOR.CONNECTORID)
                  .from(CONNECTOR)
                  .where(CONNECTOR.CHARGEBOXID.equal(chargeBoxId))
                  .fetch(CONNECTOR.CONNECTORID);
    }

    @Override
    public void addChargePoint(ChargeBoxForm form) {
        try {
            int count = DSL.using(config)
                           .insertInto(CHARGEBOX,
                                   CHARGEBOX.CHARGEBOXID, CHARGEBOX.NOTE)
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
               .update(CHARGEBOX)
               .set(CHARGEBOX.NOTE, form.getNote())
               .where(CHARGEBOX.CHARGEBOXID.equal(form.getChargeBoxId()))
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
               .delete(CHARGEBOX)
               .where(CHARGEBOX.CHARGEBOXID.equal(chargeBoxId))
               .execute();
        } catch (DataAccessException e) {
            throw new SteveException("The charge point with chargeBoxId '%s' could NOT be deleted.", chargeBoxId, e);
        }
    }
}
