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
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.soap.MessageHeaderInterceptor;
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.service.CentralSystemService16_Service;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StopTransactionRequest;

import jakarta.xml.ws.WebServiceException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.byLessThan;

/**
 * @author Andreas Heuvels <andreas.heuvels@rwth-aachen.de>
 * @since 22.03.18
 */
@Slf4j
public class OperationalTestSoapOCPP16 {

    private static final String REGISTERED_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();
    private static final String REGISTERED_CHARGE_BOX_ID_2 = __DatabasePreparer__.getRegisteredChargeBoxId2();
    private static final String REGISTERED_OCPP_TAG = __DatabasePreparer__.getRegisteredOcppTag();
    private static final int numConnectors = 5;

    private static SteveConfiguration config;
    private static String path;
    private static Application app;

    @BeforeAll
    public static void initClass() throws Exception {
        config = SteveConfigurationReader.readSteveConfiguration("main.properties");
        assertThat(config.getProfile()).isEqualTo(ApplicationProfile.TEST);

        path = getPath(config);

        app = new Application(config);
        app.start();
    }

    @AfterAll
    public static void destroyClass() throws Exception {
        app.stop();
    }

    @BeforeEach
    public void init() {
        __DatabasePreparer__.prepare(config);
    }

    @AfterEach
    public void destroy() {
        __DatabasePreparer__.cleanUp();
    }

    @Test
    public void testUnregisteredCP() {
        assertThat(config.getOcpp().isAutoRegisterUnknownStations()).isFalse();

        var client = getForOcpp16(path);

        var boot = client.bootNotification(
                new BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                getRandomString());

        assertThat(boot).isNotNull();
        assertThat(boot.getStatus()).isNotEqualTo(RegistrationStatus.ACCEPTED);
    }

    /**
     * Reason: We started to check registration status by intercepting every SOAP message other than BootNotification
     * in {@link MessageHeaderInterceptor} and throw exception if station is not registered and auto-register is
     * disabled (and therefore early-exit the processing pipeline of the message).
     *
     * In case of BootNotification, the expected behaviour is to set RegistrationStatus.REJECTED in response, as done
     * by {@link CentralSystemService16_Service#bootNotification(BootNotificationRequest, String, OcppProtocol)}.
     * Therefore, no exception. This case is tested by {@link OperationalTestSoapOCPP16#testUnregisteredCP()} already.
     *
     * WS/JSON stations cannot connect at all if they are not registered, as ensured by {@link OcppWebSocketUpgrader}.
     */
    @Test
    public void testUnregisteredCPWithInterceptor() {
        assertThat(config.getOcpp().isAutoRegisterUnknownStations()).isFalse();
        var client = getForOcpp16(path);
        var request = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var chargeBoxIdentity = getRandomString();
        assertThatExceptionOfType(WebServiceException.class).isThrownBy(() -> {
            client.authorize(request, chargeBoxIdentity);
        });
    }

    @Test
    public void testRegisteredCP() {
        var client = getForOcpp16(path);

        initStationWithBootNotification(client);

        var details = __DatabasePreparer__.getCBDetails(REGISTERED_CHARGE_BOX_ID);
        assertThat(details.getChargeBox().getOcppProtocol()).contains("ocpp1.6");
    }

    @Test
    public void testRegisteredIdTag() {
        var client = getForOcpp16(path);

        var auth = client.authorize(
                new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                REGISTERED_CHARGE_BOX_ID);

        assertThat(auth).isNotNull();
        assertThat(auth.getIdTagInfo().getStatus()).isEqualTo(AuthorizationStatus.ACCEPTED);
    }

    @Test
    public void testUnregisteredIdTag() {
        var client = getForOcpp16(path);

        var auth = client.authorize(
                new AuthorizeRequest().withIdTag(getRandomString()),
                REGISTERED_CHARGE_BOX_ID);

        assertThat(auth).isNotNull();
        assertThat(auth.getIdTagInfo().getStatus()).isEqualTo(AuthorizationStatus.INVALID);
    }

    @Test
    public void testInTransactionStatusOfIdTag() {
        var client = getForOcpp16(path);

        var start = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(2)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(OffsetDateTime.now())
                        .withMeterStart(0),
                REGISTERED_CHARGE_BOX_ID
        );

        assertThat(start).isNotNull();
        assertThat(start.getTransactionId()).isGreaterThan(0);
        assertThat(__DatabasePreparer__.getOcppTagRecord(REGISTERED_OCPP_TAG).getInTransaction()).isTrue();

        var stop = client.stopTransaction(
                new StopTransactionRequest()
                        .withTransactionId(start.getTransactionId())
                        .withTimestamp(OffsetDateTime.now())
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withMeterStop(30),
                REGISTERED_CHARGE_BOX_ID
        );

        assertThat(stop).isNotNull();
        assertThat(__DatabasePreparer__.getOcppTagRecord(REGISTERED_OCPP_TAG).getInTransaction()).isFalse();
    }

    /**
     * https://github.com/steve-community/steve/issues/217
     * https://github.com/steve-community/steve/issues/219
     */
    @Test
    public void testAuthorizationStatus() {
        var client = getForOcpp16(path);

        {
            var auth1 = client.authorize(
                    new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                    REGISTERED_CHARGE_BOX_ID);
            assertThat(auth1.getIdTagInfo().getStatus()).isEqualTo(AuthorizationStatus.ACCEPTED);

            var start1 = client.startTransaction(
                    new StartTransactionRequest()
                            .withConnectorId(2)
                            .withIdTag(REGISTERED_OCPP_TAG)
                            .withTimestamp(OffsetDateTime.now())
                            .withMeterStart(0),
                    REGISTERED_CHARGE_BOX_ID);
            assertThat(start1.getTransactionId()).isGreaterThan(0);
            assertThat(start1.getIdTagInfo().getStatus()).isEqualTo(AuthorizationStatus.ACCEPTED);

            var auth1Retry = client.authorize(
                    new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                    REGISTERED_CHARGE_BOX_ID);
            assertThat(auth1Retry.getIdTagInfo().getStatus()).isEqualTo(AuthorizationStatus.ACCEPTED);
        }

        {
            var auth2 = client.authorize(
                    new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                    REGISTERED_CHARGE_BOX_ID_2);
            assertThat(auth2.getIdTagInfo().getStatus()).isEqualTo(AuthorizationStatus.ACCEPTED);

            var start2 = client.startTransaction(
                    new StartTransactionRequest()
                            .withConnectorId(2)
                            .withIdTag(REGISTERED_OCPP_TAG)
                            .withTimestamp(OffsetDateTime.now())
                            .withMeterStart(0),
                    REGISTERED_CHARGE_BOX_ID_2);
            assertThat(start2.getTransactionId()).isGreaterThan(0);
            assertThat(start2.getIdTagInfo().getStatus()).isEqualTo(AuthorizationStatus.CONCURRENT_TX);

            var auth2Retry = client.authorize(
                    new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                    REGISTERED_CHARGE_BOX_ID_2);
            assertThat(auth2Retry.getIdTagInfo().getStatus()).isEqualTo(AuthorizationStatus.ACCEPTED);
        }
    }

    @Test
    public void testStatusNotification() {
        var client = getForOcpp16(path);

        // -------------------------------------------------------------------------
        // init the station and verify db connector status values
        // -------------------------------------------------------------------------

        initStationWithBootNotification(client);

        // test all status enum values
        for (ChargePointStatus chargePointStatus : ChargePointStatus.values()) {
            // status for numConnectors connectors + connector 0 (main controller of CP)
            for (var i = 0; i <= numConnectors; i++) {
                var status = client.statusNotification(
                        new StatusNotificationRequest()
                                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                .withStatus(chargePointStatus)
                                .withConnectorId(i)
                                .withTimestamp(OffsetDateTime.now()),
                        REGISTERED_CHARGE_BOX_ID
                );
                assertThat(status).isNotNull();
            }

            var connectorStatusList = __DatabasePreparer__.getChargePointConnectorStatus();
            for (var connectorStatus : connectorStatusList) {
                assertThat(connectorStatus.getStatus()).isEqualTo(chargePointStatus.value());
                assertThat(connectorStatus.getErrorCode()).isEqualTo(ChargePointErrorCode.NO_ERROR.value());
            }
        }

        // -------------------------------------------------------------------------
        // send status for faulty connector and verify db values
        // -------------------------------------------------------------------------

        int faultyConnectorId = 1;

        var statusConnectorError = client.statusNotification(
                new StatusNotificationRequest()
                        .withErrorCode(ChargePointErrorCode.HIGH_TEMPERATURE)
                        .withStatus(ChargePointStatus.FAULTED)
                        .withConnectorId(faultyConnectorId)
                        .withTimestamp(OffsetDateTime.now()),
                REGISTERED_CHARGE_BOX_ID
        );
        assertThat(statusConnectorError).isNotNull();

        var connectorStatusList = __DatabasePreparer__.getChargePointConnectorStatus();
        for (var connectorStatus : connectorStatusList) {
            if (connectorStatus.getConnectorId() == faultyConnectorId) {
                assertThat(connectorStatus.getStatus()).isEqualTo(ChargePointStatus.FAULTED.value());
                assertThat(connectorStatus.getErrorCode()).isEqualTo(ChargePointErrorCode.HIGH_TEMPERATURE.value());
            } else {
                assertThat(connectorStatus.getStatus()).isNotEqualTo(ChargePointStatus.FAULTED.value());
                assertThat(connectorStatus.getErrorCode()).isNotEqualTo(ChargePointErrorCode.HIGH_TEMPERATURE.value());
            }
        }
    }

    @Test
    public void testReservation() {
        int usedConnectorID = 1;

        var client = getForOcpp16(path);

        // -------------------------------------------------------------------------
        // init the station and make reservation
        // -------------------------------------------------------------------------

        initStationWithBootNotification(client);
        initConnectorsWithStatusNotification(client);

        var reservationId = __DatabasePreparer__.makeReservation(usedConnectorID);

        // -------------------------------------------------------------------------
        // startTransaction (invalid reservationId)
        // -------------------------------------------------------------------------

        var nonExistingReservationId = reservationId + 17;

        var startInvalid = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(usedConnectorID)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(OffsetDateTime.now())
                        .withMeterStart(0)
                        .withReservationId(nonExistingReservationId),
                REGISTERED_CHARGE_BOX_ID
        );
        assertThat(startInvalid).isNotNull();

        // validate that the transaction is written to db, even though reservation was invalid
        var transactions = __DatabasePreparer__.getTransactions();
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getId()).isEqualTo(startInvalid.getTransactionId());

        // make sure that this invalid reservation had no side effects
        {
            var reservations = __DatabasePreparer__.getReservations();
            assertThat(reservations).hasSize(1);
            var res = reservations.get(0);
            assertThat(res.getId()).isEqualTo(reservationId);
            assertThat(res.getStatus()).isEqualTo(ReservationStatus.ACCEPTED.value());
        }

        // -------------------------------------------------------------------------
        // startTransaction (idtag and connectorid are not the ones from the reservation)
        // -------------------------------------------------------------------------

        var startWrongTag = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(3)
                        .withIdTag(getRandomString())
                        .withTimestamp(OffsetDateTime.now())
                        .withMeterStart(0)
                        .withReservationId(reservationId),
                REGISTERED_CHARGE_BOX_ID
        );
        assertThat(startWrongTag).isNotNull();

        {
            var reservations = __DatabasePreparer__.getReservations();
            assertThat(reservations).hasSize(1);
            var res = reservations.get(0);
            assertThat(res.getStatus()).isEqualTo(ReservationStatus.ACCEPTED.value());
            assertThat(res.getTransactionId()).isNull();
        }

        // -------------------------------------------------------------------------
        // startTransaction (valid)
        // -------------------------------------------------------------------------

        var startValidId = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(usedConnectorID)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(OffsetDateTime.now())
                        .withMeterStart(0)
                        .withReservationId(reservationId),
                REGISTERED_CHARGE_BOX_ID
        );
        assertThat(startValidId).isNotNull();
        var transactionIdValid = startValidId.getTransactionId();

        {
            var reservations = __DatabasePreparer__.getReservations();
            assertThat(reservations).hasSize(1);
            var res = reservations.get(0);
            assertThat(res.getStatus()).isEqualTo(ReservationStatus.USED.value());
            assertThat(res.getTransactionId()).isEqualTo(transactionIdValid);
        }

        // -------------------------------------------------------------------------
        // startTransaction (valid again)
        // -------------------------------------------------------------------------

        var startValidIdUsedTwice = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(usedConnectorID)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(OffsetDateTime.now())
                        .withMeterStart(0)
                        .withReservationId(reservationId),
                REGISTERED_CHARGE_BOX_ID
        );
        assertThat(startValidIdUsedTwice).isNotNull();

        {
            var reservations = __DatabasePreparer__.getReservations();
            assertThat(reservations).hasSize(1);
            var res = reservations.get(0);
            assertThat(res.getStatus()).isEqualTo(ReservationStatus.USED.value());
            assertThat(res.getTransactionId()).isEqualTo(transactionIdValid);
        }
    }

    @Test
    public void testWithMeterValuesAndTransactionData() {
        testBody(getMeterValues(), getTransactionData());
    }

    @Test
    public void testWithMeterValues() {
        testBody(getMeterValues(), null);
    }

    @Test
    public void testWithTransactionData() {
        testBody(null, getTransactionData());
    }

    @Test
    public void testWithoutMeterValuesAndTransactionData() {
        testBody(null, null);
    }

    private void testBody(List<MeterValue> meterValues, List<MeterValue> transactionData) {
        final var usedConnectorID = 1;

        var client = getForOcpp16(path);

        initStationWithBootNotification(client);
        initConnectorsWithStatusNotification(client);

        // heartbeat
        var heartbeat = client.heartbeat(
                new HeartbeatRequest(),
                REGISTERED_CHARGE_BOX_ID
        );
        assertThat(heartbeat).isNotNull();

        // Auth
        var auth = client.authorize(
                new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                REGISTERED_CHARGE_BOX_ID
        );
        // Simple request, not much done here
        assertThat(auth).isNotNull();
        assertThat(auth.getIdTagInfo().getStatus()).isEqualTo(AuthorizationStatus.ACCEPTED);

        // startTransaction
        var startTimeStamp = OffsetDateTime.now();
        var start = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(usedConnectorID)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(startTimeStamp)
                        .withMeterStart(0),
                REGISTERED_CHARGE_BOX_ID
        );
        assertThat(start).isNotNull();

        var transactionID = start.getTransactionId();

        var allTransactions = __DatabasePreparer__.getTransactionRecords();
        assertThat(allTransactions).hasSize(1);

        {
            var t = allTransactions.get(0);
            assertThat(t.getStartTimestamp()).isCloseTo(startTimeStamp.toLocalDateTime(), byLessThan(1, ChronoUnit.SECONDS));
            assertThat(t.getStartValue()).isEqualTo("0");

            assertThat(t.getStopTimestamp()).isNull();
            assertThat(t.getStopReason()).isNull();
            assertThat(t.getStopValue()).isNull();
        }

        // status
        var statusStart = client.statusNotification(
                new StatusNotificationRequest()
                        .withStatus(ChargePointStatus.CHARGING)
                        .withErrorCode(ChargePointErrorCode.NO_ERROR)
                        .withConnectorId(0)
                        .withTimestamp(OffsetDateTime.now()),
                REGISTERED_CHARGE_BOX_ID

        );
        assertThat(statusStart).isNotNull();

        // send meterValues
        if (meterValues != null) {
            var meter = client.meterValues(
                    new MeterValuesRequest()
                            .withConnectorId(usedConnectorID)
                            .withTransactionId(transactionID)
                            .withMeterValue(meterValues),
                    REGISTERED_CHARGE_BOX_ID
            );
            assertThat(meter).isNotNull();
            checkMeterValues(meterValues, transactionID);
        }

        // stopTransaction
        var stopTimeStamp = OffsetDateTime.now();
        var stopValue = 30;
        var stop = client.stopTransaction(
                new StopTransactionRequest()
                        .withTransactionId(transactionID)
                        .withTransactionData(transactionData)
                        .withTimestamp(stopTimeStamp)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withMeterStop(stopValue),
                REGISTERED_CHARGE_BOX_ID
        );

        {
            assertThat(stop).isNotNull();
            var transactionsStop = __DatabasePreparer__.getTransactionRecords();
            assertThat(transactionsStop).hasSize(1);
            var t = transactionsStop.get(0);
            assertThat(t.getStopTimestamp()).isCloseTo(stopTimeStamp.toLocalDateTime(), byLessThan(1, ChronoUnit.SECONDS));
            assertThat(t.getStopValue()).isEqualTo(Integer.toString(stopValue));

            if (transactionData != null) {
                checkMeterValues(transactionData, transactionID);
            }
        }

        // status
        var statusStop = client.statusNotification(
                new StatusNotificationRequest()
                        .withStatus(ChargePointStatus.AVAILABLE)
                        .withErrorCode(ChargePointErrorCode.NO_ERROR)
                        .withConnectorId(usedConnectorID)
                        .withTimestamp(OffsetDateTime.now()),
                REGISTERED_CHARGE_BOX_ID
        );
        assertThat(statusStop).isNotNull();
    }

    private void initStationWithBootNotification(CentralSystemService client) {
        var boot = client.bootNotification(
                new BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                REGISTERED_CHARGE_BOX_ID);
        assertThat(boot).isNotNull();
        assertThat(boot.getStatus()).isEqualTo(RegistrationStatus.ACCEPTED);
    }

    private void initConnectorsWithStatusNotification(CentralSystemService client) {
        for (var i = 0; i <= numConnectors; i++) {
            var statusBoot = client.statusNotification(
                    new StatusNotificationRequest()
                            .withErrorCode(ChargePointErrorCode.NO_ERROR)
                            .withStatus(ChargePointStatus.AVAILABLE)
                            .withConnectorId(i)
                            .withTimestamp(OffsetDateTime.now()),
                    REGISTERED_CHARGE_BOX_ID
            );
            assertThat(statusBoot).isNotNull();
        }
    }

    private void checkMeterValues(List<MeterValue> meterValues, int transactionPk) {
        var details = __DatabasePreparer__.getDetails(transactionPk);

        // iterate over all created meter values
        for (var meterValue : meterValues) {
            var sampledValues = meterValue.getSampledValue();
            assertThat(sampledValues).isEmpty();
            var thisValueFound = false;
            // and check, if it can be found in the DB
            for (var values : details.getValues()) {
                if (values.getValue().equals(sampledValues.get(0).getValue())) {
                    thisValueFound = true;
                    break;
                }
            }
            assertThat(thisValueFound).isTrue();
        }
    }

    private List<MeterValue> getTransactionData() {
        return Arrays.asList(
                createMeterValue("0.0"),
                createMeterValue("10.0"),
                createMeterValue("20.0"),
                createMeterValue("30.0")
        );
    }

    private List<MeterValue> getMeterValues() {
        return Arrays.asList(
                createMeterValue("3.0"),
                createMeterValue("13.0"),
                createMeterValue("23.0")
        );
    }

    private static MeterValue createMeterValue(String val) {
        return new MeterValue().withTimestamp(OffsetDateTime.now())
                               .withSampledValue(new SampledValue().withValue(val));
    }
}
