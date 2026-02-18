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

import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import jooq.steve.db.enums.TransactionStopEventActor;
import jooq.steve.db.tables.records.TransactionRecord;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.02.2026
 */
public class CentralSystemService16ServiceValidatorTest {

    private static final Instant NOW = Instant.parse("2026-02-17T12:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    private final CentralSystemService16_ServiceValidator validator = new CentralSystemService16_ServiceValidator(FIXED_CLOCK);

    @Test
    public void validateStop_eventActorManual_ignored() {
        var params = params(TransactionStopEventActor.manual, new DateTime(NOW.toEpochMilli()), "200");
        var result = validator.validateStop(null, params);

        Assertions.assertNull(result);
    }

    @Test
    public void validateStop_transactionMissing_returnsError() {
        var params = params(TransactionStopEventActor.station, new DateTime(NOW.toEpochMilli()), "200");
        var result = validator.validateStop(null, params);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("The transaction is not found in database", result.getMessage());
    }

    @Test
    public void validateStop_transactionAlreadyStoppedByStation_returnsError() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), "150", DateTime.parse("2026-02-17T10:00:00Z"), TransactionStopEventActor.station);
        var params = params(TransactionStopEventActor.station, DateTime.parse("2026-02-17T10:30:00Z"), "200");
        var result = validator.validateStop(tx, params);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("The transaction was already stopped by the station", result.getMessage());
    }

    @Test
    public void validateStop_transactionAlreadyStoppedManually_isAllowed() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), "150", DateTime.parse("2026-02-17T10:00:00Z"), TransactionStopEventActor.manual);
        var params = params(TransactionStopEventActor.station, DateTime.parse("2026-02-17T10:30:00Z"), "200");
        var result = validator.validateStop(tx, params);

        Assertions.assertNull(result);
    }

    @Test
    public void validateStop_startAfterStop_returnsError() {
        var tx = tx("100", DateTime.parse("2026-02-17T12:01:00Z"), null, null, null);
        var params = params(TransactionStopEventActor.station, DateTime.parse("2026-02-17T12:00:00Z"), "200");
        var result = validator.validateStop(tx, params);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("start.timestamp is after stop.timestamp", result.getMessage());
    }

    @Test
    public void validateStop_futureStopTimestamp_returnsError() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = params(TransactionStopEventActor.station, DateTime.parse("2026-02-17T12:05:01Z"), "200");
        var result = validator.validateStop(tx, params);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("stop.timestamp is in the future", result.getMessage());
    }

    @Test
    public void validateStop_futureStopTimestampAtBoundary_isAllowed() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = params(TransactionStopEventActor.station, DateTime.parse("2026-02-17T12:05:00Z"), "200");
        var result = validator.validateStop(tx, params);

        Assertions.assertNull(result);
    }

    @Test
    public void validateStop_stopMeterLowerThanStart_returnsError() {
        var tx = tx("300", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = params(TransactionStopEventActor.station, DateTime.parse("2026-02-17T10:00:00Z"), "200");
        var result = validator.validateStop(tx, params);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("meterStart is greater than meterStop", result.getMessage());
    }

    @Test
    public void validateStop_validStationStop_returnsNull() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = params(TransactionStopEventActor.station, DateTime.parse("2026-02-17T10:00:00Z"), "200");
        var result = validator.validateStop(tx, params);

        Assertions.assertNull(result);
    }

    private static UpdateTransactionParams params(TransactionStopEventActor actor,
                                                  DateTime stopTimestamp,
                                                  String meterStop) {
        return UpdateTransactionParams.builder()
            .chargeBoxId("box-1")
            .transactionId(1)
            .stopTimestamp(stopTimestamp)
            .stopMeterValue(meterStop)
            .eventTimestamp(new DateTime(NOW.toEpochMilli()))
            .eventActor(actor)
            .build();
    }

    private static TransactionRecord tx(String startValue, DateTime startTimestamp,
                                        String stopValue, DateTime stopTimestamp,
                                        TransactionStopEventActor stopActor) {
        TransactionRecord tx = Mockito.mock(TransactionRecord.class);
        Mockito.when(tx.getStartValue()).thenReturn(startValue);
        Mockito.when(tx.getStartTimestamp()).thenReturn(startTimestamp);
        Mockito.when(tx.getStopValue()).thenReturn(stopValue);
        Mockito.when(tx.getStopTimestamp()).thenReturn(stopTimestamp);
        Mockito.when(tx.getStopEventActor()).thenReturn(stopActor);
        return tx;
    }


}
