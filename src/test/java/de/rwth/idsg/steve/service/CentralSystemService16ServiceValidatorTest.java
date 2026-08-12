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

import jooq.steve.db.enums.TransactionStopEventActor;
import jooq.steve.db.tables.records.TransactionRecord;
import ocpp._2022._02.security.SecurityEventNotification;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.ReadingContext;
import ocpp.cs._2015._10.Reason;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.UnitOfMeasure;
import ocpp.cs._2015._10.ValueFormat;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.02.2026
 */
public class CentralSystemService16ServiceValidatorTest {

    private static final Instant NOW = Instant.parse("2026-02-17T12:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final BiFunction<String, String, Boolean> DIFFERENT_PARENTS = (first, second) -> false;

    private final CentralSystemService16_ServiceValidator validator = new CentralSystemService16_ServiceValidator(FIXED_CLOCK);

    @Test
    public void validateStart_connectorIdZero_returnsHardError() {
        var result = validator.validateStart(startParams(0, 10, new DateTime(NOW.toEpochMilli())));

        assertHardErrors(result, "StartTransaction.connectorId must be positive");
    }

    @Test
    public void validateStart_meterStartNegative_returnsHardError() {
        var result = validator.validateStart(startParams(1, -1, new DateTime(NOW.toEpochMilli())));

        assertHardErrors(result, "StartTransaction.meterStart must not be negative");
    }

    @Test
    public void validateStart_futureTimestamp_returnsSoftError() {
        var result = validator.validateStart(startParams(1, 10, DateTime.parse("2026-02-17T12:05:01Z")));

        assertSoftErrors(result, "StartTransaction.timestamp is in the future");
    }

    @Test
    public void validateStart_multipleProblems_returnsAllErrorsBySeverity() {
        var result = validator.validateStart(startParams(0, -1, DateTime.parse("2026-02-17T12:05:01Z")));

        assertErrors(
            result,
            List.of("StartTransaction.timestamp is in the future"),
            List.of(
                "StartTransaction.connectorId must be positive",
                "StartTransaction.meterStart must not be negative"
            )
        );
    }

    @Test
    public void validateStart_futureTimestampAtBoundary_isAllowed() {
        var result = validator.validateStart(startParams(1, 10, DateTime.parse("2026-02-17T12:05:00Z")));

        assertValid(result);
    }

    @Test
    public void validateStatusNotification_connectorIdNegative_returnsHardError() {
        var result = validator.validateStatusNotification(statusParams(-1, DateTime.parse("2026-02-17T12:00:00Z")));

        assertHardErrors(result, "StatusNotification.connectorId must not be negative");
    }

    @Test
    public void validateStatusNotification_futureTimestamp_returnsSoftError() {
        var result = validator.validateStatusNotification(statusParams(1, DateTime.parse("2026-02-17T12:05:01Z")));

        assertSoftErrors(result, "StatusNotification.timestamp is in the future");
    }

    @Test
    public void validateStatusNotification_futureTimestampAtBoundary_isAllowed() {
        var result = validator.validateStatusNotification(statusParams(1, DateTime.parse("2026-02-17T12:05:00Z")));

        assertValid(result);
    }

    @Test
    public void validateStatusNotification_withoutTimestamp_isAllowed() {
        var result = validator.validateStatusNotification(new StatusNotificationRequest().withConnectorId(1));

        assertValid(result);
    }

    @Test
    public void validateSecurityEvent_futureTimestamp_returnsSoftError() {
        var params = new SecurityEventNotification()
            .withTimestamp(DateTime.parse("2026-02-17T12:05:01Z"));

        var result = validator.validateSecurityEvent(params);

        assertSoftErrors(result, "SecurityEventNotification.timestamp is in the future");
    }

    @Test
    public void validateSecurityEvent_futureTimestampAtBoundary_isAllowed() {
        var params = new SecurityEventNotification()
            .withTimestamp(DateTime.parse("2026-02-17T12:05:00Z"));

        var result = validator.validateSecurityEvent(params);

        assertValid(result);
    }

    @Test
    public void validateMeterValues_connectorIdNegative_returnsHardError() {
        var result = validator.validateMeterValues(meterValuesParams(-1, List.of(meterValue("2026-02-17T10:00:00Z"))));

        assertHardErrors(result, "MeterValues.connectorId must not be negative");
    }

    @Test
    public void validateMeterValues_futureTimestamp_returnsSoftError() {
        var result = validator.validateMeterValues(meterValuesParams(1, List.of(meterValue("2026-02-17T12:05:01Z"))));

        assertSoftErrors(result, "at least one MeterValue.timestamp is in the future");
    }

    @Test
    public void validateMeterValues_nullTimestamp_returnsHardError() {
        var result = validator.validateMeterValues(meterValuesParams(1, List.of(new MeterValue())));

        assertHardErrors(result, "MeterValue.timestamp is empty");
    }

    @Test
    public void validateMeterValues_valid_returnsValidResult() {
        var result = validator.validateMeterValues(meterValuesParams(1, List.of(meterValue("2026-02-17T12:05:00Z"))));

        assertValid(result);
    }

    @Test
    public void validateMeterValuesWithTransaction_transactionMissing_returnsHardError() {
        var result = validator.validateMeterValues(
            meterValuesParams(1, List.of(meterValue("2026-02-17T10:00:00Z"))),
            null
        );

        assertHardErrors(result, "The transaction is not found in database");
    }

    @Test
    public void validateMeterValuesWithTransaction_transactionAlreadyStoppedByStation_returnsHardError() {
        var tx = tx(
            "100",
            DateTime.parse("2026-02-17T09:00:00Z"),
            "150",
            DateTime.parse("2026-02-17T10:00:00Z"),
            TransactionStopEventActor.station
        );

        var result = validator.validateMeterValues(
            meterValuesParams(1, List.of(meterValue("2026-02-17T10:30:00Z"))),
            tx
        );

        assertHardErrors(result, "The transaction was already stopped by the station");
    }

    @Test
    public void validateMeterValuesWithTransaction_transactionAlreadyStoppedManually_isAllowed() {
        var tx = tx(
            "100",
            DateTime.parse("2026-02-17T09:00:00Z"),
            "150",
            DateTime.parse("2026-02-17T10:00:00Z"),
            TransactionStopEventActor.manual
        );

        var result = validator.validateMeterValues(
            meterValuesParams(1, List.of(meterValue("2026-02-17T10:30:00Z"))),
            tx
        );

        assertValid(result);
    }

    @Test
    public void validateMeterValuesWithTransaction_connectorIdNegative_returnsHardError() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);

        var result = validator.validateMeterValues(
            meterValuesParams(-1, List.of(meterValue("2026-02-17T10:00:00Z"))),
            tx
        );

        assertHardErrors(result, "MeterValues.connectorId must not be negative");
    }

    @Test
    public void validateMeterValuesWithTransaction_beforeStartTimestamp_returnsSoftError() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);

        var result = validator.validateMeterValues(
            meterValuesParams(1, List.of(meterValue("2026-02-17T08:54:59Z"))),
            tx
        );

        assertSoftErrors(result, "at least one MeterValue.timestamp is before start.timestamp");
    }

    @Test
    public void validateMeterValuesWithTransaction_valid_returnsValidResult() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);

        var result = validator.validateMeterValues(
            meterValuesParams(1, List.of(meterValue("2026-02-17T10:00:00Z"))),
            tx
        );

        assertValid(result);
    }

    @Test
    public void validateStop_transactionMissing_returnsHardError() {
        var params = stopParams(new DateTime(NOW.toEpochMilli()), "200");
        var result = validator.validateStop(null, params, DIFFERENT_PARENTS);

        assertHardErrors(result, "The transaction is not found in database");
    }

    @Test
    public void validateStop_transactionAlreadyStoppedByStation_returnsHardError() {
        var tx = tx(
            "100",
            DateTime.parse("2026-02-17T09:00:00Z"),
            "150",
            DateTime.parse("2026-02-17T10:00:00Z"),
            TransactionStopEventActor.station
        );
        var params = stopParams(DateTime.parse("2026-02-17T10:30:00Z"), "200");
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertHardErrors(result, "The transaction was already stopped by the station");
    }

    @Test
    public void validateStop_transactionAlreadyStoppedManually_isAllowed() {
        var tx = tx(
            "100",
            DateTime.parse("2026-02-17T09:00:00Z"),
            "150",
            DateTime.parse("2026-02-17T10:00:00Z"),
            TransactionStopEventActor.manual
        );
        var params = stopParams(DateTime.parse("2026-02-17T10:30:00Z"), "200");
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertValid(result);
    }

    @Test
    public void validateStop_startAfterStop_returnsHardError() {
        var tx = tx("100", DateTime.parse("2026-02-17T12:01:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T12:00:00Z"), "200");
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertHardErrors(result, "start.timestamp is after stop.timestamp");
    }

    @Test
    public void validateStop_futureStopTimestamp_returnsHardError() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T12:05:01Z"), "200");
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertHardErrors(result, "stop.timestamp is in the future");
    }

    @Test
    public void validateStop_futureStopTimestampAtBoundary_isAllowed() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T12:05:00Z"), "200");
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertValid(result);
    }

    @Test
    public void validateStop_stopMeterLowerThanStart_returnsHardError() {
        var tx = tx("300", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T10:00:00Z"), "200");
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertHardErrors(result, "meterStart is greater than meterStop");
    }

    @Test
    public void validateStop_validStationStop_returnsValidResult() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T10:00:00Z"), "200");
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertValid(result);
    }

    @Test
    public void validateStop_differentIdTagsWithoutMatchingParents_returnsHardError() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T10:00:00Z"), "200")
            .withIdTag("tag-2");

        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertHardErrors(
            result,
            "stop.idTag is neither the transaction's idTag nor related to it through parentIdTag"
        );
    }

    @Test
    public void validateStop_differentIdTagsWithMatchingParents_isAllowed() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T10:00:00Z"), "200")
            .withIdTag("tag-2");

        var result = validator.validateStop(tx, params, (first, second) -> true);

        assertValid(result);
    }

    @Test
    public void validateStop_withoutIdTag_isAllowed() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T10:00:00Z"), "200")
            .withIdTag(null);

        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertValid(result);
    }

    @Test
    public void validateStop_transactionDataFutureTimestamp_returnsSoftErrors() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T10:00:00Z"), "200")
            .withTransactionData(List.of(meterValue("2026-02-17T12:05:01Z")));
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertSoftErrors(
            result,
            "at least one MeterValue.timestamp is in the future",
            "at least one MeterValue.timestamp is after stop.timestamp"
        );
    }

    @Test
    public void validateStop_transactionDataAfterStopTimestamp_returnsSoftError() {
        // more than 5 minutes (operational delta) after stop
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T10:00:00Z"), "200")
            .withTransactionData(List.of(meterValue("2026-02-17T10:10:00Z")));
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertSoftErrors(result, "at least one MeterValue.timestamp is after stop.timestamp");
    }

    @Test
    public void validateStop_transactionDataBeforeStartTimestamp_returnsSoftError() {
        // more than 5 minutes (operational delta) before start
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T10:00:00Z"), "200")
            .withTransactionData(List.of(meterValue("2026-02-17T08:54:59Z")));
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertSoftErrors(result, "at least one MeterValue.timestamp is before start.timestamp");
    }

    @Test
    public void validateStop_transactionDataSlightlyBeforeStartTimestamp_isAllowed() {
        // within 5 minutes (operational delta) before start — allowed for clock drift
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T10:00:00Z"), "200")
            .withTransactionData(List.of(meterValue("2026-02-17T08:55:01Z")));
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertValid(result);
    }

    @Test
    public void validateStop_transactionDataAtStartTimestamp_isAllowed() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);
        var params = stopParams(DateTime.parse("2026-02-17T10:00:00Z"), "200")
            .withTransactionData(List.of(meterValue("2026-02-17T09:00:00Z")));
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertValid(result);
    }

    /**
     * this behavior is coming from a real station in the field: the latest of meterValues is 1 second
     * after stopTimestamp, because the station sampled stop timestamp first, and then later the timestamp
     * for this meterValue entry.
     */
    @Test
    public void validateStop_transactionDataOneSecondAfterStop_isAllowed() {
        var tx = tx("100", DateTime.parse("2026-02-17T09:00:00Z"), null, null, null);

        var params = stopParams(DateTime.parse("2026-02-17T10:00:00Z"), "200")
            .withTransactionData(List.of(meterValue("2026-02-17T10:00:01Z")));
        var result = validator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertValid(result);
    }

    /**
     * we cannot and should not check whether timestamps are in chronological order. there are real and valid
     * reasons for why they might not be: https://github.com/steve-community/steve/issues/1992
     */
    @Test
    public void validateStop_outOfOrderTransactionData_isAllowed() {
        var messageClock = Clock.fixed(Instant.parse("2026-03-25T08:10:00Z"), ZoneOffset.UTC);
        var localValidator = new CentralSystemService16_ServiceValidator(messageClock);

        var tx = tx("100", DateTime.parse("2026-03-25T06:22:44.000Z"), null, null, null)
            .setIdTag("XYZ");

        StopTransactionRequest params = new StopTransactionRequest()
            .withIdTag("XYZ")
            .withMeterStop(200)
            .withTimestamp(DateTime.parse("2026-03-25T07:58:42.000Z"))
            .withTransactionId(123)
            .withReason(Reason.OTHER)
            .withTransactionData(List.of(
                new MeterValue()
                    .withTimestamp(DateTime.parse("2026-03-25T06:22:44.000Z"))
                    .withSampledValue(sampledValue("101", ReadingContext.TRANSACTION_BEGIN, ValueFormat.RAW)),
                new MeterValue()
                    .withTimestamp(DateTime.parse("2026-03-25T07:58:42.000Z"))
                    .withSampledValue(sampledValue("202", ReadingContext.TRANSACTION_END, ValueFormat.RAW)),
                new MeterValue()
                    .withTimestamp(DateTime.parse("2026-03-25T06:22:45.000Z"))
                    .withSampledValue(sampledValue("102", ReadingContext.TRANSACTION_BEGIN, ValueFormat.SIGNED_DATA)),
                new MeterValue()
                    .withTimestamp(DateTime.parse("2026-03-25T07:58:43.000Z"))
                    .withSampledValue(sampledValue("203", ReadingContext.TRANSACTION_END, ValueFormat.SIGNED_DATA))
            ));

        var result = localValidator.validateStop(tx, params, DIFFERENT_PARENTS);

        assertValid(result);
    }

    /**
     * we cannot and should not check whether timestamps are in chronological order. there are real and valid
     * reasons for why they might not be: https://github.com/steve-community/steve/issues/1992
     */
    @Test
    public void validateMeterValues_timestampsOutOfOrder_returnsValidResult() {
        var result = validator.validateMeterValues(meterValuesParams(1, List.of(
            meterValue("2026-02-17T10:00:00Z"),
            meterValue("2026-02-17T09:00:00Z")
        )));

        assertValid(result);
    }

    @Test
    public void validateMeterValues_timestampsInOrder_returnsValidResult() {
        var result = validator.validateMeterValues(meterValuesParams(1, List.of(
            meterValue("2026-02-17T09:00:00Z"),
            meterValue("2026-02-17T09:30:00Z"),
            meterValue("2026-02-17T10:00:00Z")
        )));

        assertValid(result);
    }

    @Test
    public void validateMeterValues_sameTimestamps_returnsValidResult() {
        var result = validator.validateMeterValues(meterValuesParams(1, List.of(
            meterValue("2026-02-17T10:00:00Z"),
            meterValue("2026-02-17T10:00:00Z")
        )));

        assertValid(result);
    }

    @Test
    public void validateMeterValues_nullElementsInList_doesNotThrowNPE() {
        // null MeterValue elements should be filtered out, not cause NPE
        var result = validator.validateMeterValues(meterValuesParams(1, Arrays.asList(
            null,
            meterValue("2026-02-17T09:00:00Z"),
            null
        )));

        assertValid(result);
    }

    @Test
    public void validateMeterValues_allNullElements_returnsHardError() {
        // when all elements are null, all timestamps are filtered out → treated as empty
        var result = validator.validateMeterValues(meterValuesParams(1, Arrays.asList(
            null, null
        )));

        assertHardErrors(result, "MeterValue.timestamp is empty");
    }

    private static void assertValid(CentralSystemService16_ServiceValidator.ValidationResults result) {
        Assertions.assertAll(
            () -> Assertions.assertTrue(result.isValid()),
            () -> Assertions.assertEquals(List.of(), result.getSoftErrors()),
            () -> Assertions.assertEquals(List.of(), result.getHardErrors())
        );
    }

    private static void assertSoftErrors(
        CentralSystemService16_ServiceValidator.ValidationResults result,
        String... expectedErrors
    ) {
        assertErrors(result, List.of(expectedErrors), List.of());
    }

    private static void assertHardErrors(
        CentralSystemService16_ServiceValidator.ValidationResults result,
        String... expectedErrors
    ) {
        assertErrors(result, List.of(), List.of(expectedErrors));
    }

    private static void assertErrors(
        CentralSystemService16_ServiceValidator.ValidationResults result,
        List<String> expectedSoftErrors,
        List<String> expectedHardErrors
    ) {
        Assertions.assertAll(
            () -> Assertions.assertFalse(result.isValid()),
            () -> Assertions.assertEquals(expectedSoftErrors, result.getSoftErrors()),
            () -> Assertions.assertEquals(expectedHardErrors, result.getHardErrors()),
            () -> Assertions.assertEquals(!expectedHardErrors.isEmpty(), result.hasHardErrors())
        );
    }

    private static StopTransactionRequest stopParams(DateTime stopTimestamp, String meterStop) {
        return new StopTransactionRequest()
            .withIdTag("tag-1")
            .withTransactionId(1)
            .withTimestamp(stopTimestamp)
            .withMeterStop(Integer.valueOf(meterStop));
    }

    private static StartTransactionRequest startParams(int connectorId, int meterStart, DateTime timestamp) {
        return new StartTransactionRequest()
            .withConnectorId(connectorId)
            .withMeterStart(meterStart)
            .withTimestamp(timestamp)
            .withIdTag("tag-1");
    }

    private static StatusNotificationRequest statusParams(int connectorId, DateTime timestamp) {
        return new StatusNotificationRequest()
            .withConnectorId(connectorId)
            .withTimestamp(timestamp);
    }

    private static MeterValuesRequest meterValuesParams(int connectorId, List<MeterValue> values) {
        return new MeterValuesRequest()
            .withConnectorId(connectorId)
            .withMeterValue(values);
    }

    private static MeterValue meterValue(String timestamp) {
        return new MeterValue().withTimestamp(DateTime.parse(timestamp));
    }

    private static SampledValue sampledValue(String value, ReadingContext context, ValueFormat format) {
        return new SampledValue()
            .withValue(value)
            .withContext(context)
            .withFormat(format)
            .withMeasurand(Measurand.ENERGY_ACTIVE_IMPORT_REGISTER)
            .withUnit(UnitOfMeasure.WH);
    }

    private static TransactionRecord tx(String startValue, DateTime startTimestamp,
                                        String stopValue, DateTime stopTimestamp,
                                        TransactionStopEventActor stopActor) {
        return new TransactionRecord()
            .setIdTag("tag-1")
            .setStartValue(startValue)
            .setStartTimestamp(startTimestamp)
            .setStopValue(stopValue)
            .setStopTimestamp(stopTimestamp)
            .setStopEventActor(stopActor);
    }
}
