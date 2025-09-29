/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve.utils;

import com.google.common.collect.Sets;
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.repository.impl.AddressRepositoryImpl;
import de.rwth.idsg.steve.repository.impl.ChargePointRepositoryImpl;
import de.rwth.idsg.steve.repository.impl.OcppTagRepositoryImpl;
import de.rwth.idsg.steve.repository.impl.ReservationRepositoryImpl;
import de.rwth.idsg.steve.repository.impl.TransactionRepositoryImpl;
import de.rwth.idsg.steve.web.dto.ReservationQueryForm;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import jooq.steve.db.DefaultCatalog;
import jooq.steve.db.tables.OcppTagActivity;
import jooq.steve.db.tables.SchemaVersion;
import jooq.steve.db.tables.Settings;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import jooq.steve.db.tables.records.TransactionRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toLocalDateTime;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.Transaction.TRANSACTION;

/**
 * This is a dangerous class. It performs database operations no class should do, like truncating all tables and
 * inserting data while bypassing normal mechanisms of SteVe. However, for integration testing with reproducible
 * results we need a clean and isolated database.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 21.03.2018
 */
@RequiredArgsConstructor
public class __DatabasePreparer__ {

    private static final String REGISTERED_CHARGE_BOX_ID = "charge_box_2aa6a783d47d";
    private static final String REGISTERED_CHARGE_BOX_ID_2 = "charge_box_2aa6a783d47d_2";
    private static final String REGISTERED_OCPP_TAG = "id_tag_2aa6a783d47d";

    private final DSLContext dslContext;
    private final SteveProperties steveProperties;

    public void prepare() {
        truncateTables(dslContext, steveProperties.getJooq().getSchemaSource());
        insertChargeBox(dslContext);
        insertOcppIdTag(dslContext);
    }

    public int makeReservation(int connectorId) {
        var r = new ReservationRepositoryImpl(dslContext);
        var params = InsertReservationParams.builder()
                .chargeBoxId(REGISTERED_CHARGE_BOX_ID)
                .idTag(REGISTERED_OCPP_TAG)
                .connectorId(connectorId)
                .expiryTimestamp(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();
        var reservationId = r.insert(params);
        r.accepted(reservationId);
        return reservationId;
    }

    public void cleanUp() {
        truncateTables(dslContext, steveProperties.getJooq().getSchemaSource());
    }

    public static String getRegisteredChargeBoxId() {
        return REGISTERED_CHARGE_BOX_ID;
    }

    public static String getRegisteredChargeBoxId2() {
        return REGISTERED_CHARGE_BOX_ID_2;
    }

    public static String getRegisteredOcppTag() {
        return REGISTERED_OCPP_TAG;
    }

    public List<Transaction> getTransactions() {
        var impl = new TransactionRepositoryImpl(dslContext);
        return impl.getTransactions(new TransactionQueryForm());
    }

    public List<TransactionRecord> getTransactionRecords() {
        return dslContext.selectFrom(TRANSACTION).fetch();
    }

    public List<Reservation> getReservations() {
        var impl = new ReservationRepositoryImpl(dslContext);
        return impl.getReservations(new ReservationQueryForm());
    }

    public List<ConnectorStatus> getChargePointConnectorStatus() {
        var impl = new ChargePointRepositoryImpl(dslContext, new AddressRepositoryImpl(dslContext));
        return impl.getChargePointConnectorStatus();
    }

    public TransactionDetails getDetails(int transactionPk) {
        var impl = new TransactionRepositoryImpl(dslContext);
        return impl.getDetails(transactionPk).orElseThrow();
    }

    public OcppTagActivityRecord getOcppTagRecord(String idTag) {
        var impl = new OcppTagRepositoryImpl(dslContext);
        var dto = impl.getRecord(idTag).orElseThrow();
        var activity = new OcppTagActivityRecord();
        activity.setOcppTagPk(dto.getOcppTagPk());
        activity.setIdTag(dto.getIdTag());
        activity.setParentIdTag(dto.getParentIdTag());
        activity.setExpiryDate(toLocalDateTime(dto.getExpiryDate()));
        activity.setInTransaction(dto.isInTransaction());
        activity.setBlocked(dto.isBlocked());
        activity.setMaxActiveTransactionCount(dto.getMaxActiveTransactionCount());
        activity.setActiveTransactionCount(dto.getActiveTransactionCount());
        activity.setNote(dto.getNote());
        return activity;
    }

    public ChargePoint.Details getCBDetails(String chargeboxID) {
        var impl = new ChargePointRepositoryImpl(dslContext, new AddressRepositoryImpl(dslContext));
        var pkMap = impl.getChargeBoxIdPkPair(Collections.singletonList(chargeboxID));
        int pk = pkMap.get(chargeboxID);
        return impl.getDetails(pk).orElseThrow();
    }

    private static void truncateTables(DSLContext ctx, String schemaToTruncate) {
        var skipList = Sets.newHashSet(
                SchemaVersion.SCHEMA_VERSION,
                Settings.SETTINGS,
                OcppTagActivity.OCPP_TAG_ACTIVITY, // only a view
                TRANSACTION // only a view
                );

        ctx.transaction(configuration -> {
            var schema = DefaultCatalog.DEFAULT_CATALOG.getSchemas().stream()
                    .filter(s -> schemaToTruncate.equals(s.getName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Could not find schema"));

            var tables = schema.getTables().stream()
                    .filter(t -> !skipList.contains(t))
                    .toList();

            if (tables.isEmpty()) {
                throw new RuntimeException("Could not find tables to truncate");
            }

            var internalCtx = DSL.using(configuration);
            internalCtx.execute("SET FOREIGN_KEY_CHECKS=0");
            tables.forEach(t -> internalCtx.truncate(t).execute());
            internalCtx.execute("SET FOREIGN_KEY_CHECKS=1");
        });
    }

    private static void insertChargeBox(DSLContext ctx) {
        ctx.insertInto(CHARGE_BOX)
                .set(CHARGE_BOX.CHARGE_BOX_ID, getRegisteredChargeBoxId())
                .execute();

        ctx.insertInto(CHARGE_BOX)
                .set(CHARGE_BOX.CHARGE_BOX_ID, getRegisteredChargeBoxId2())
                .execute();
    }

    private static void insertOcppIdTag(DSLContext ctx) {
        ctx.insertInto(OCPP_TAG).set(OCPP_TAG.ID_TAG, getRegisteredOcppTag()).execute();
    }
}
