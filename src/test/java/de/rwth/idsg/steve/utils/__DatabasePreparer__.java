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
package de.rwth.idsg.steve.utils;

import com.google.common.collect.Sets;
import de.rwth.idsg.steve.config.BeanConfiguration;
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
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
public class __DatabasePreparer__ {

    private static final String SCHEMA_TO_TRUNCATE = "stevedb_test_2aa6a783d47d";
    private static final String REGISTERED_CHARGE_BOX_ID = "charge_box_2aa6a783d47d";
    private static final String REGISTERED_CHARGE_BOX_ID_2 = "charge_box_2aa6a783d47d_2";
    private static final String REGISTERED_OCPP_TAG = "id_tag_2aa6a783d47d";

    private static final BeanConfiguration beanConfiguration = new BeanConfiguration();
    private static final DSLContext dslContext = beanConfiguration.dslContext();

    public static void prepare() {
        runOperation(ctx -> {
            truncateTables(ctx);
            insertChargeBox(ctx);
            insertOcppIdTag(ctx);
        });
    }

    public static int makeReservation(int connectorId) {
        ReservationRepositoryImpl r = new ReservationRepositoryImpl(dslContext);
        InsertReservationParams params = InsertReservationParams.builder()
                                                                .chargeBoxId(REGISTERED_CHARGE_BOX_ID)
                                                                .idTag(REGISTERED_OCPP_TAG)
                                                                .connectorId(connectorId)
                                                                .expiryTimestamp(DateTime.now().plusHours(1))
                                                                .build();
        int reservationId = r.insert(params);
        r.accepted(reservationId);
        return reservationId;
    }

    public static void cleanUp() {
        runOperation(__DatabasePreparer__::truncateTables);
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

    public static List<Transaction> getTransactions() {
        TransactionRepositoryImpl impl = new TransactionRepositoryImpl(dslContext);
        return impl.getTransactions(new TransactionQueryForm());
    }
    public static List<TransactionRecord> getTransactionRecords() {
        return dslContext.selectFrom(TRANSACTION).fetch();
    }

    public static List<Reservation> getReservations() {
        ReservationRepositoryImpl impl = new ReservationRepositoryImpl(dslContext);
        return impl.getReservations(new ReservationQueryForm());
    }

    public static List<ConnectorStatus> getChargePointConnectorStatus() {
        ChargePointRepositoryImpl impl = new ChargePointRepositoryImpl(dslContext, new AddressRepositoryImpl());
        return impl.getChargePointConnectorStatus();
    }

    public static TransactionDetails getDetails(int transactionPk) {
        TransactionRepositoryImpl impl = new TransactionRepositoryImpl(dslContext);
        return impl.getDetails(transactionPk);
    }

    public static OcppTagActivityRecord getOcppTagRecord(String idTag) {
        OcppTagRepositoryImpl impl = new OcppTagRepositoryImpl(dslContext);
        return impl.getRecord(idTag);
    }

    public static ChargePoint.Details getCBDetails(String chargeboxID) {
        ChargePointRepositoryImpl impl = new ChargePointRepositoryImpl(dslContext, new AddressRepositoryImpl());
        Map<String, Integer> pkMap = impl.getChargeBoxIdPkPair(Arrays.asList(chargeboxID));
        int pk = pkMap.get(chargeboxID);
        return impl.getDetails(pk);
    }

    private static void runOperation(Consumer<DSLContext> consumer) {
        consumer.accept(dslContext);
    }

    private static void truncateTables(DSLContext ctx) {
        Set<Table<?>> skipList = Sets.newHashSet(
                SchemaVersion.SCHEMA_VERSION,
                Settings.SETTINGS,
                OcppTagActivity.OCPP_TAG_ACTIVITY, // only a view
                TRANSACTION // only a view
        );

        ctx.transaction(configuration -> {
            Schema schema = DefaultCatalog.DEFAULT_CATALOG.getSchemas()
                                                          .stream()
                                                          .filter(s -> SCHEMA_TO_TRUNCATE.equals(s.getName()))
                                                          .findFirst()
                                                          .orElseThrow(() -> new RuntimeException("Could not find schema"));

            List<Table<?>> tables = schema.getTables()
                                          .stream()
                                          .filter(t -> !skipList.contains(t))
                                          .collect(Collectors.toList());

            if (tables.isEmpty()) {
                throw new RuntimeException("Could not find tables to truncate");
            }

            DSLContext internalCtx = DSL.using(configuration);
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
        ctx.insertInto(OCPP_TAG)
           .set(OCPP_TAG.ID_TAG, getRegisteredOcppTag())
           .execute();
    }
}
