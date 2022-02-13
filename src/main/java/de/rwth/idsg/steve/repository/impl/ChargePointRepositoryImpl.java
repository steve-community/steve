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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.AddressRepository;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import jooq.steve.db.tables.records.AddressRecord;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;
import org.joda.time.DateTime;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.CustomDSL.date;
import static de.rwth.idsg.steve.utils.CustomDSL.includes;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.ConnectorStatus.CONNECTOR_STATUS;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 14.08.2014
 */
@Slf4j
@Repository
public class ChargePointRepositoryImpl implements ChargePointRepository {

    private final DSLContext ctx;
    private final AddressRepository addressRepository;

    @Autowired
    public ChargePointRepositoryImpl(DSLContext ctx, AddressRepository addressRepository) {
        this.ctx = ctx;
        this.addressRepository = addressRepository;
    }

    @Override
    public Optional<String> getRegistrationStatus(String chargeBoxId) {
        String status = ctx.select(CHARGE_BOX.REGISTRATION_STATUS)
                           .from(CHARGE_BOX)
                           .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxId))
                           .fetchOne(CHARGE_BOX.REGISTRATION_STATUS);

        return Optional.ofNullable(status);
    }

    @Override
    public List<ChargePointSelect> getChargePointSelect(OcppProtocol protocol, List<String> inStatusFilter) {
        return ctx.select(CHARGE_BOX.CHARGE_BOX_ID, CHARGE_BOX.ENDPOINT_ADDRESS)
                  .from(CHARGE_BOX)
                  .where(CHARGE_BOX.OCPP_PROTOCOL.equal(protocol.getCompositeValue()))
                  .and(CHARGE_BOX.ENDPOINT_ADDRESS.isNotNull())
                  .and(CHARGE_BOX.REGISTRATION_STATUS.in(inStatusFilter))
                  .fetch()
                  .map(r -> new ChargePointSelect(protocol.getTransport(), r.value1(), r.value2()));
    }

    @Override
    public List<String> getChargeBoxIds() {
        return ctx.select(CHARGE_BOX.CHARGE_BOX_ID)
                  .from(CHARGE_BOX)
                  .fetch(CHARGE_BOX.CHARGE_BOX_ID);
    }

    @Override
    public Map<String, Integer> getChargeBoxIdPkPair(List<String> chargeBoxIdList) {
        return ctx.select(CHARGE_BOX.CHARGE_BOX_ID, CHARGE_BOX.CHARGE_BOX_PK)
                  .from(CHARGE_BOX)
                  .where(CHARGE_BOX.CHARGE_BOX_ID.in(chargeBoxIdList))
                  .fetchMap(CHARGE_BOX.CHARGE_BOX_ID, CHARGE_BOX.CHARGE_BOX_PK);
    }

    @Override
    public List<ChargePoint.Overview> getOverview(ChargePointQueryForm form) {
        return getOverviewInternal(form)
                .map(r -> ChargePoint.Overview.builder()
                                              .chargeBoxPk(r.value1())
                                              .chargeBoxId(r.value2())
                                              .description(r.value3())
                                              .ocppProtocol(r.value4())
                                              .lastHeartbeatTimestampDT(r.value5())
                                              .lastHeartbeatTimestamp(DateTimeUtils.humanize(r.value5()))
                                              .build()
                );
    }

    @SuppressWarnings("unchecked")
    private Result<Record5<Integer, String, String, String, DateTime>> getOverviewInternal(ChargePointQueryForm form) {
        SelectQuery selectQuery = ctx.selectQuery();
        selectQuery.addFrom(CHARGE_BOX);
        selectQuery.addSelect(
                CHARGE_BOX.CHARGE_BOX_PK,
                CHARGE_BOX.CHARGE_BOX_ID,
                CHARGE_BOX.DESCRIPTION,
                CHARGE_BOX.OCPP_PROTOCOL,
                CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP
        );

        if (form.isSetOcppVersion()) {

            // http://dev.mysql.com/doc/refman/5.7/en/pattern-matching.html
            selectQuery.addConditions(CHARGE_BOX.OCPP_PROTOCOL.like(form.getOcppVersion().getValue() + "_"));
        }

        if (form.isSetDescription()) {
            selectQuery.addConditions(includes(CHARGE_BOX.DESCRIPTION, form.getDescription()));
        }

        if (form.isSetChargeBoxId()) {
            selectQuery.addConditions(includes(CHARGE_BOX.CHARGE_BOX_ID, form.getChargeBoxId()));
        }

        switch (form.getHeartbeatPeriod()) {
            case ALL:
                break;

            case TODAY:
                selectQuery.addConditions(
                        date(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP).eq(date(DateTime.now()))
                );
                break;

            case YESTERDAY:
                selectQuery.addConditions(
                        date(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP).eq(date(DateTime.now().minusDays(1)))
                );
                break;

            case EARLIER:
                selectQuery.addConditions(
                        date(CHARGE_BOX.LAST_HEARTBEAT_TIMESTAMP).lessThan(date(DateTime.now().minusDays(1)))
                );
                break;

            default:
                throw new SteveException("Unknown enum type");
        }

        // Default order
        selectQuery.addOrderBy(CHARGE_BOX.CHARGE_BOX_PK.asc());

        return selectQuery.fetch();
    }

    @Override
    public ChargePoint.Details getDetails(int chargeBoxPk) {
        ChargeBoxRecord cbr = ctx.selectFrom(CHARGE_BOX)
                                 .where(CHARGE_BOX.CHARGE_BOX_PK.equal(chargeBoxPk))
                                 .fetchOne();

        if (cbr == null) {
            throw new SteveException("Charge point not found");
        }

        AddressRecord ar = addressRepository.get(ctx, cbr.getAddressPk());

        return new ChargePoint.Details(cbr, ar);
    }

    @Override
    public List<ConnectorStatus> getChargePointConnectorStatus(ConnectorStatusForm form) {
        // find out the latest timestamp for each connector
        Field<Integer> t1Pk = CONNECTOR_STATUS.CONNECTOR_PK.as("t1_pk");
        Field<DateTime> t1TsMax = DSL.max(CONNECTOR_STATUS.STATUS_TIMESTAMP).as("t1_ts_max");
        Table<?> t1 = ctx.select(t1Pk, t1TsMax)
                         .from(CONNECTOR_STATUS)
                         .groupBy(CONNECTOR_STATUS.CONNECTOR_PK)
                         .asTable("t1");

        // get the status table with latest timestamps only
        Field<Integer> t2Pk = CONNECTOR_STATUS.CONNECTOR_PK.as("t2_pk");
        Field<DateTime> t2Ts = CONNECTOR_STATUS.STATUS_TIMESTAMP.as("t2_ts");
        Field<String> t2Status = CONNECTOR_STATUS.STATUS.as("t2_status");
        Field<String> t2Error = CONNECTOR_STATUS.ERROR_CODE.as("t2_error");
        Table<?> t2 = ctx.selectDistinct(t2Pk, t2Ts, t2Status, t2Error)
                         .from(CONNECTOR_STATUS)
                         .join(t1)
                            .on(CONNECTOR_STATUS.CONNECTOR_PK.equal(t1.field(t1Pk)))
                            .and(CONNECTOR_STATUS.STATUS_TIMESTAMP.equal(t1.field(t1TsMax)))
                         .asTable("t2");

        // https://github.com/RWTH-i5-IDSG/steve/issues/691
        Condition chargeBoxCondition = CHARGE_BOX.REGISTRATION_STATUS.eq(RegistrationStatus.ACCEPTED.value());

        if (form != null && form.getChargeBoxId() != null) {
            chargeBoxCondition = chargeBoxCondition.and(CHARGE_BOX.CHARGE_BOX_ID.eq(form.getChargeBoxId()));
        }

        final Condition statusCondition;
        if (form == null || form.getStatus() == null) {
            statusCondition = DSL.noCondition();
        } else {
            statusCondition = t2.field(t2Status).eq(form.getStatus());
        }

        return ctx.select(
                        CHARGE_BOX.CHARGE_BOX_PK,
                        CONNECTOR.CHARGE_BOX_ID,
                        CONNECTOR.CONNECTOR_ID,
                        t2.field(t2Ts),
                        t2.field(t2Status),
                        t2.field(t2Error),
                        CHARGE_BOX.OCPP_PROTOCOL)
                  .from(t2)
                  .join(CONNECTOR)
                        .on(CONNECTOR.CONNECTOR_PK.eq(t2.field(t2Pk)))
                  .join(CHARGE_BOX)
                        .on(CHARGE_BOX.CHARGE_BOX_ID.eq(CONNECTOR.CHARGE_BOX_ID))
                  .where(chargeBoxCondition, statusCondition)
                  .orderBy(t2.field(t2Ts).desc())
                  .fetch()
                  .map(r -> ConnectorStatus.builder()
                                           .chargeBoxPk(r.value1())
                                           .chargeBoxId(r.value2())
                                           .connectorId(r.value3())
                                           .timeStamp(DateTimeUtils.humanize(r.value4()))
                                           .statusTimestamp(r.value4())
                                           .status(r.value5())
                                           .errorCode(r.value6())
                                           .ocppProtocol(r.value7() == null ? null : OcppProtocol.fromCompositeValue(r.value7()))
                                           .build()
                  );
    }

    @Override
    public List<Integer> getNonZeroConnectorIds(String chargeBoxId) {
        return ctx.select(CONNECTOR.CONNECTOR_ID)
                  .from(CONNECTOR)
                  .where(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxId))
                  .and(CONNECTOR.CONNECTOR_ID.notEqual(0))
                  .fetch(CONNECTOR.CONNECTOR_ID);
    }

    @Override
    public void addChargePointList(List<String> chargeBoxIdList) {
        List<ChargeBoxRecord> batch = chargeBoxIdList.stream()
                                                     .map(s -> ctx.newRecord(CHARGE_BOX)
                                                                  .setChargeBoxId(s)
                                                                  .setInsertConnectorStatusAfterTransactionMsg(false))
                                                     .collect(Collectors.toList());

        ctx.batchInsert(batch).execute();
    }

    @Override
    public int addChargePoint(ChargePointForm form) {
        return ctx.transactionResult(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                Integer addressId = addressRepository.updateOrInsert(ctx, form.getAddress());
                return addChargePointInternal(ctx, form, addressId);

            } catch (DataAccessException e) {
                throw new SteveException("Failed to add the charge point with chargeBoxId '%s'",
                        form.getChargeBoxId(), e);
            }
        });
    }

    @Override
    public void updateChargePoint(ChargePointForm form) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                Integer addressId = addressRepository.updateOrInsert(ctx, form.getAddress());
                updateChargePointInternal(ctx, form, addressId);

            } catch (DataAccessException e) {
                throw new SteveException("Failed to update the charge point with chargeBoxId '%s'",
                        form.getChargeBoxId(), e);
            }
        });
    }

    @Override
    public void deleteChargePoint(int chargeBoxPk) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                addressRepository.delete(ctx, selectAddressId(chargeBoxPk));
                deleteChargePointInternal(ctx, chargeBoxPk);

            } catch (DataAccessException e) {
                throw new SteveException("Failed to delete the charge point", e);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private SelectConditionStep<Record1<Integer>> selectAddressId(int chargeBoxPk) {
        return ctx.select(CHARGE_BOX.ADDRESS_PK)
                  .from(CHARGE_BOX)
                  .where(CHARGE_BOX.CHARGE_BOX_PK.eq(chargeBoxPk));
    }

    private int addChargePointInternal(DSLContext ctx, ChargePointForm form, Integer addressPk) {
        return ctx.insertInto(CHARGE_BOX)
                  .set(CHARGE_BOX.CHARGE_BOX_ID, form.getChargeBoxId())
                  .set(CHARGE_BOX.DESCRIPTION, form.getDescription())
                  .set(CHARGE_BOX.LOCATION_LATITUDE, form.getLocationLatitude())
                  .set(CHARGE_BOX.LOCATION_LONGITUDE, form.getLocationLongitude())
                  .set(CHARGE_BOX.INSERT_CONNECTOR_STATUS_AFTER_TRANSACTION_MSG, form.getInsertConnectorStatusAfterTransactionMsg())
                  .set(CHARGE_BOX.REGISTRATION_STATUS, form.getRegistrationStatus())
                  .set(CHARGE_BOX.NOTE, form.getNote())
                  .set(CHARGE_BOX.ADMIN_ADDRESS, form.getAdminAddress())
                  .set(CHARGE_BOX.ADDRESS_PK, addressPk)
                  .returning(CHARGE_BOX.CHARGE_BOX_PK)
                  .fetchOne()
                  .getChargeBoxPk();
    }

    private void updateChargePointInternal(DSLContext ctx, ChargePointForm form, Integer addressPk) {
        ctx.update(CHARGE_BOX)
           .set(CHARGE_BOX.DESCRIPTION, form.getDescription())
           .set(CHARGE_BOX.LOCATION_LATITUDE, form.getLocationLatitude())
           .set(CHARGE_BOX.LOCATION_LONGITUDE, form.getLocationLongitude())
           .set(CHARGE_BOX.INSERT_CONNECTOR_STATUS_AFTER_TRANSACTION_MSG, form.getInsertConnectorStatusAfterTransactionMsg())
           .set(CHARGE_BOX.REGISTRATION_STATUS, form.getRegistrationStatus())
           .set(CHARGE_BOX.NOTE, form.getNote())
           .set(CHARGE_BOX.ADMIN_ADDRESS, form.getAdminAddress())
           .set(CHARGE_BOX.ADDRESS_PK, addressPk)
           .where(CHARGE_BOX.CHARGE_BOX_PK.equal(form.getChargeBoxPk()))
           .execute();
    }

    private void deleteChargePointInternal(DSLContext ctx, int chargeBoxPk) {
        ctx.delete(CHARGE_BOX)
           .where(CHARGE_BOX.CHARGE_BOX_PK.equal(chargeBoxPk))
           .execute();
    }
}
