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
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.soap.MessageHeaderInterceptor;
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.repository.dto.Reservation;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import de.rwth.idsg.steve.service.CentralSystemService16_Service;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import jooq.steve.db.tables.records.TransactionRecord;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.HeartbeatResponse;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.MeterValuesResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StatusNotificationResponse;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.StopTransactionResponse;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.ws.WebServiceException;
import java.util.Arrays;
import java.util.List;

import static de.rwth.idsg.steve.utils.Helpers.getForOcpp16;
import static de.rwth.idsg.steve.utils.Helpers.getPath;
import static de.rwth.idsg.steve.utils.Helpers.getRandomString;

/**
 * @author Andreas Heuvels <andreas.heuvels@rwth-aachen.de>
 * @since 22.03.18
 */
@Slf4j
public class OperationalTestSoapOCPP16 {

    private static final String REGISTERED_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();
    private static final String REGISTERED_CHARGE_BOX_ID_2 = __DatabasePreparer__.getRegisteredChargeBoxId2();
    private static final String REGISTERED_OCPP_TAG = __DatabasePreparer__.getRegisteredOcppTag();
    private static final String path = getPath();
    private static final int numConnectors = 5;
    private static Application app;

    @BeforeAll
    public static void initClass() throws Exception {
        Assertions.assertEquals(ApplicationProfile.TEST, SteveConfiguration.CONFIG.getProfile());

        app = new Application();
        app.start();
    }

    @AfterAll
    public static void destroyClass() throws Exception {
        app.stop();
    }

    @BeforeEach
    public void init() throws Exception {
        __DatabasePreparer__.prepare();
    }

    @AfterEach
    public void destroy() throws Exception {
        __DatabasePreparer__.cleanUp();
    }

    @Test
    public void testUnregisteredCP() {
        Assertions.assertFalse(SteveConfiguration.CONFIG.getOcpp().isAutoRegisterUnknownStations());

        CentralSystemService client = getForOcpp16(path);

        BootNotificationResponse boot = client.bootNotification(
                new BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                getRandomString());

        Assertions.assertNotNull(boot);
        Assertions.assertNotEquals(RegistrationStatus.ACCEPTED, boot.getStatus());
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
        Assertions.assertThrows(WebServiceException.class, () -> {
            Assertions.assertFalse(SteveConfiguration.CONFIG.getOcpp().isAutoRegisterUnknownStations());

            CentralSystemService client = getForOcpp16(path);

            client.authorize(
                new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                getRandomString());
        });
    }

    @Test
    public void testRegisteredCP() {
        CentralSystemService client = getForOcpp16(path);

        initStationWithBootNotification(client);

        ChargePoint.Details details = __DatabasePreparer__.getCBDetails(REGISTERED_CHARGE_BOX_ID);
        Assertions.assertTrue(details.getChargeBox().getOcppProtocol().contains("ocpp1.6"));
    }

    @Test
    public void testRegisteredIdTag() {
        CentralSystemService client = getForOcpp16(path);

        AuthorizeResponse auth = client.authorize(
                new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                REGISTERED_CHARGE_BOX_ID);

        Assertions.assertNotNull(auth);
        Assertions.assertEquals(AuthorizationStatus.ACCEPTED, auth.getIdTagInfo().getStatus());
    }

    @Test
    public void testUnregisteredIdTag() {
        CentralSystemService client = getForOcpp16(path);

        AuthorizeResponse auth = client.authorize(
                new AuthorizeRequest().withIdTag(getRandomString()),
                REGISTERED_CHARGE_BOX_ID);

        Assertions.assertNotNull(auth);
        Assertions.assertEquals(AuthorizationStatus.INVALID, auth.getIdTagInfo().getStatus());
    }

    @Test
    public void testInTransactionStatusOfIdTag() {
        CentralSystemService client = getForOcpp16(path);

        StartTransactionResponse start = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(2)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(DateTime.now())
                        .withMeterStart(0),
                REGISTERED_CHARGE_BOX_ID
        );

        Assertions.assertNotNull(start);
        Assertions.assertTrue(start.getTransactionId() > 0);
        Assertions.assertTrue(__DatabasePreparer__.getOcppTagRecord(REGISTERED_OCPP_TAG).getInTransaction());

        StopTransactionResponse stop = client.stopTransaction(
                new StopTransactionRequest()
                        .withTransactionId(start.getTransactionId())
                        .withTimestamp(DateTime.now())
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withMeterStop(30),
                REGISTERED_CHARGE_BOX_ID
        );

        Assertions.assertNotNull(stop);
        Assertions.assertFalse(__DatabasePreparer__.getOcppTagRecord(REGISTERED_OCPP_TAG).getInTransaction());
    }

    /**
     * https://github.com/RWTH-i5-IDSG/steve/issues/217
     * https://github.com/RWTH-i5-IDSG/steve/issues/219
     */
    @Test
    public void testAuthorizationStatus() {
        CentralSystemService client = getForOcpp16(path);

        {
            AuthorizeResponse auth1 = client.authorize(
                    new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                    REGISTERED_CHARGE_BOX_ID);
            Assertions.assertEquals(AuthorizationStatus.ACCEPTED, auth1.getIdTagInfo().getStatus());

            StartTransactionResponse start1 = client.startTransaction(
                    new StartTransactionRequest()
                            .withConnectorId(2)
                            .withIdTag(REGISTERED_OCPP_TAG)
                            .withTimestamp(DateTime.now())
                            .withMeterStart(0),
                    REGISTERED_CHARGE_BOX_ID);
            Assertions.assertTrue(start1.getTransactionId() > 0);
            Assertions.assertEquals(AuthorizationStatus.ACCEPTED, start1.getIdTagInfo().getStatus());

            AuthorizeResponse auth1Retry = client.authorize(
                    new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                    REGISTERED_CHARGE_BOX_ID);
            Assertions.assertEquals(AuthorizationStatus.ACCEPTED, auth1Retry.getIdTagInfo().getStatus());
        }

        {
            AuthorizeResponse auth2 = client.authorize(
                    new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                    REGISTERED_CHARGE_BOX_ID_2);
            Assertions.assertEquals(AuthorizationStatus.ACCEPTED, auth2.getIdTagInfo().getStatus());

            StartTransactionResponse start2 = client.startTransaction(
                    new StartTransactionRequest()
                            .withConnectorId(2)
                            .withIdTag(REGISTERED_OCPP_TAG)
                            .withTimestamp(DateTime.now())
                            .withMeterStart(0),
                    REGISTERED_CHARGE_BOX_ID_2);
            Assertions.assertTrue(start2.getTransactionId() > 0);
            Assertions.assertEquals(AuthorizationStatus.CONCURRENT_TX, start2.getIdTagInfo().getStatus());

            AuthorizeResponse auth2Retry = client.authorize(
                    new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                    REGISTERED_CHARGE_BOX_ID_2);
            Assertions.assertEquals(AuthorizationStatus.ACCEPTED, auth2Retry.getIdTagInfo().getStatus());
        }
    }

    @Test
    public void testStatusNotification() {
        CentralSystemService client = getForOcpp16(path);

        // -------------------------------------------------------------------------
        // init the station and verify db connector status values
        // -------------------------------------------------------------------------

        initStationWithBootNotification(client);

        // test all status enum values
        for (ChargePointStatus chargePointStatus : ChargePointStatus.values()) {
            // status for numConnectors connectors + connector 0 (main controller of CP)
            for (int i = 0; i <= numConnectors; i++) {
                StatusNotificationResponse status = client.statusNotification(
                        new StatusNotificationRequest()
                                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                                .withStatus(chargePointStatus)
                                .withConnectorId(i)
                                .withTimestamp(DateTime.now()),
                        REGISTERED_CHARGE_BOX_ID
                );
                Assertions.assertNotNull(status);
            }

            List<ConnectorStatus> connectorStatusList = __DatabasePreparer__.getChargePointConnectorStatus();
            for (ConnectorStatus connectorStatus : connectorStatusList) {
                Assertions.assertEquals(chargePointStatus.value(), connectorStatus.getStatus());
                Assertions.assertEquals(ChargePointErrorCode.NO_ERROR.value(), connectorStatus.getErrorCode());
            }
        }

        // -------------------------------------------------------------------------
        // send status for faulty connector and verify db values
        // -------------------------------------------------------------------------

        int faultyConnectorId = 1;

        StatusNotificationResponse statusConnectorError = client.statusNotification(
                new StatusNotificationRequest()
                        .withErrorCode(ChargePointErrorCode.HIGH_TEMPERATURE)
                        .withStatus(ChargePointStatus.FAULTED)
                        .withConnectorId(faultyConnectorId)
                        .withTimestamp(DateTime.now()),
                REGISTERED_CHARGE_BOX_ID
        );
        Assertions.assertNotNull(statusConnectorError);


        List<ConnectorStatus> connectorStatusList = __DatabasePreparer__.getChargePointConnectorStatus();
        for (ConnectorStatus connectorStatus : connectorStatusList) {
            if (connectorStatus.getConnectorId() == faultyConnectorId) {
                Assertions.assertEquals(ChargePointStatus.FAULTED.value(), connectorStatus.getStatus());
                Assertions.assertEquals(ChargePointErrorCode.HIGH_TEMPERATURE.value(), connectorStatus.getErrorCode());
            } else {
                Assertions.assertNotEquals(ChargePointStatus.FAULTED.value(), connectorStatus.getStatus());
                Assertions.assertNotEquals(ChargePointErrorCode.HIGH_TEMPERATURE.value(), connectorStatus.getErrorCode());
            }
        }
    }

    @Test
    public void testReservation() {
        int usedConnectorID = 1;

        CentralSystemService client = getForOcpp16(path);

        // -------------------------------------------------------------------------
        // init the station and make reservation
        // -------------------------------------------------------------------------

        initStationWithBootNotification(client);
        initConnectorsWithStatusNotification(client);

        int reservationId = __DatabasePreparer__.makeReservation(usedConnectorID);

        // -------------------------------------------------------------------------
        // startTransaction (invalid reservationId)
        // -------------------------------------------------------------------------

        int nonExistingReservationId = reservationId + 17;

        StartTransactionResponse startInvalid = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(usedConnectorID)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(DateTime.now())
                        .withMeterStart(0)
                        .withReservationId(nonExistingReservationId),
                REGISTERED_CHARGE_BOX_ID
        );
        Assertions.assertNotNull(startInvalid);

        // validate that the transaction is written to db, even though reservation was invalid
        List<Transaction> transactions = __DatabasePreparer__.getTransactions();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(startInvalid.getTransactionId(), transactions.get(0).getId());

        // make sure that this invalid reservation had no side effects
        {
            List<Reservation> reservations = __DatabasePreparer__.getReservations();
            Assertions.assertEquals(1, reservations.size());
            Reservation res = reservations.get(0);
            Assertions.assertEquals(reservationId, res.getId());
            Assertions.assertEquals(ReservationStatus.ACCEPTED.value(), res.getStatus());
        }

        // -------------------------------------------------------------------------
        // startTransaction (idtag and connectorid are not the ones from the reservation)
        // -------------------------------------------------------------------------

        StartTransactionResponse startWrongTag = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(3)
                        .withIdTag(getRandomString())
                        .withTimestamp(DateTime.now())
                        .withMeterStart(0)
                        .withReservationId(reservationId),
                REGISTERED_CHARGE_BOX_ID
        );
        Assertions.assertNotNull(startWrongTag);

        {
            List<Reservation> reservations = __DatabasePreparer__.getReservations();
            Assertions.assertEquals(1, reservations.size());
            Reservation res = reservations.get(0);
            Assertions.assertEquals(ReservationStatus.ACCEPTED.value(), res.getStatus());
            Assertions.assertNull(res.getTransactionId());
        }

        // -------------------------------------------------------------------------
        // startTransaction (valid)
        // -------------------------------------------------------------------------

        StartTransactionResponse startValidId = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(usedConnectorID)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(DateTime.now())
                        .withMeterStart(0)
                        .withReservationId(reservationId),
                REGISTERED_CHARGE_BOX_ID
        );
        Assertions.assertNotNull(startValidId);
        Integer transactionIdValid = startValidId.getTransactionId();

        {
            List<Reservation> reservations = __DatabasePreparer__.getReservations();
            Assertions.assertEquals(reservations.size(), 1);
            Reservation res = reservations.get(0);
            Assertions.assertEquals(ReservationStatus.USED.value(), res.getStatus());
            Assertions.assertEquals(transactionIdValid, res.getTransactionId());
        }

        // -------------------------------------------------------------------------
        // startTransaction (valid again)
        // -------------------------------------------------------------------------

        StartTransactionResponse startValidIdUsedTwice = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(usedConnectorID)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(DateTime.now())
                        .withMeterStart(0)
                        .withReservationId(reservationId),
                REGISTERED_CHARGE_BOX_ID
        );
        Assertions.assertNotNull(startValidIdUsedTwice);

        {
            List<Reservation> reservations = __DatabasePreparer__.getReservations();
            Assertions.assertEquals(reservations.size(), 1);
            Reservation res = reservations.get(0);
            Assertions.assertEquals(ReservationStatus.USED.value(), res.getStatus());
            Assertions.assertEquals(transactionIdValid, res.getTransactionId());
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
        final int usedConnectorID = 1;

        CentralSystemService client = getForOcpp16(path);

        initStationWithBootNotification(client);
        initConnectorsWithStatusNotification(client);

        // heartbeat
        HeartbeatResponse heartbeat = client.heartbeat(
                new HeartbeatRequest(),
                REGISTERED_CHARGE_BOX_ID
        );
        Assertions.assertNotNull(heartbeat);

        // Auth
        AuthorizeResponse auth = client.authorize(
                new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG),
                REGISTERED_CHARGE_BOX_ID
        );
        // Simple request, not much done here
        Assertions.assertNotNull(auth);
        Assertions.assertEquals(AuthorizationStatus.ACCEPTED, auth.getIdTagInfo().getStatus());


        // startTransaction
        DateTime startTimeStamp = DateTime.now();
        StartTransactionResponse start = client.startTransaction(
                new StartTransactionRequest()
                        .withConnectorId(usedConnectorID)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withTimestamp(startTimeStamp)
                        .withMeterStart(0),
                REGISTERED_CHARGE_BOX_ID
        );
        Assertions.assertNotNull(start);

        int transactionID = start.getTransactionId();

        List<TransactionRecord> allTransactions = __DatabasePreparer__.getTransactionRecords();
        Assertions.assertEquals(1, allTransactions.size());

        {
            TransactionRecord t = allTransactions.get(0);
            Assertions.assertEquals(startTimeStamp, t.getStartTimestamp());
            Assertions.assertEquals(0, Integer.parseInt(t.getStartValue()));

            Assertions.assertNull(t.getStopTimestamp());
            Assertions.assertNull(t.getStopReason());
            Assertions.assertNull(t.getStopValue());
        }

        // status
        StatusNotificationResponse statusStart = client.statusNotification(
                new StatusNotificationRequest()
                        .withStatus(ChargePointStatus.CHARGING)
                        .withErrorCode(ChargePointErrorCode.NO_ERROR)
                        .withConnectorId(0)
                        .withTimestamp(DateTime.now()),
                REGISTERED_CHARGE_BOX_ID

        );
        Assertions.assertNotNull(statusStart);

        // send meterValues
        if (meterValues != null) {
            MeterValuesResponse meter = client.meterValues(
                    new MeterValuesRequest()
                            .withConnectorId(usedConnectorID)
                            .withTransactionId(transactionID)
                            .withMeterValue(meterValues),
                    REGISTERED_CHARGE_BOX_ID
            );
            Assertions.assertNotNull(meter);
            checkMeterValues(meterValues, transactionID);
        }

        // stopTransaction
        DateTime stopTimeStamp = DateTime.now();
        int stopValue = 30;
        StopTransactionResponse stop = client.stopTransaction(
                new StopTransactionRequest()
                        .withTransactionId(transactionID)
                        .withTransactionData(transactionData)
                        .withTimestamp(stopTimeStamp)
                        .withIdTag(REGISTERED_OCPP_TAG)
                        .withMeterStop(stopValue),
                REGISTERED_CHARGE_BOX_ID
        );

        {
            Assertions.assertNotNull(stop);
            List<TransactionRecord> transactionsStop = __DatabasePreparer__.getTransactionRecords();
            Assertions.assertEquals(1, transactionsStop.size());
            TransactionRecord t = transactionsStop.get(0);
            Assertions.assertEquals(stopTimeStamp, t.getStopTimestamp());
            Assertions.assertEquals(stopValue, Integer.parseInt(t.getStopValue()));

            if (transactionData != null) {
                checkMeterValues(transactionData, transactionID);
            }
        }

        // status
        StatusNotificationResponse statusStop = client.statusNotification(
                new StatusNotificationRequest()
                        .withStatus(ChargePointStatus.AVAILABLE)
                        .withErrorCode(ChargePointErrorCode.NO_ERROR)
                        .withConnectorId(usedConnectorID)
                        .withTimestamp(DateTime.now()),
                REGISTERED_CHARGE_BOX_ID
        );
        Assertions.assertNotNull(statusStop);
    }

    private void initStationWithBootNotification(CentralSystemService client) {
        BootNotificationResponse boot = client.bootNotification(
                new BootNotificationRequest()
                        .withChargePointVendor(getRandomString())
                        .withChargePointModel(getRandomString()),
                REGISTERED_CHARGE_BOX_ID);
        Assertions.assertNotNull(boot);
        Assertions.assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());
    }

    private void initConnectorsWithStatusNotification(CentralSystemService client) {
        for (int i = 0; i <= numConnectors; i++) {
            StatusNotificationResponse statusBoot = client.statusNotification(
                    new StatusNotificationRequest()
                            .withErrorCode(ChargePointErrorCode.NO_ERROR)
                            .withStatus(ChargePointStatus.AVAILABLE)
                            .withConnectorId(i)
                            .withTimestamp(DateTime.now()),
                    REGISTERED_CHARGE_BOX_ID
            );
            Assertions.assertNotNull(statusBoot);
        }
    }

    private void checkMeterValues(List<MeterValue> meterValues, int transactionPk) {
        TransactionDetails details = __DatabasePreparer__.getDetails(transactionPk);

        // iterate over all created meter values
        for (MeterValue meterValue : meterValues) {
            List<SampledValue> sampledValues = meterValue.getSampledValue();
            Assertions.assertFalse(sampledValues.isEmpty());
            boolean thisValueFound = false;
            // and check, if it can be found in the DB
            for (TransactionDetails.MeterValues values : details.getValues()) {
                if (values.getValue().equals(sampledValues.get(0).getValue())) {
                    thisValueFound = true;
                    break;
                }
            }
            Assertions.assertTrue(thisValueFound);
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
        return new MeterValue().withTimestamp(DateTime.now())
                               .withSampledValue(new SampledValue().withValue(val));
    }
}
