/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import ocpp.cs._2012._06.UnitOfMeasure;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TransactionServiceTest {

    private static final int TRANSACTION_PK = 42;
    private static final String START_VALUE = "1000";
    private static final DateTime START_TIMESTAMP = DateTime.parse("2026-06-30T10:00:00Z");

    @Test
    public void findLast_usesPreviousStreakWhenLatestReadingBreaksMonotonicity() {
        // given
        DateTime t1 = START_TIMESTAMP.plusMinutes(1);
        DateTime t2 = START_TIMESTAMP.plusMinutes(2);
        DateTime t3 = START_TIMESTAMP.plusMinutes(3);
        DateTime t4 = START_TIMESTAMP.plusMinutes(4);
        DateTime t5 = START_TIMESTAMP.plusMinutes(5);

        // when
        var last = TransactionService.findLastMeterValue(
            List.of(
                meterValue("1050", t1),
                meterValue("1100", t2),
                meterValue("1150", t3),
                meterValue("1200", t4),
                meterValue("1050", t5)
            ),
            transactionDetails()
        );

        // then
        Assertions.assertNotNull(last);
        Assertions.assertEquals("1200", last.getValue());
        Assertions.assertEquals(t4, last.getValueTimestamp());
    }

    @Test
    public void findLast_usesLatestReadingFromNewStreakAfterMonotonicityBreak() {
        // given
        DateTime t1 = START_TIMESTAMP.plusMinutes(1);
        DateTime t2 = START_TIMESTAMP.plusMinutes(2);
        DateTime t3 = START_TIMESTAMP.plusMinutes(3);
        DateTime t4 = START_TIMESTAMP.plusMinutes(4);

        // when
        var last = TransactionService.findLastMeterValue(
            List.of(
                meterValue("1100", t1),
                meterValue("1200", t2),
                meterValue("900", t3),
                meterValue("1300", t4)
            ),
            transactionDetails()
        );

        // then
        Assertions.assertNotNull(last);
        Assertions.assertEquals("1300", last.getValue());
        Assertions.assertEquals(t4, last.getValueTimestamp());
    }

    @Test
    public void findLast_comparesKWhAndWhValuesWithinSameStreak() {
        // given
        DateTime t1 = START_TIMESTAMP.plusMinutes(1);
        DateTime t2 = START_TIMESTAMP.plusMinutes(2);
        DateTime t3 = START_TIMESTAMP.plusMinutes(3);

        // when
        var last = TransactionService.findLastMeterValue(
            List.of(
                meterValue("1.1", UnitOfMeasure.K_WH.value(), t1),
                meterValue("1200", UnitOfMeasure.WH.value(), t2),
                meterValue("1.15", UnitOfMeasure.K_WH.value(), t3)
            ),
            transactionDetails()
        );

        // then
        Assertions.assertNotNull(last);
        Assertions.assertEquals("1200", last.getValue());
        Assertions.assertEquals(t2, last.getValueTimestamp());
    }

    private static Transaction transactionDetails() {
        return Transaction.builder()
            .id(TRANSACTION_PK)
            .chargeBoxId("charge-box")
            .startValue(START_VALUE)
            .startTimestamp(START_TIMESTAMP)
            .build();
    }

    private static TransactionDetails.MeterValues meterValue(String value, DateTime timestamp) {
        return meterValue(value, UnitOfMeasure.WH.value(), timestamp);
    }

    private static TransactionDetails.MeterValues meterValue(String value, String unit, DateTime timestamp) {
        return TransactionDetails.MeterValues.builder()
            .value(value)
            .valueTimestamp(timestamp)
            .unit(unit)
            .build();
    }
}
