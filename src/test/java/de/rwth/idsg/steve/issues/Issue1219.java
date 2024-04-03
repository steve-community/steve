/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
package de.rwth.idsg.steve.issues;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.OcppTag;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import de.rwth.idsg.steve.repository.impl.AddressRepositoryImpl;
import de.rwth.idsg.steve.repository.impl.ChargePointRepositoryImpl;
import de.rwth.idsg.steve.repository.impl.OcppServerRepositoryImpl;
import de.rwth.idsg.steve.repository.impl.OcppTagRepositoryImpl;
import de.rwth.idsg.steve.repository.impl.ReservationRepositoryImpl;
import de.rwth.idsg.steve.repository.impl.TransactionRepositoryImpl;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import de.rwth.idsg.steve.web.dto.OcppTagQueryForm;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import jooq.steve.db.enums.TransactionStopEventActor;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.tools.jdbc.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class Issue1219 {

    private static final String url = "jdbc:mysql://localhost:3306/stevedb";
    private static final String userName = "steve";
    private static final String password = "changeme";

    private final DSLContext ctx;

    public static void main(String[] args) throws Exception {
        Connection con = DriverManager.getConnection(url, userName, password);

        org.jooq.Configuration conf = new DefaultConfiguration()
            .set(SQLDialect.MYSQL)
            .set(new DataSourceConnectionProvider(new SingleConnectionDataSource(con)));

        DSLContext ctx = DSL.using(conf);

        Issue1219 issue1219 = new Issue1219(ctx);

        var ocppTags = issue1219.insertOcppTags(1_000);
        System.out.println("inserted ocppTags");

        var chargeBoxIds = issue1219.insertChargeBoxes(500);
        System.out.println("inserted chargeBoxIds");

        var transactionIds = issue1219.insertStartTransactions(10_000, ocppTags, chargeBoxIds);
        System.out.println("inserted transaction_starts");

        var stoppedTransactionIds = issue1219.insertStopTransactions(transactionIds);
        System.out.println("inserted transaction_stops: " + stoppedTransactionIds.size());

        System.out.println("-- TESTING --");
        issue1219.realTest();
    }

    private void realTest() {
        var repository = new OcppTagRepositoryImpl(ctx);

        long start = System.currentTimeMillis();
        List<OcppTag.Overview> values = repository.getOverview(new OcppTagQueryForm());
        long stop = System.currentTimeMillis();

        System.out.println("took " + Duration.millis(stop - start));
    }

    private List<Integer> insertStopTransactions(List<Integer> insertedTransactionIds) {
        var ocppServerRepository = new OcppServerRepositoryImpl(ctx, new ReservationRepositoryImpl(ctx));
        var transactionRepository = new TransactionRepositoryImpl(ctx);

        List<Integer> stopped = new ArrayList<>();
        for (Integer transactionId : insertedTransactionIds) {
            if (!ThreadLocalRandom.current().nextBoolean()) {
                continue;
            }

            TransactionQueryForm form = new TransactionQueryForm();
            form.setTransactionPk(transactionId);
            Transaction transaction = transactionRepository.getTransactions(form).get(0);

            DateTime stopTimestamp = transaction.getStartTimestamp().plusHours(1);
            UpdateTransactionParams p = UpdateTransactionParams.builder()
                .chargeBoxId(transaction.getChargeBoxId())
                .transactionId(transaction.getId())
                .stopTimestamp(stopTimestamp)
                .eventTimestamp(stopTimestamp)
                .stopMeterValue(transaction.getStartValue() + "0")
                .eventActor(TransactionStopEventActor.station)
                .build();

            ocppServerRepository.updateTransaction(p);
            System.out.println("stopped transaction " + transactionId);
            stopped.add(transactionId);
        }
        return stopped;
    }

    private List<Integer> insertStartTransactions(int count, List<String> ocppTags, List<String> chargeBoxIds) {
        var repository = new OcppServerRepositoryImpl(ctx, new ReservationRepositoryImpl(ctx));

        List<Integer> transactionIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            DateTime now = DateTime.now();
            InsertTransactionParams params = InsertTransactionParams.builder()
                .idTag(ocppTags.get(ThreadLocalRandom.current().nextInt(0, ocppTags.size())))
                .chargeBoxId(chargeBoxIds.get(ThreadLocalRandom.current().nextInt(0, chargeBoxIds.size())))
                .connectorId(ThreadLocalRandom.current().nextInt(4))
                .startMeterValue(String.valueOf(ThreadLocalRandom.current().nextLong(5_000, 20_000)))
                .startTimestamp(now)
                .eventTimestamp(now)
                .build();
            int transactionId = repository.insertTransaction(params);
            System.out.println("started transaction " + transactionId);
            transactionIds.add(transactionId);
        }
        return transactionIds;
    }

    private List<String> insertChargeBoxes(int count) {
        var repository = new ChargePointRepositoryImpl(ctx, new AddressRepositoryImpl());

        List<String> ids = IntStream.range(0, count).mapToObj(val -> UUID.randomUUID().toString()).collect(Collectors.toList());
        repository.addChargePointList(ids);

        return ids;
    }

    private List<String> insertOcppTags(int count) {
        var repository = new OcppTagRepositoryImpl(ctx);

        List<String> idTags = IntStream.range(0, count).mapToObj(val -> UUID.randomUUID().toString()).collect(Collectors.toList());
        List<String> insertedTags = new ArrayList<>();

        for (String idTag : idTags) {
            OcppTagForm form = new OcppTagForm();
            form.setIdTag(idTag);
            form.setExpiryDate(getRandomExpiry());
            form.setParentIdTag(getRandomParentIdTag(idTag, insertedTags));
            form.setMaxActiveTransactionCount(ThreadLocalRandom.current().nextInt(1, 4));

            try {
                repository.addOcppTag(form);
            } catch (SteveException.AlreadyExists e) {
                // because the referenced idTag was not inserted yet. just inserted without it.
                form.setParentIdTag(null);
                repository.addOcppTag(form);
            }
            insertedTags.add(idTag);
        }

        return insertedTags;
    }

    private static String getRandomParentIdTag(String current, List<String> source) {
        if (source.isEmpty()) {
            return null;
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            String parent = source.get(ThreadLocalRandom.current().nextInt(0, source.size()));
            if (!Objects.equals(parent, current)) {
                return parent;
            }
        }
        return null;
    }

    private static LocalDateTime getRandomExpiry() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return LocalDateTime.now().plusDays(ThreadLocalRandom.current().nextInt(1, 365));
        }
        return null;
    }
}
