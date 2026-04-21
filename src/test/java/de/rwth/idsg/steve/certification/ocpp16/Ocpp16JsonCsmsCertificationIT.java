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
package de.rwth.idsg.steve.certification.ocpp16;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.UpdateFirmwareTask;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.service.OcppOperationsService;
import de.rwth.idsg.steve.utils.OcppJsonChargePoint;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import de.rwth.idsg.steve.utils.mapper.ChargingProfileDetailsMapper;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileFilterType;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.DeleteCertificateParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetInstalledCertificateIdsParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetLogParams;
import de.rwth.idsg.steve.web.dto.ocpp.InstallCertificateParams;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ReserveNowParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.SignedUpdateFirmwareParams;
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageEnum;
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;
import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.ocpp.UpdateFirmwareParams;
import lombok.extern.slf4j.Slf4j;
import ocpp._2022._02.security.InstallCertificate;
import ocpp.cp._2015._10.ChangeConfigurationRequest;
import ocpp.cp._2015._10.ChangeConfigurationResponse;
import ocpp.cp._2015._10.ClearCacheRequest;
import ocpp.cp._2015._10.ClearCacheResponse;
import ocpp.cp._2015._10.ClearCacheStatus;
import ocpp.cp._2015._10.ConfigurationStatus;
import ocpp.cp._2015._10.GetConfigurationRequest;
import ocpp.cp._2015._10.GetConfigurationResponse;
import ocpp.cp._2015._10.KeyValue;
import ocpp.cp._2015._10.RemoteStartStopStatus;
import ocpp.cp._2015._10.RemoteStartTransactionRequest;
import ocpp.cp._2015._10.RemoteStartTransactionResponse;
import ocpp.cp._2015._10.RemoteStopTransactionRequest;
import ocpp.cp._2015._10.RemoteStopTransactionResponse;
import ocpp.cp._2015._10.ResetRequest;
import ocpp.cp._2015._10.ResetResponse;
import ocpp.cp._2015._10.ResetStatus;
import ocpp.cp._2015._10.ResetType;
import ocpp.cp._2015._10.UnlockConnectorRequest;
import ocpp.cp._2015._10.UnlockConnectorResponse;
import ocpp.cp._2015._10.UnlockStatus;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.HeartbeatResponse;
import ocpp.cs._2015._10.Reason;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StatusNotificationResponse;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.StopTransactionResponse;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.AuthorizationKey;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.CpoName;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.SecurityProfile;
import static jooq.steve.db.Tables.CHARGE_BOX_CERTIFICATE_INSTALLED;
import static jooq.steve.db.Tables.CHARGE_BOX_FIRMWARE_UPDATE_EVENT;
import static jooq.steve.db.Tables.CHARGE_BOX_FIRMWARE_UPDATE_JOB;
import static jooq.steve.db.Tables.CHARGE_BOX_LOG_UPLOAD_EVENT;
import static jooq.steve.db.Tables.CHARGE_BOX_LOG_UPLOAD_JOB;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * These are the integration tests for all OCPP 1.6 (SUT) CSMS test cases relevant for the OCA certification
 * (version: 2026-02).
 *
 * https://openchargealliance.org/wp-content/uploads/2026/03/CompliancyTestTool-TestCaseDocument.pdf
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 20.04.2026
 */
@Slf4j
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class Ocpp16JsonCsmsCertificationIT {

    private static final String PATH = "ws://localhost:8080/steve/websocket/CentralSystemService/";

    private static final String REGISTERED_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();
    private static final String REGISTERED_OCPP_TAG = __DatabasePreparer__.getRegisteredOcppTag();

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private OcppOperationsService operationsService;
    @Autowired
    private ChargingProfileRepository chargingProfileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private __DatabasePreparer__ databasePreparer;

    @BeforeEach
    public void setup(TestInfo testInfo) {
        log.info("----- START: {} -----", testInfo.getDisplayName());

        dslContext.settings().setExecuteLogging(false);

        databasePreparer = new __DatabasePreparer__(dslContext);
        databasePreparer.prepare();
    }

    @AfterEach
    public void teardown() {
        databasePreparer.cleanUp();
    }

    @Test
    public void test_TC_001_CSMS_ColdBootChargePoint() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var boot = new BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());
        var bootResponse = chargePoint.send(boot, BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus());
        assertNotNull(bootResponse.getCurrentTime());
        assertTrue(bootResponse.getInterval() > 0);

        for (int i = 0; i < 3; i++) {
            var status = new StatusNotificationRequest()
                .withConnectorId(i)
                .withStatus(ChargePointStatus.AVAILABLE)
                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                .withTimestamp(DateTime.now());
            var statusResponse = chargePoint.send(status, StatusNotificationResponse.class);
            assertNotNull(statusResponse);
        }

        var heartbeatResponse = chargePoint.send(new HeartbeatRequest(), HeartbeatResponse.class);
        assertNotNull(heartbeatResponse);
        assertNotNull(heartbeatResponse.getCurrentTime());

        chargePoint.close();
    }

    @Test
    public void test_TC_003_CSMS_RegularChargingSession_PluginFirst() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        var preparingResponse = chargePoint.send(preparing, StatusNotificationResponse.class);
        assertNotNull(preparingResponse);

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        var chargingResponse = chargePoint.send(charging, StatusNotificationResponse.class);
        assertNotNull(chargingResponse);

        chargePoint.close();
    }

    @Test
    public void test_TC_004_1_CSMS_RegularChargingSession_IdentificationFirst() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        var chargingResponse = chargePoint.send(charging, StatusNotificationResponse.class);
        assertNotNull(chargingResponse);

        chargePoint.close();
    }

    @Test
    public void test_TC_004_2_CSMS_RegularChargingSession_IdentificationFirst_ConnectionTimeout() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        var preparingResponse = chargePoint.send(preparing, StatusNotificationResponse.class);
        assertNotNull(preparingResponse);

        var available = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.AVAILABLE)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now().plusSeconds(5));
        var availableResponse = chargePoint.send(available, StatusNotificationResponse.class);
        assertNotNull(availableResponse);

        chargePoint.close();
    }

    @Test
    public void test_TC_005_1_CSMS_EVSideDisconnected_StopTransactionAndUnlock() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertTrue(startTransactionResponse.getTransactionId() > 0);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        var suspendedEv = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.SUSPENDED_EV)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(suspendedEv, StatusNotificationResponse.class));

        var stopTransaction = new StopTransactionRequest()
            .withTransactionId(startTransactionResponse.getTransactionId())
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStop(10)
            .withReason(Reason.EV_DISCONNECTED)
            .withTimestamp(DateTime.now());
        var stopTransactionResponse = chargePoint.send(stopTransaction, StopTransactionResponse.class);
        assertNotNull(stopTransactionResponse);
        assertNotNull(stopTransactionResponse.getIdTagInfo());
        assertEquals(AuthorizationStatus.ACCEPTED, stopTransactionResponse.getIdTagInfo().getStatus());

        var finishing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.FINISHING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(finishing, StatusNotificationResponse.class));

        var available = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.AVAILABLE)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(available, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_007_CSMS_RegularStartChargingSession_CachedId() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertNotNull(startTransactionResponse.getIdTagInfo());
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_061_CSMS_ClearAuthorizationDataInAuthorizationCache() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new MultipleChargePointSelect();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));

        CompletableFuture<ocpp.cp._2015._10.ClearCacheStatus> operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.clearCache(params);
                assertNotNull(callback);
                assertFalse(callback.getSuccessResponsesByChargeBoxId().isEmpty());
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var clearCacheRequest = new ClearCacheRequest();
        var clearCacheResponse = new ClearCacheResponse().withStatus(ClearCacheStatus.ACCEPTED);
        chargePoint.expectRequest(clearCacheRequest, clearCacheResponse);

        assertEquals(ClearCacheStatus.ACCEPTED, operationFuture.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_010_CSMS_RemoteStartChargingSession_CablePluggedInFirst() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var params = new RemoteStartTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.remoteStartTransaction(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var remoteStartReq = new RemoteStartTransactionRequest().withConnectorId(1).withIdTag(REGISTERED_OCPP_TAG);
        var remoteStartRes = new RemoteStartTransactionResponse().withStatus(RemoteStartStopStatus.ACCEPTED);
        chargePoint.expectRequest(remoteStartReq, remoteStartRes);
        assertEquals(RemoteStartStopStatus.ACCEPTED, operationFuture.join());

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_011_1_CSMS_RemoteStartChargingSession_RemoteStartFirst() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new RemoteStartTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.remoteStartTransaction(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var remoteStartReq = new RemoteStartTransactionRequest().withConnectorId(1).withIdTag(REGISTERED_OCPP_TAG);
        var remoteStartRes = new RemoteStartTransactionResponse().withStatus(RemoteStartStopStatus.ACCEPTED);
        chargePoint.expectRequest(remoteStartReq, remoteStartRes);
        assertEquals(RemoteStartStopStatus.ACCEPTED, operationFuture.join());

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_011_2_CSMS_RemoteStartChargingSession_Timeout() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new RemoteStartTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.remoteStartTransaction(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var remoteStartReq = new RemoteStartTransactionRequest().withConnectorId(1).withIdTag(REGISTERED_OCPP_TAG);
        var remoteStartRes = new RemoteStartTransactionResponse().withStatus(RemoteStartStopStatus.ACCEPTED);
        chargePoint.expectRequest(remoteStartReq, remoteStartRes);
        assertEquals(RemoteStartStopStatus.ACCEPTED, operationFuture.join());

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var available = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.AVAILABLE)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now().plusSeconds(5));
        assertNotNull(chargePoint.send(available, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_012_CSMS_RemoteStopChargingSession() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        var params = new RemoteStopTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setTransactionId(startTransactionResponse.getTransactionId());

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.remoteStopTransaction(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var remoteStopReq = new RemoteStopTransactionRequest().withTransactionId(startTransactionResponse.getTransactionId());
        var remoteStopRes = new RemoteStopTransactionResponse().withStatus(RemoteStartStopStatus.ACCEPTED);
        chargePoint.expectRequest(remoteStopReq, remoteStopRes);
        assertEquals(RemoteStartStopStatus.ACCEPTED, operationFuture.join());

        var stopTransaction = new StopTransactionRequest()
            .withTransactionId(startTransactionResponse.getTransactionId())
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStop(10)
            .withReason(Reason.REMOTE)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(stopTransaction, StopTransactionResponse.class));

        var finishing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.FINISHING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(finishing, StatusNotificationResponse.class));

        var available = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.AVAILABLE)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(available, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_013_CSMS_HardReset() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new ResetParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setResetType(ResetType.HARD);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reset(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var resetReq = new ResetRequest().withType(ResetType.HARD);
        var resetRes = new ResetResponse().withStatus(ResetStatus.ACCEPTED);
        chargePoint.expectRequest(resetReq, resetRes);
        assertEquals(ResetStatus.ACCEPTED, operationFuture.join());

        var boot = new BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());
        var bootResponse = chargePoint.send(boot, BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus());

        for (var connectorId : List.of(0, 1, 2)) {
            var status = new StatusNotificationRequest()
                .withConnectorId(connectorId)
                .withStatus(ChargePointStatus.AVAILABLE)
                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                .withTimestamp(DateTime.now());
            assertNotNull(chargePoint.send(status, StatusNotificationResponse.class));
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_014_CSMS_SoftReset() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new ResetParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setResetType(ResetType.SOFT);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reset(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var resetReq = new ResetRequest().withType(ResetType.SOFT);
        var resetRes = new ResetResponse().withStatus(ResetStatus.ACCEPTED);
        chargePoint.expectRequest(resetReq, resetRes);
        assertEquals(ResetStatus.ACCEPTED, operationFuture.join());

        var boot = new BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());
        var bootResponse = chargePoint.send(boot, BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus());

        for (var connectorId : List.of(0, 1, 2)) {
            var status = new StatusNotificationRequest()
                .withConnectorId(connectorId)
                .withStatus(ChargePointStatus.AVAILABLE)
                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                .withTimestamp(DateTime.now());
            assertNotNull(chargePoint.send(status, StatusNotificationResponse.class));
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_017_1_CSMS_UnlockConnector_NoTransaction_NotFixedCable() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new UnlockConnectorParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.unlockConnector(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var unlockReq = new UnlockConnectorRequest().withConnectorId(1);
        var unlockRes = new UnlockConnectorResponse().withStatus(UnlockStatus.UNLOCKED);
        chargePoint.expectRequest(unlockReq, unlockRes);
        assertEquals(UnlockStatus.UNLOCKED, operationFuture.join());

        chargePoint.close();
    }

    @Test
    public void test_TC_017_2_CSMS_UnlockConnector_NoTransaction_FixedCable() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new UnlockConnectorParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.unlockConnector(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var unlockReq = new UnlockConnectorRequest().withConnectorId(1);
        var unlockRes = new UnlockConnectorResponse().withStatus(UnlockStatus.NOT_SUPPORTED);
        chargePoint.expectRequest(unlockReq, unlockRes);
        assertEquals(UnlockStatus.NOT_SUPPORTED, operationFuture.join());

        chargePoint.close();
    }

    @Test
    public void test_TC_018_1_CSMS_UnlockConnector_WithChargingSession() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertTrue(startTransactionResponse.getTransactionId() > 0);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        var params = new UnlockConnectorParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.unlockConnector(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var unlockReq = new UnlockConnectorRequest().withConnectorId(1);
        var unlockRes = new UnlockConnectorResponse().withStatus(UnlockStatus.UNLOCKED);
        chargePoint.expectRequest(unlockReq, unlockRes);
        assertEquals(UnlockStatus.UNLOCKED, operationFuture.join());

        var finishing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.FINISHING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(finishing, StatusNotificationResponse.class));

        var stopTransaction = new StopTransactionRequest()
            .withTransactionId(startTransactionResponse.getTransactionId())
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStop(10)
            .withReason(Reason.UNLOCK_COMMAND)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(stopTransaction, StopTransactionResponse.class));

        var available = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.AVAILABLE)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(available, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_019_1_CSMS_RetrieveAllConfigurationKeys() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new GetConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConfKeyList(List.of());

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.getConfiguration(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var getConfigReq = new GetConfigurationRequest().withKey(List.of());
        var getConfigRes = new GetConfigurationResponse()
            .withConfigurationKey(
                new KeyValue().withKey("SupportedFeatureProfiles").withReadonly(true).withValue("Core,FirmwareManagement,Reservation,RemoteTrigger,SmartCharging"),
                new KeyValue().withKey("MeterValueSampleInterval").withReadonly(false).withValue("60")
            )
            .withUnknownKey(List.of());
        chargePoint.expectRequest(getConfigReq, getConfigRes);

        var values = operationFuture.join();
        assertNotNull(values);
        assertTrue(values.getUnknownKeys().isEmpty());
        assertFalse(values.getConfigurationKeys().isEmpty());

        chargePoint.close();
    }

    @Test
    public void test_TC_019_2_CSMS_RetrieveSpecificConfigurationKey() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new GetConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConfKeyList(List.of("SupportedFeatureProfiles"));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.getConfiguration(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var getConfigReq = new GetConfigurationRequest().withKey(List.of("SupportedFeatureProfiles"));
        var getConfigRes = new GetConfigurationResponse()
            .withConfigurationKey(
                new KeyValue().withKey("SupportedFeatureProfiles").withReadonly(true).withValue("Core,FirmwareManagement,Reservation,RemoteTrigger,SmartCharging")
            )
            .withUnknownKey(List.of());
        chargePoint.expectRequest(getConfigReq, getConfigRes);

        var values = operationFuture.join();
        assertNotNull(values);
        assertTrue(values.getUnknownKeys().isEmpty());
        assertEquals(1, values.getConfigurationKeys().size());
        assertEquals("SupportedFeatureProfiles", values.getConfigurationKeys().getFirst().getKey());

        chargePoint.close();
    }

    @Test
    public void test_TC_021_CSMS_ChangeSetConfiguration() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new ChangeConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConfKey("MeterValueSampleInterval");
        params.setValue("60");

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.changeConfiguration(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var changeConfigReq = new ChangeConfigurationRequest()
            .withKey("MeterValueSampleInterval")
            .withValue("60");
        var changeConfigRes = new ChangeConfigurationResponse().withStatus(ConfigurationStatus.ACCEPTED);
        chargePoint.expectRequest(changeConfigReq, changeConfigRes);
        assertEquals(ConfigurationStatus.ACCEPTED, operationFuture.join());

        chargePoint.close();
    }

    @Test
    public void test_TC_023_1_CSMS_StartChargingSession_AuthorizeInvalid() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var authorize = new AuthorizeRequest().withIdTag("INVALID_TAG");
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.INVALID, authorizeResponse.getIdTagInfo().getStatus());

        chargePoint.close();
    }

    @Test
    public void test_TC_023_2_CSMS_StartChargingSession_AuthorizeExpired() {
        var expiredTag = getRandomString();

        dslContext.insertInto(OCPP_TAG)
            .set(OCPP_TAG.ID_TAG, expiredTag)
            .set(OCPP_TAG.EXPIRY_DATE, DateTime.now().minusDays(1))
            .set(OCPP_TAG.NOTE, "integration test expired idTag")
            .execute();

        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var authorize = new AuthorizeRequest().withIdTag(expiredTag);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.EXPIRED, authorizeResponse.getIdTagInfo().getStatus());

        chargePoint.close();
    }

    @Test
    public void test_TC_023_3_CSMS_StartChargingSession_AuthorizeBlocked() {
        var blockedTag = getRandomString();

        dslContext.insertInto(OCPP_TAG)
            .set(OCPP_TAG.ID_TAG, blockedTag)
            .set(OCPP_TAG.MAX_ACTIVE_TRANSACTION_COUNT, 0)
            .set(OCPP_TAG.NOTE, "integration test blocked idTag")
            .execute();

        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var authorize = new AuthorizeRequest().withIdTag(blockedTag);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.BLOCKED, authorizeResponse.getIdTagInfo().getStatus());

        chargePoint.close();
    }

    @Test
    public void test_TC_024_CSMS_StartChargingSession_LockFailure() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var faulted = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.FAULTED)
            .withErrorCode(ChargePointErrorCode.CONNECTOR_LOCK_FAILURE)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(faulted, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_026_CSMS_RemoteStartChargingSession_Rejected() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new RemoteStartTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.remoteStartTransaction(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var remoteStartReq = new RemoteStartTransactionRequest().withConnectorId(1).withIdTag(REGISTERED_OCPP_TAG);
        var remoteStartRes = new RemoteStartTransactionResponse().withStatus(RemoteStartStopStatus.REJECTED);
        chargePoint.expectRequest(remoteStartReq, remoteStartRes);
        assertEquals(RemoteStartStopStatus.REJECTED, operationFuture.join());

        chargePoint.close();
    }

    @Test
    public void test_TC_028_CSMS_RemoteStopTransaction_Rejected() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertNotNull(startTransactionResponse);
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        var params = new RemoteStopTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setTransactionId(startTransactionResponse.getTransactionId());

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.remoteStopTransaction(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var remoteStopReq = new RemoteStopTransactionRequest().withTransactionId(startTransactionResponse.getTransactionId());
        var remoteStopRes = new RemoteStopTransactionResponse().withStatus(RemoteStartStopStatus.REJECTED);
        chargePoint.expectRequest(remoteStopReq, remoteStopRes);
        assertEquals(RemoteStartStopStatus.REJECTED, operationFuture.join());

        chargePoint.close();
    }

    @Test
    public void test_TC_030_CSMS_UnlockConnector_UnlockFailure() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new UnlockConnectorParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.unlockConnector(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var unlockReq = new UnlockConnectorRequest().withConnectorId(1);
        var unlockRes = new UnlockConnectorResponse().withStatus(UnlockStatus.UNLOCK_FAILED);
        chargePoint.expectRequest(unlockReq, unlockRes);
        assertEquals(UnlockStatus.UNLOCK_FAILED, operationFuture.join());

        chargePoint.close();
    }

    @Test
    public void test_TC_031_CSMS_UnlockConnector_UnknownConnector() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new UnlockConnectorParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.unlockConnector(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var unlockReq = new UnlockConnectorRequest().withConnectorId(1);
        var unlockRes = new UnlockConnectorResponse().withStatus(UnlockStatus.NOT_SUPPORTED);
        chargePoint.expectRequest(unlockReq, unlockRes);
        assertEquals(UnlockStatus.NOT_SUPPORTED, operationFuture.join());

        chargePoint.close();
    }

    @Test
    public void test_TC_032_1_CSMS_PowerFailureBoot_StopTransactions() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var preparing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.PREPARING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertNotNull(startTransactionResponse);
        assertTrue(startTransactionResponse.getTransactionId() > 0);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        var boot = new BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());
        var bootResponse = chargePoint.send(boot, BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus());

        var connector0 = new StatusNotificationRequest()
            .withConnectorId(0)
            .withStatus(ChargePointStatus.AVAILABLE)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(connector0, StatusNotificationResponse.class));

        var connector1 = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.FINISHING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(connector1, StatusNotificationResponse.class));

        var connector2 = new StatusNotificationRequest()
            .withConnectorId(2)
            .withStatus(ChargePointStatus.AVAILABLE)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(connector2, StatusNotificationResponse.class));

        var stopTransaction = new StopTransactionRequest()
            .withTransactionId(startTransactionResponse.getTransactionId())
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStop(10)
            .withReason(Reason.POWER_LOSS)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(stopTransaction, StopTransactionResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_037_1_CSMS_OfflineStartTransaction_ValidIdTag() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertNotNull(startTransactionResponse);
        assertTrue(startTransactionResponse.getTransactionId() > 0);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_037_3_CSMS_OfflineStartTransaction_InvalidIdTag_StopOnInvalidTrue() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var invalidTag = getRandomString();

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(invalidTag)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertNotNull(startTransactionResponse);
        assertTrue(startTransactionResponse.getTransactionId() > 0);
        assertEquals(AuthorizationStatus.INVALID, startTransactionResponse.getIdTagInfo().getStatus());

        var charging = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.CHARGING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        var stopTransaction = new StopTransactionRequest()
            .withTransactionId(startTransactionResponse.getTransactionId())
            .withIdTag(invalidTag)
            .withMeterStop(10)
            .withReason(Reason.DE_AUTHORIZED)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(stopTransaction, StopTransactionResponse.class));

        var finishing = new StatusNotificationRequest()
            .withConnectorId(1)
            .withStatus(ChargePointStatus.FINISHING)
            .withErrorCode(ChargePointErrorCode.NO_ERROR)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(finishing, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_039_CSMS_OfflineTransaction() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var startTransaction = new StartTransactionRequest()
            .withConnectorId(1)
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStart(0)
            .withTimestamp(DateTime.now());
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertNotNull(startTransactionResponse);
        assertTrue(startTransactionResponse.getTransactionId() > 0);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());

        var stopTransaction = new StopTransactionRequest()
            .withTransactionId(startTransactionResponse.getTransactionId())
            .withIdTag(REGISTERED_OCPP_TAG)
            .withMeterStop(10)
            .withReason(Reason.LOCAL)
            .withTimestamp(DateTime.now());
        assertNotNull(chargePoint.send(stopTransaction, StopTransactionResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_040_1_CSMS_ConfigurationKeys_NotSupported() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var key = "UnknownConfigKey_" + getRandomString();

        var params = new ChangeConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setKeyType(ChangeConfigurationParams.ConfigurationKeyType.CUSTOM);
        params.setCustomConfKey(key);
        params.setValue("123");

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.changeConfiguration(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var changeConfigReq = new ChangeConfigurationRequest()
            .withKey(key)
            .withValue("123");
        var changeConfigRes = new ChangeConfigurationResponse().withStatus(ConfigurationStatus.NOT_SUPPORTED);
        chargePoint.expectRequest(changeConfigReq, changeConfigRes);
        assertEquals(ConfigurationStatus.NOT_SUPPORTED, operationFuture.join());

        chargePoint.close();
    }

    @Test
    public void test_TC_040_2_CSMS_ConfigurationKeys_InvalidValue() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new ChangeConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConfKey("MeterValueSampleInterval");
        params.setValue("INVALID_VALUE");

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.changeConfiguration(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var changeConfigReq = new ChangeConfigurationRequest()
            .withKey("MeterValueSampleInterval")
            .withValue("INVALID_VALUE");
        var changeConfigRes = new ChangeConfigurationResponse().withStatus(ConfigurationStatus.REJECTED);
        chargePoint.expectRequest(changeConfigReq, changeConfigRes);
        assertEquals(ConfigurationStatus.REJECTED, operationFuture.join());

        chargePoint.close();
    }

    @Test
    public void test_TC_042_1_CSMS_GetLocalListVersion_NotSupported() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new MultipleChargePointSelect();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.getLocalListVersion(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            new ocpp.cp._2015._10.GetLocalListVersionRequest(),
            new ocpp.cp._2015._10.GetLocalListVersionResponse().withListVersion(-1)
        );
        assertEquals(-1, operationFuture.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_042_2_CSMS_GetLocalListVersion_Empty() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new MultipleChargePointSelect();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.getLocalListVersion(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            new ocpp.cp._2015._10.GetLocalListVersionRequest(),
            new ocpp.cp._2015._10.GetLocalListVersionResponse().withListVersion(0)
        );
        assertEquals(0, operationFuture.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_043_1_CSMS_SendLocalAuthorizationList_NotSupported() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new SendLocalListParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setListVersion(1);
        params.setUpdateType(ocpp.cp._2015._10.UpdateType.FULL);

        var expectedRequest = new ocpp.cp._2015._10.SendLocalListRequest()
            .withListVersion(1)
            .withUpdateType(ocpp.cp._2015._10.UpdateType.FULL)
            .withLocalAuthorizationList(List.of(
                new ocpp.cp._2015._10.AuthorizationData()
                    .withIdTag(REGISTERED_OCPP_TAG)
                    .withIdTagInfo(new ocpp.cp._2015._10.IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED))
            ));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.sendLocalList(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            expectedRequest,
            new ocpp.cp._2015._10.SendLocalListResponse().withStatus(ocpp.cp._2015._10.UpdateStatus.NOT_SUPPORTED)
        );
        assertEquals(ocpp.cp._2015._10.UpdateStatus.NOT_SUPPORTED, operationFuture.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_043_3_CSMS_SendLocalAuthorizationList_Failed() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new SendLocalListParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setListVersion(1);
        params.setUpdateType(ocpp.cp._2015._10.UpdateType.FULL);

        var expectedRequest = new ocpp.cp._2015._10.SendLocalListRequest()
            .withListVersion(1)
            .withUpdateType(ocpp.cp._2015._10.UpdateType.FULL)
            .withLocalAuthorizationList(List.of(
                new ocpp.cp._2015._10.AuthorizationData()
                    .withIdTag(REGISTERED_OCPP_TAG)
                    .withIdTagInfo(new ocpp.cp._2015._10.IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED))
            ));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.sendLocalList(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            expectedRequest,
            new ocpp.cp._2015._10.SendLocalListResponse().withStatus(ocpp.cp._2015._10.UpdateStatus.FAILED)
        );
        assertEquals(ocpp.cp._2015._10.UpdateStatus.FAILED, operationFuture.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_043_4_CSMS_SendLocalAuthorizationList_Full() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new SendLocalListParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setListVersion(1);
        params.setUpdateType(ocpp.cp._2015._10.UpdateType.FULL);

        var expectedRequest = new ocpp.cp._2015._10.SendLocalListRequest()
            .withListVersion(1)
            .withUpdateType(ocpp.cp._2015._10.UpdateType.FULL)
            .withLocalAuthorizationList(List.of(
                new ocpp.cp._2015._10.AuthorizationData()
                    .withIdTag(REGISTERED_OCPP_TAG)
                    .withIdTagInfo(new ocpp.cp._2015._10.IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED))
            ));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.sendLocalList(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            expectedRequest,
            new ocpp.cp._2015._10.SendLocalListResponse().withStatus(ocpp.cp._2015._10.UpdateStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.UpdateStatus.ACCEPTED, operationFuture.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_043_5_CSMS_SendLocalAuthorizationList_Differential() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new SendLocalListParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setListVersion(2);
        params.setUpdateType(ocpp.cp._2015._10.UpdateType.DIFFERENTIAL);
        params.setAddUpdateList(List.of(REGISTERED_OCPP_TAG));

        var expectedRequest = new ocpp.cp._2015._10.SendLocalListRequest()
            .withListVersion(2)
            .withUpdateType(ocpp.cp._2015._10.UpdateType.DIFFERENTIAL)
            .withLocalAuthorizationList(List.of(
                new ocpp.cp._2015._10.AuthorizationData()
                    .withIdTag(REGISTERED_OCPP_TAG)
                    .withIdTagInfo(new ocpp.cp._2015._10.IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED))
            ));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.sendLocalList(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            expectedRequest,
            new ocpp.cp._2015._10.SendLocalListResponse().withStatus(ocpp.cp._2015._10.UpdateStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.UpdateStatus.ACCEPTED, operationFuture.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_044_1_CSMS_FirmwareUpdate_DownloadAndInstall() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new UpdateFirmwareParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("https://example.org/fw.bin");
        params.setRetries(1);
        params.setRetryInterval(1);
        params.setRetrieveDateTime(DateTime.now().plusMinutes(1));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.updateFirmware(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            new ocpp.cp._2015._10.UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(params.getRetrieveDateTime())
                .withRetries(1)
                .withRetryInterval(1),
            new ocpp.cp._2015._10.UpdateFirmwareResponse()
        );
        assertEquals(UpdateFirmwareTask.UpdateFirmwareResponseStatus.OK, operationFuture.join());

        assertNotNull(chargePoint.send(
            new ocpp.cs._2015._10.FirmwareStatusNotificationRequest()
                .withStatus(ocpp.cs._2015._10.FirmwareStatus.fromValue("Downloading")),
            ocpp.cs._2015._10.FirmwareStatusNotificationResponse.class));
        assertNotNull(chargePoint.send(
            new ocpp.cs._2015._10.FirmwareStatusNotificationRequest()
                .withStatus(ocpp.cs._2015._10.FirmwareStatus.fromValue("Downloaded")),
            ocpp.cs._2015._10.FirmwareStatusNotificationResponse.class));
        assertNotNull(chargePoint.send(
            new StatusNotificationRequest()
                .withConnectorId(1)
                .withStatus(ChargePointStatus.UNAVAILABLE)
                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                .withTimestamp(DateTime.now()),
            StatusNotificationResponse.class));
        assertNotNull(chargePoint.send(
            new ocpp.cs._2015._10.FirmwareStatusNotificationRequest()
                .withStatus(ocpp.cs._2015._10.FirmwareStatus.fromValue("Installing")),
            ocpp.cs._2015._10.FirmwareStatusNotificationResponse.class));
        assertEquals(RegistrationStatus.ACCEPTED, chargePoint.send(
            new BootNotificationRequest().withChargePointVendor(getRandomString()).withChargePointModel(getRandomString()),
            BootNotificationResponse.class).getStatus());
        assertNotNull(chargePoint.send(
            new StatusNotificationRequest()
                .withConnectorId(1)
                .withStatus(ChargePointStatus.AVAILABLE)
                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                .withTimestamp(DateTime.now()),
            StatusNotificationResponse.class));
        assertNotNull(chargePoint.send(
            new ocpp.cs._2015._10.FirmwareStatusNotificationRequest()
                .withStatus(ocpp.cs._2015._10.FirmwareStatus.fromValue("Installed")),
            ocpp.cs._2015._10.FirmwareStatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_044_2_CSMS_FirmwareUpdate_DownloadFailed() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new UpdateFirmwareParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("https://example.org/fw.bin");
        params.setRetries(1);
        params.setRetryInterval(1);
        params.setRetrieveDateTime(DateTime.now().plusMinutes(1));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.updateFirmware(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            new ocpp.cp._2015._10.UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(params.getRetrieveDateTime())
                .withRetries(1)
                .withRetryInterval(1),
            new ocpp.cp._2015._10.UpdateFirmwareResponse()
        );
        assertEquals(UpdateFirmwareTask.UpdateFirmwareResponseStatus.OK, operationFuture.join());

        assertNotNull(chargePoint.send(
            new ocpp.cs._2015._10.FirmwareStatusNotificationRequest()
                .withStatus(ocpp.cs._2015._10.FirmwareStatus.fromValue("Downloading")),
            ocpp.cs._2015._10.FirmwareStatusNotificationResponse.class));
        assertNotNull(chargePoint.send(
            new ocpp.cs._2015._10.FirmwareStatusNotificationRequest()
                .withStatus(ocpp.cs._2015._10.FirmwareStatus.fromValue("DownloadFailed")),
            ocpp.cs._2015._10.FirmwareStatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_044_3_CSMS_FirmwareUpdate_InstallationFailed() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new UpdateFirmwareParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("https://example.org/fw.bin");
        params.setRetries(1);
        params.setRetryInterval(1);
        params.setRetrieveDateTime(DateTime.now().plusMinutes(1));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.updateFirmware(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            new ocpp.cp._2015._10.UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(params.getRetrieveDateTime())
                .withRetries(1)
                .withRetryInterval(1),
            new ocpp.cp._2015._10.UpdateFirmwareResponse()
        );
        assertEquals(UpdateFirmwareTask.UpdateFirmwareResponseStatus.OK, operationFuture.join());

        assertNotNull(chargePoint.send(new ocpp.cs._2015._10.FirmwareStatusNotificationRequest()
            .withStatus(ocpp.cs._2015._10.FirmwareStatus.fromValue("Downloading")), ocpp.cs._2015._10.FirmwareStatusNotificationResponse.class));
        assertNotNull(chargePoint.send(new ocpp.cs._2015._10.FirmwareStatusNotificationRequest()
            .withStatus(ocpp.cs._2015._10.FirmwareStatus.fromValue("Downloaded")), ocpp.cs._2015._10.FirmwareStatusNotificationResponse.class));
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.UNAVAILABLE).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));
        assertNotNull(chargePoint.send(new ocpp.cs._2015._10.FirmwareStatusNotificationRequest()
            .withStatus(ocpp.cs._2015._10.FirmwareStatus.fromValue("Installing")), ocpp.cs._2015._10.FirmwareStatusNotificationResponse.class));
        assertEquals(RegistrationStatus.ACCEPTED, chargePoint.send(
            new BootNotificationRequest().withChargePointVendor(getRandomString()).withChargePointModel(getRandomString()),
            BootNotificationResponse.class).getStatus());
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.AVAILABLE).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));
        assertNotNull(chargePoint.send(new ocpp.cs._2015._10.FirmwareStatusNotificationRequest()
            .withStatus(ocpp.cs._2015._10.FirmwareStatus.fromValue("InstallationFailed")), ocpp.cs._2015._10.FirmwareStatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_045_1_CSMS_GetDiagnostics() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new GetDiagnosticsParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("ftp://user:pass@example.org/logs");
        params.setRetries(1);
        params.setRetryInterval(1);
        params.setStart(DateTime.now().minusHours(2));
        params.setStop(DateTime.now().minusHours(1));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.getDiagnostics(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            new ocpp.cp._2015._10.GetDiagnosticsRequest()
                .withLocation(params.getLocation())
                .withRetries(1)
                .withRetryInterval(1)
                .withStartTime(params.getStart())
                .withStopTime(params.getStop()),
            new ocpp.cp._2015._10.GetDiagnosticsResponse().withFileName("diag.log")
        );
        assertEquals("diag.log", operationFuture.join().getFileName());

        assertNotNull(chargePoint.send(new ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest()
            .withStatus(ocpp.cs._2015._10.DiagnosticsStatus.fromValue("Uploading")), ocpp.cs._2015._10.DiagnosticsStatusNotificationResponse.class));
        assertNotNull(chargePoint.send(new ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest()
            .withStatus(ocpp.cs._2015._10.DiagnosticsStatus.fromValue("Uploaded")), ocpp.cs._2015._10.DiagnosticsStatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_045_2_CSMS_GetDiagnostics_UploadFailed() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new GetDiagnosticsParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("ftp://user:pass@example.org/logs");
        params.setRetries(1);
        params.setRetryInterval(1);
        params.setStart(DateTime.now().minusHours(2));
        params.setStop(DateTime.now().minusHours(1));

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.getDiagnostics(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            new ocpp.cp._2015._10.GetDiagnosticsRequest()
                .withLocation(params.getLocation())
                .withRetries(1)
                .withRetryInterval(1)
                .withStartTime(params.getStart())
                .withStopTime(params.getStop()),
            new ocpp.cp._2015._10.GetDiagnosticsResponse().withFileName("diag.log")
        );
        assertEquals("diag.log", operationFuture.join().getFileName());

        assertNotNull(chargePoint.send(new ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest()
            .withStatus(ocpp.cs._2015._10.DiagnosticsStatus.fromValue("Uploading")), ocpp.cs._2015._10.DiagnosticsStatusNotificationResponse.class));
        assertNotNull(chargePoint.send(new ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest()
            .withStatus(ocpp.cs._2015._10.DiagnosticsStatus.fromValue("UploadFailed")), ocpp.cs._2015._10.DiagnosticsStatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_046_CSMS_ReservationOfConnector_Transaction() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var expiry = DateTime.now().plusMinutes(5);
        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(expiry);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reserveNow(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ReserveNowRequest().withConnectorId(1).withExpiryDate(expiry).withIdTag(REGISTERED_OCPP_TAG).withReservationId(1),
            new ocpp.cp._2015._10.ReserveNowResponse().withStatus(ocpp.cp._2015._10.ReservationStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.ReservationStatus.ACCEPTED, operationFuture.join());

        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.RESERVED).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));
        assertNotNull(chargePoint.send(new StartTransactionRequest()
            .withConnectorId(1).withIdTag(REGISTERED_OCPP_TAG).withReservationId(1).withMeterStart(0).withTimestamp(DateTime.now()), StartTransactionResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_047_CSMS_ReservationOfConnector_Expire() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var expiry = DateTime.now().plusMinutes(2);
        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(expiry);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reserveNow(params);
                assertNotNull(callback);
                assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ReserveNowRequest().withConnectorId(1).withExpiryDate(expiry).withIdTag(REGISTERED_OCPP_TAG).withReservationId(1),
            new ocpp.cp._2015._10.ReserveNowResponse().withStatus(ocpp.cp._2015._10.ReservationStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.ReservationStatus.ACCEPTED, operationFuture.join());
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.RESERVED).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.AVAILABLE).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now().plusMinutes(3)), StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_048_1_CSMS_ReservationOfConnector_Faulted() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(DateTime.now().plusMinutes(5));
        params.setIdTag(REGISTERED_OCPP_TAG);

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reserveNow(params);
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ReserveNowRequest().withConnectorId(1).withExpiryDate(params.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(1),
            new ocpp.cp._2015._10.ReserveNowResponse().withStatus(ocpp.cp._2015._10.ReservationStatus.FAULTED)
        );
        assertEquals(ocpp.cp._2015._10.ReservationStatus.FAULTED, future.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_048_2_CSMS_ReservationOfConnector_Occupied() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.PREPARING).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));

        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(DateTime.now().plusMinutes(5));
        params.setIdTag(REGISTERED_OCPP_TAG);

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reserveNow(params);
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ReserveNowRequest().withConnectorId(1).withExpiryDate(params.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(1),
            new ocpp.cp._2015._10.ReserveNowResponse().withStatus(ocpp.cp._2015._10.ReservationStatus.OCCUPIED)
        );
        assertEquals(ocpp.cp._2015._10.ReservationStatus.OCCUPIED, future.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_048_3_CSMS_ReservationOfConnector_Unavailable() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(DateTime.now().plusMinutes(5));
        params.setIdTag(REGISTERED_OCPP_TAG);

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reserveNow(params);
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ReserveNowRequest().withConnectorId(1).withExpiryDate(params.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(1),
            new ocpp.cp._2015._10.ReserveNowResponse().withStatus(ocpp.cp._2015._10.ReservationStatus.UNAVAILABLE)
        );
        assertEquals(ocpp.cp._2015._10.ReservationStatus.UNAVAILABLE, future.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_048_4_CSMS_ReservationOfConnector_Rejected() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(DateTime.now().plusMinutes(5));
        params.setIdTag(REGISTERED_OCPP_TAG);

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reserveNow(params);
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ReserveNowRequest().withConnectorId(1).withExpiryDate(params.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(1),
            new ocpp.cp._2015._10.ReserveNowResponse().withStatus(ocpp.cp._2015._10.ReservationStatus.REJECTED)
        );
        assertEquals(ocpp.cp._2015._10.ReservationStatus.REJECTED, future.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_049_CSMS_ReservationOfChargePoint_Transaction() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(0);
        params.setExpiry(DateTime.now().plusMinutes(5));
        params.setIdTag(REGISTERED_OCPP_TAG);

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reserveNow(params);
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ReserveNowRequest().withConnectorId(0).withExpiryDate(params.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(1),
            new ocpp.cp._2015._10.ReserveNowResponse().withStatus(ocpp.cp._2015._10.ReservationStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.ReservationStatus.ACCEPTED, future.join());
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.RESERVED).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));
        chargePoint.close();
    }

    @Test
    public void test_TC_051_CSMS_CancelReservation() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var reserve = new ReserveNowParams();
        reserve.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        reserve.setConnectorId(1);
        reserve.setExpiry(DateTime.now().plusMinutes(5));
        reserve.setIdTag(REGISTERED_OCPP_TAG);

        var reserveFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reserveNow(reserve);
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ReserveNowRequest().withConnectorId(1).withExpiryDate(reserve.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(1),
            new ocpp.cp._2015._10.ReserveNowResponse().withStatus(ocpp.cp._2015._10.ReservationStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.ReservationStatus.ACCEPTED, reserveFuture.join());
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.RESERVED).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));

        var cancel = new CancelReservationParams();
        cancel.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        cancel.setReservationId(1);

        var cancelFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.cancelReservation(cancel);
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.CancelReservationRequest().withReservationId(1),
            new ocpp.cp._2015._10.CancelReservationResponse().withStatus(ocpp.cp._2015._10.CancelReservationStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.CancelReservationStatus.ACCEPTED, cancelFuture.join());
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.AVAILABLE).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));
        chargePoint.close();
    }

    @Test
    public void test_TC_052_CSMS_CancelReservation_Rejected() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var reserve = new ReserveNowParams();
        reserve.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        reserve.setConnectorId(1);
        reserve.setExpiry(DateTime.now().plusMinutes(5));
        reserve.setIdTag(REGISTERED_OCPP_TAG);

        var reserveFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reserveNow(reserve);
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ReserveNowRequest().withConnectorId(1).withExpiryDate(reserve.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(1),
            new ocpp.cp._2015._10.ReserveNowResponse().withStatus(ocpp.cp._2015._10.ReservationStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.ReservationStatus.ACCEPTED, reserveFuture.join());
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.RESERVED).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));

        var cancel = new CancelReservationParams();
        cancel.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        cancel.setReservationId(1);

        var cancelFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.cancelReservation(cancel);
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.CancelReservationRequest().withReservationId(1),
            new ocpp.cp._2015._10.CancelReservationResponse().withStatus(ocpp.cp._2015._10.CancelReservationStatus.REJECTED)
        );
        assertEquals(ocpp.cp._2015._10.CancelReservationStatus.REJECTED, cancelFuture.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_053_CSMS_UseReservedConnectorWithParentIdTag() {
        var parentTag = getRandomString();
        var childTag = getRandomString();

        dslContext.insertInto(OCPP_TAG)
            .set(OCPP_TAG.ID_TAG, parentTag)
            .set(OCPP_TAG.NOTE, "integration parent idTag")
            .execute();
        dslContext.insertInto(OCPP_TAG)
            .set(OCPP_TAG.ID_TAG, childTag)
            .set(OCPP_TAG.PARENT_ID_TAG, parentTag)
            .set(OCPP_TAG.NOTE, "integration child idTag")
            .execute();

        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var reserve = new ReserveNowParams();
        reserve.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        reserve.setConnectorId(1);
        reserve.setExpiry(DateTime.now().plusMinutes(5));
        reserve.setIdTag(childTag);

        var reserveFuture = CompletableFuture.supplyAsync(() -> {
            try {
                var callback = operationsService.reserveNow(reserve);
                return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ReserveNowRequest()
                .withConnectorId(1)
                .withExpiryDate(reserve.getExpiry())
                .withIdTag(childTag)
                .withParentIdTag(parentTag)
                .withReservationId(1),
            new ocpp.cp._2015._10.ReserveNowResponse().withStatus(ocpp.cp._2015._10.ReservationStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.ReservationStatus.ACCEPTED, reserveFuture.join());
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.RESERVED).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_054_CSMS_TriggerMessage() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var p1 = new TriggerMessageParams();
        p1.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        p1.setTriggerMessage(TriggerMessageEnum.MeterValues);
        p1.setConnectorId(1);
        var f1 = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.triggerMessage(p1).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.TriggerMessageRequest().withRequestedMessage(ocpp.cp._2015._10.MessageTrigger.METER_VALUES).withConnectorId(1),
            new ocpp.cp._2015._10.TriggerMessageResponse().withStatus(ocpp.cp._2015._10.TriggerMessageStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.TriggerMessageStatus.ACCEPTED, f1.join());

        assertNotNull(chargePoint.send(new ocpp.cs._2015._10.MeterValuesRequest().withConnectorId(1), ocpp.cs._2015._10.MeterValuesResponse.class));

        var p2 = new TriggerMessageParams();
        p2.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        p2.setTriggerMessage(TriggerMessageEnum.Heartbeat);
        var f2 = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.triggerMessage(p2).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.TriggerMessageRequest().withRequestedMessage(ocpp.cp._2015._10.MessageTrigger.HEARTBEAT),
            new ocpp.cp._2015._10.TriggerMessageResponse().withStatus(ocpp.cp._2015._10.TriggerMessageStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.TriggerMessageStatus.ACCEPTED, f2.join());
        assertNotNull(chargePoint.send(new HeartbeatRequest(), HeartbeatResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_055_CSMS_TriggerMessage_Rejected() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var p = new TriggerMessageParams();
        p.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        p.setTriggerMessage(TriggerMessageEnum.MeterValues);
        p.setConnectorId(1);

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.triggerMessage(p).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.TriggerMessageRequest().withRequestedMessage(ocpp.cp._2015._10.MessageTrigger.METER_VALUES).withConnectorId(1),
            new ocpp.cp._2015._10.TriggerMessageResponse().withStatus(ocpp.cp._2015._10.TriggerMessageStatus.REJECTED)
        );
        assertEquals(ocpp.cp._2015._10.TriggerMessageStatus.REJECTED, future.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_056_CSMS_CentralSmartCharging_TxDefaultProfile() {
        var form = new ChargingProfileForm();
        form.setDescription("tc056");
        form.setStackLevel(0);
        form.setChargingProfilePurpose(ocpp.cp._2015._10.ChargingProfilePurposeType.TX_DEFAULT_PROFILE);
        form.setChargingProfileKind(ocpp.cp._2015._10.ChargingProfileKindType.ABSOLUTE);
        form.setChargingRateUnit(ocpp.cp._2015._10.ChargingRateUnitType.W);
        form.setValidFrom(DateTime.now().plusMinutes(1));
        form.setValidTo(DateTime.now().plusHours(2));
        form.setStartSchedule(DateTime.now().plusMinutes(2));
        form.setDurationInSeconds(3600);
        var period = new ChargingProfileForm.SchedulePeriod();
        period.setStartPeriodInSeconds(0);
        period.setPowerLimit(BigDecimal.valueOf(6.0));
        period.setNumberPhases(3);
        form.setSchedulePeriods(List.of(period));
        var profilePk = chargingProfileRepository.add(form);
        var details = chargingProfileRepository.getDetails(profilePk);

        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var params = new SetChargingProfileParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setChargingProfilePk(profilePk);

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.setChargingProfile(params).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.SetChargingProfileRequest().withConnectorId(1).withCsChargingProfiles(ChargingProfileDetailsMapper.mapToOcpp(details, null)),
            new ocpp.cp._2015._10.SetChargingProfileResponse().withStatus(ocpp.cp._2015._10.ChargingProfileStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.ChargingProfileStatus.ACCEPTED, future.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_057_CSMS_CentralSmartCharging_TxProfile() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var start = chargePoint.send(new StartTransactionRequest()
            .withConnectorId(1).withIdTag(REGISTERED_OCPP_TAG).withMeterStart(0).withTimestamp(DateTime.now()), StartTransactionResponse.class);
        var txId = start.getTransactionId();

        var form = new ChargingProfileForm();
        form.setDescription("tc057");
        form.setStackLevel(0);
        form.setChargingProfilePurpose(ocpp.cp._2015._10.ChargingProfilePurposeType.TX_PROFILE);
        form.setChargingProfileKind(ocpp.cp._2015._10.ChargingProfileKindType.ABSOLUTE);
        form.setChargingRateUnit(ocpp.cp._2015._10.ChargingRateUnitType.W);
        form.setDurationInSeconds(1800);
        var period = new ChargingProfileForm.SchedulePeriod();
        period.setStartPeriodInSeconds(0);
        period.setPowerLimit(BigDecimal.valueOf(6.0));
        form.setSchedulePeriods(List.of(period));
        var profilePk = chargingProfileRepository.add(form);
        var details = chargingProfileRepository.getDetails(profilePk);

        var params = new SetChargingProfileParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setChargingProfilePk(profilePk);
        params.setTransactionId(txId);

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.setChargingProfile(params).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.SetChargingProfileRequest().withConnectorId(1).withCsChargingProfiles(ChargingProfileDetailsMapper.mapToOcpp(details, txId)),
            new ocpp.cp._2015._10.SetChargingProfileResponse().withStatus(ocpp.cp._2015._10.ChargingProfileStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.ChargingProfileStatus.ACCEPTED, future.join());
        chargePoint.close();
    }

    @Test
    public void test_TC_059_CSMS_RemoteStartTransactionWithChargingProfile() {
        var form = new ChargingProfileForm();
        form.setDescription("tc059");
        form.setStackLevel(0);
        form.setChargingProfilePurpose(ocpp.cp._2015._10.ChargingProfilePurposeType.TX_PROFILE);
        form.setChargingProfileKind(ocpp.cp._2015._10.ChargingProfileKindType.ABSOLUTE);
        form.setChargingRateUnit(ocpp.cp._2015._10.ChargingRateUnitType.W);
        form.setDurationInSeconds(1200);
        var period = new ChargingProfileForm.SchedulePeriod();
        period.setStartPeriodInSeconds(0);
        period.setPowerLimit(BigDecimal.valueOf(6.0));
        form.setSchedulePeriods(List.of(period));
        var profilePk = chargingProfileRepository.add(form);
        var details = chargingProfileRepository.getDetails(profilePk);

        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var params = new RemoteStartTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setIdTag(REGISTERED_OCPP_TAG);
        params.setChargingProfilePk(profilePk);

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.remoteStartTransaction(params).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.RemoteStartTransactionRequest()
                .withIdTag(REGISTERED_OCPP_TAG)
                .withConnectorId(1)
                .withChargingProfile(ChargingProfileDetailsMapper.mapToOcpp(details, null)),
            new ocpp.cp._2015._10.RemoteStartTransactionResponse().withStatus(RemoteStartStopStatus.ACCEPTED)
        );
        assertEquals(RemoteStartStopStatus.ACCEPTED, future.join());

        assertEquals(AuthorizationStatus.ACCEPTED, chargePoint.send(new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG), AuthorizeResponse.class).getIdTagInfo().getStatus());
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.PREPARING).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));
        assertEquals(AuthorizationStatus.ACCEPTED, chargePoint.send(new StartTransactionRequest()
            .withConnectorId(1).withIdTag(REGISTERED_OCPP_TAG).withMeterStart(0).withTimestamp(DateTime.now()), StartTransactionResponse.class).getIdTagInfo().getStatus());
        assertNotNull(chargePoint.send(new StatusNotificationRequest()
            .withConnectorId(1).withStatus(ChargePointStatus.CHARGING).withErrorCode(ChargePointErrorCode.NO_ERROR).withTimestamp(DateTime.now()), StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_064_CSMS_DataTransferToCentralSystem() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var request = new ocpp.cs._2015._10.DataTransferRequest()
            .withVendorId(getRandomString())
            .withMessageId("TestMessage")
            .withData("test-payload");
        var response = chargePoint.send(request, ocpp.cs._2015._10.DataTransferResponse.class);

        var status = response.getStatus();
        var statusValue = status.value();
        assertTrue(
            statusValue.equals("Rejected")
                || statusValue.equals("UnknownMessageId")
                || statusValue.equals("UnknownVendorId")
                || statusValue.equals("Accepted")
        );

        chargePoint.close();
    }

    @Test
    public void test_TC_066_CSMS_GetCompositeSchedule() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var params = new GetCompositeScheduleParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setDurationInSeconds(300);
        params.setChargingRateUnit(ocpp.cp._2015._10.ChargingRateUnitType.W);

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.getCompositeSchedule(params).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.GetCompositeScheduleRequest().withConnectorId(1).withDuration(300).withChargingRateUnit(ocpp.cp._2015._10.ChargingRateUnitType.W),
            new ocpp.cp._2015._10.GetCompositeScheduleResponse()
                .withStatus(ocpp.cp._2015._10.GetCompositeScheduleStatus.ACCEPTED)
                .withConnectorId(1)
                .withScheduleStart(DateTime.now())
                .withChargingSchedule(new ocpp.cp._2015._10.ChargingSchedule()
                    .withChargingRateUnit(ocpp.cp._2015._10.ChargingRateUnitType.W)
                    .withDuration(300)
                    .withChargingSchedulePeriod(List.of(new ocpp.cp._2015._10.ChargingSchedulePeriod().withStartPeriod(0).withLimit(BigDecimal.valueOf(6.0)))))
        );
        assertEquals(ocpp.cp._2015._10.GetCompositeScheduleStatus.ACCEPTED, future.join().getStatus());
        chargePoint.close();
    }

    @Test
    public void test_TC_067_CSMS_ClearChargingProfile() {
        var txDefaultForm = new ChargingProfileForm();
        txDefaultForm.setDescription("tc067-tx-default");
        txDefaultForm.setStackLevel(0);
        txDefaultForm.setChargingProfilePurpose(ocpp.cp._2015._10.ChargingProfilePurposeType.TX_DEFAULT_PROFILE);
        txDefaultForm.setChargingProfileKind(ocpp.cp._2015._10.ChargingProfileKindType.ABSOLUTE);
        txDefaultForm.setChargingRateUnit(ocpp.cp._2015._10.ChargingRateUnitType.W);
        txDefaultForm.setDurationInSeconds(900);
        var txDefaultPeriod = new ChargingProfileForm.SchedulePeriod();
        txDefaultPeriod.setStartPeriodInSeconds(0);
        txDefaultPeriod.setPowerLimit(BigDecimal.valueOf(6.0));
        txDefaultForm.setSchedulePeriods(List.of(txDefaultPeriod));
        var txDefaultPk = chargingProfileRepository.add(txDefaultForm);
        var txDefaultDetails = chargingProfileRepository.getDetails(txDefaultPk);

        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var setParams = new SetChargingProfileParams();
        setParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        setParams.setConnectorId(1);
        setParams.setChargingProfilePk(txDefaultPk);
        var setFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.setChargingProfile(setParams).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.SetChargingProfileRequest().withConnectorId(1).withCsChargingProfiles(ChargingProfileDetailsMapper.mapToOcpp(txDefaultDetails, null)),
            new ocpp.cp._2015._10.SetChargingProfileResponse().withStatus(ocpp.cp._2015._10.ChargingProfileStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.ChargingProfileStatus.ACCEPTED, setFuture.join());

        var clearById = new ClearChargingProfileParams();
        clearById.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        clearById.setFilterType(ClearChargingProfileFilterType.ChargingProfileId);
        clearById.setChargingProfilePk(txDefaultPk);
        var clearByIdFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.clearChargingProfile(clearById).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ClearChargingProfileRequest().withId(txDefaultPk),
            new ocpp.cp._2015._10.ClearChargingProfileResponse().withStatus(ocpp.cp._2015._10.ClearChargingProfileStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.ClearChargingProfileStatus.ACCEPTED, clearByIdFuture.join());

        var clearAll = new ClearChargingProfileParams();
        clearAll.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        clearAll.setFilterType(ClearChargingProfileFilterType.OtherParameters);
        var clearAllFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.clearChargingProfile(clearAll).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp.cp._2015._10.ClearChargingProfileRequest(),
            new ocpp.cp._2015._10.ClearChargingProfileResponse().withStatus(ocpp.cp._2015._10.ClearChargingProfileStatus.ACCEPTED)
        );
        assertEquals(ocpp.cp._2015._10.ClearChargingProfileStatus.ACCEPTED, clearAllFuture.join());

        chargePoint.close();
    }

    @Test
    public void test_TC_073_CSMS_UpdateChargePointPasswordForHttpBasicAuth() {
        String password = "0123456789abcdef0123456789abcdef";
        String newPassword = "1023456789abcdef0123456789abcdef";

        dslContext.update(CHARGE_BOX)
            .set(CHARGE_BOX.SECURITY_PROFILE, 1)
            .set(CHARGE_BOX.AUTH_PASSWORD, passwordEncoder.encode(password))
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .execute();

        var chargePoint = new OcppJsonChargePoint(
            OcppVersion.V_16,
            REGISTERED_CHARGE_BOX_ID,
            PATH,
            password
        ).start();

        expectGetConfCpoName(chargePoint);

        var params = new ChangeConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setKeyType(ChangeConfigurationParams.ConfigurationKeyType.PREDEFINED);
        params.setConfKey(AuthorizationKey.name());
        params.setValue(newPassword);

        var valueHex = HexFormat.of().formatHex(params.getValue().getBytes(StandardCharsets.UTF_8));

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.changeConfiguration(params).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ChangeConfigurationRequest().withKey(AuthorizationKey.name()).withValue(valueHex),
            new ChangeConfigurationResponse().withStatus(ConfigurationStatus.ACCEPTED)
        );
        assertEquals(ConfigurationStatus.ACCEPTED, future.join());

        chargePoint.close();

        var record = dslContext
            .selectFrom(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .fetchOne();

        assertNotNull(record);
        assertTrue(passwordEncoder.matches(newPassword, record.getAuthPassword()));

        chargePoint = new OcppJsonChargePoint(
            OcppVersion.V_16,
            REGISTERED_CHARGE_BOX_ID,
            PATH,
            newPassword
        ).start();

        expectGetConfCpoName(chargePoint);

        chargePoint.close();
    }

    @Test
    @Disabled("Pending implementation")
    public void test_TC_074_CSMS_PENDING() {
        log.info("Skipping TC_074_CSMS until scenario harness is expanded");
    }

    @Test
    public void test_TC_075_1_CSMS_InstallManufacturerRootCertificate() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var certificate = "-----BEGIN CERTIFICATE-----\\nMANUFACTURER-ROOT-" + getRandomString() + "\\n-----END CERTIFICATE-----";
        var installParams = new InstallCertificateParams();
        installParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        installParams.setCertificateType(ocpp._2022._02.security.InstallCertificate.CertificateUseEnumType.MANUFACTURER_ROOT_CERTIFICATE);
        installParams.setCertificate(certificate);

        var installFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.installCertificate(installParams).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp._2022._02.security.InstallCertificate()
                .withCertificateType(ocpp._2022._02.security.InstallCertificate.CertificateUseEnumType.MANUFACTURER_ROOT_CERTIFICATE)
                .withCertificate(certificate),
            new ocpp._2022._02.security.InstallCertificateResponse()
                .withStatus(ocpp._2022._02.security.InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED)
        );
        assertEquals(ocpp._2022._02.security.InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED, installFuture.join());

        var hashData = new ocpp._2022._02.security.CertificateHashData()
            .withHashAlgorithm(ocpp._2022._02.security.CertificateHashData.HashAlgorithmEnumType.SHA_256)
            .withIssuerNameHash("issuer-name-hash-manufacturer")
            .withIssuerKeyHash("issuer-key-hash-manufacturer")
            .withSerialNumber("serial-manufacturer");

        var getIdsParams = new GetInstalledCertificateIdsParams();
        getIdsParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        getIdsParams.setCertificateType(ocpp._2022._02.security.GetInstalledCertificateIds.CertificateUseEnumType.MANUFACTURER_ROOT_CERTIFICATE);

        var getIdsFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.getInstalledCertificateIds(getIdsParams).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp._2022._02.security.GetInstalledCertificateIds()
                .withCertificateType(ocpp._2022._02.security.GetInstalledCertificateIds.CertificateUseEnumType.MANUFACTURER_ROOT_CERTIFICATE),
            new ocpp._2022._02.security.GetInstalledCertificateIdsResponse()
                .withStatus(ocpp._2022._02.security.GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED)
                .withCertificateHashData(List.of(hashData))
        );

        var getIdsResponse = getIdsFuture.join();
        assertEquals(ocpp._2022._02.security.GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED, getIdsResponse.getStatus());
        assertNotNull(getIdsResponse.getCertificateHashData());
        assertEquals(1, getIdsResponse.getCertificateHashData().size());

        chargePoint.close();
    }

    @Test
    public void test_TC_075_2_CSMS_InstallCentralSystemRootCertificate() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var certificate = "-----BEGIN CERTIFICATE-----\\nCSMS-ROOT-" + getRandomString() + "\\n-----END CERTIFICATE-----";
        var installParams = new InstallCertificateParams();
        installParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        installParams.setCertificateType(InstallCertificate.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE);
        installParams.setCertificate(certificate);

        var installFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.installCertificate(installParams).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp._2022._02.security.InstallCertificate()
                .withCertificateType(ocpp._2022._02.security.InstallCertificate.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE)
                .withCertificate(certificate),
            new ocpp._2022._02.security.InstallCertificateResponse()
                .withStatus(ocpp._2022._02.security.InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED)
        );
        assertEquals(ocpp._2022._02.security.InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED, installFuture.join());

        var hashData = new ocpp._2022._02.security.CertificateHashData()
            .withHashAlgorithm(ocpp._2022._02.security.CertificateHashData.HashAlgorithmEnumType.SHA_256)
            .withIssuerNameHash("issuer-name-hash-csms")
            .withIssuerKeyHash("issuer-key-hash-csms")
            .withSerialNumber("serial-csms");

        var getIdsParams = new GetInstalledCertificateIdsParams();
        getIdsParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        getIdsParams.setCertificateType(ocpp._2022._02.security.GetInstalledCertificateIds.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE);

        var getIdsFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.getInstalledCertificateIds(getIdsParams).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp._2022._02.security.GetInstalledCertificateIds()
                .withCertificateType(ocpp._2022._02.security.GetInstalledCertificateIds.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE),
            new ocpp._2022._02.security.GetInstalledCertificateIdsResponse()
                .withStatus(ocpp._2022._02.security.GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED)
                .withCertificateHashData(List.of(hashData))
        );
        var getIdsResponse = getIdsFuture.join();
        assertEquals(ocpp._2022._02.security.GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED, getIdsResponse.getStatus());
        assertNotNull(getIdsResponse.getCertificateHashData());
        assertEquals(1, getIdsResponse.getCertificateHashData().size());

        chargePoint.close();
    }

    @Test
    public void test_TC_076_CSMS_DeleteSpecificInstalledCertificate() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        for (var hashAlgorithm : List.of(
            ocpp._2022._02.security.CertificateHashData.HashAlgorithmEnumType.SHA_256,
            ocpp._2022._02.security.CertificateHashData.HashAlgorithmEnumType.SHA_384,
            ocpp._2022._02.security.CertificateHashData.HashAlgorithmEnumType.SHA_512
        )) {
            var certificate = "-----BEGIN CERTIFICATE-----\\nCSMS-ROOT-" + hashAlgorithm.value() + "-" + getRandomString() + "\\n-----END CERTIFICATE-----";
            var installParams = new InstallCertificateParams();
            installParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            installParams.setCertificateType(ocpp._2022._02.security.InstallCertificate.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE);
            installParams.setCertificate(certificate);

            var installFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return operationsService.installCertificate(installParams).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            chargePoint.expectRequest(
                new ocpp._2022._02.security.InstallCertificate()
                    .withCertificateType(ocpp._2022._02.security.InstallCertificate.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE)
                    .withCertificate(certificate),
                new ocpp._2022._02.security.InstallCertificateResponse()
                    .withStatus(ocpp._2022._02.security.InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED)
            );
            assertEquals(ocpp._2022._02.security.InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED, installFuture.join());

            var hashData = new ocpp._2022._02.security.CertificateHashData()
                .withHashAlgorithm(hashAlgorithm)
                .withIssuerNameHash("issuer-name-" + hashAlgorithm.value().toLowerCase())
                .withIssuerKeyHash("issuer-key-" + hashAlgorithm.value().toLowerCase())
                .withSerialNumber("serial-" + hashAlgorithm.value().toLowerCase());

            var getIdsParams = new GetInstalledCertificateIdsParams();
            getIdsParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            getIdsParams.setCertificateType(ocpp._2022._02.security.GetInstalledCertificateIds.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE);

            var getIdsFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return operationsService.getInstalledCertificateIds(getIdsParams).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            chargePoint.expectRequest(
                new ocpp._2022._02.security.GetInstalledCertificateIds()
                    .withCertificateType(ocpp._2022._02.security.GetInstalledCertificateIds.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE),
                new ocpp._2022._02.security.GetInstalledCertificateIdsResponse()
                    .withStatus(ocpp._2022._02.security.GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED)
                    .withCertificateHashData(List.of(hashData))
            );
            assertEquals(ocpp._2022._02.security.GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED, getIdsFuture.join().getStatus());

            var installedCertificateId = dslContext.select(CHARGE_BOX_CERTIFICATE_INSTALLED.ID)
                .from(CHARGE_BOX_CERTIFICATE_INSTALLED)
                .join(CHARGE_BOX).on(CHARGE_BOX_CERTIFICATE_INSTALLED.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
                .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
                .and(CHARGE_BOX_CERTIFICATE_INSTALLED.CERTIFICATE_TYPE.eq(ocpp._2022._02.security.GetInstalledCertificateIds.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE.value()))
                .orderBy(CHARGE_BOX_CERTIFICATE_INSTALLED.ID.desc())
                .limit(1)
                .fetchOne(CHARGE_BOX_CERTIFICATE_INSTALLED.ID);
            assertNotNull(installedCertificateId);

            var deleteParams = new DeleteCertificateParams();
            deleteParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            deleteParams.setInstalledCertificateId(installedCertificateId);

            var deleteFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return operationsService.deleteCertificate(deleteParams).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            chargePoint.expectRequest(
                new ocpp._2022._02.security.DeleteCertificate()
                    .withCertificateHashData(new ocpp._2022._02.security.CertificateHashDataType()
                        .withHashAlgorithm(ocpp._2022._02.security.CertificateHashDataType.HashAlgorithmEnumType.fromValue(hashAlgorithm.value()))
                        .withIssuerNameHash(hashData.getIssuerNameHash())
                        .withIssuerKeyHash(hashData.getIssuerKeyHash())
                        .withSerialNumber(hashData.getSerialNumber())),
                new ocpp._2022._02.security.DeleteCertificateResponse()
                    .withStatus(ocpp._2022._02.security.DeleteCertificateResponse.DeleteCertificateStatusEnumType.ACCEPTED)
            );
            assertEquals(ocpp._2022._02.security.DeleteCertificateResponse.DeleteCertificateStatusEnumType.ACCEPTED, deleteFuture.join());

            var verifyFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return operationsService.getInstalledCertificateIds(getIdsParams).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            chargePoint.expectRequest(
                new ocpp._2022._02.security.GetInstalledCertificateIds()
                    .withCertificateType(ocpp._2022._02.security.GetInstalledCertificateIds.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE),
                new ocpp._2022._02.security.GetInstalledCertificateIdsResponse()
                    .withStatus(ocpp._2022._02.security.GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED)
                    .withCertificateHashData(null)
            );
            assertEquals(ocpp._2022._02.security.GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED, verifyFuture.join().getStatus());
        }

        var remaining = dslContext.selectCount()
            .from(CHARGE_BOX_CERTIFICATE_INSTALLED)
            .join(CHARGE_BOX).on(CHARGE_BOX_CERTIFICATE_INSTALLED.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .and(CHARGE_BOX_CERTIFICATE_INSTALLED.CERTIFICATE_TYPE.eq(ocpp._2022._02.security.GetInstalledCertificateIds.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE.value()))
            .fetchOne(0, int.class);
        assertEquals(0, remaining);

        chargePoint.close();
    }

    @Test
    @Disabled("Pending implementation")
    public void test_TC_077_CSMS_PENDING() {
        log.info("Skipping TC_077_CSMS until scenario harness is expanded");
    }

    @Test
    public void test_TC_078_CSMS_InvalidCentralSystemCertificateSecurityEvent() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();

        var certificate = "-----BEGIN CERTIFICATE-----\\nINVALID-CSMS-ROOT-" + getRandomString() + "\\n-----END CERTIFICATE-----";
        var installParams = new InstallCertificateParams();
        installParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        installParams.setCertificateType(ocpp._2022._02.security.InstallCertificate.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE);
        installParams.setCertificate(certificate);

        var installFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.installCertificate(installParams).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp._2022._02.security.InstallCertificate()
                .withCertificateType(ocpp._2022._02.security.InstallCertificate.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE)
                .withCertificate(certificate),
            new ocpp._2022._02.security.InstallCertificateResponse()
                .withStatus(ocpp._2022._02.security.InstallCertificateResponse.InstallCertificateStatusEnumType.REJECTED)
        );
        assertEquals(ocpp._2022._02.security.InstallCertificateResponse.InstallCertificateStatusEnumType.REJECTED, installFuture.join());

        var securityEventResponse = chargePoint.send(
            new ocpp._2022._02.security.SecurityEventNotification()
                .withType("InvalidCentralSystemCertificate")
                .withTimestamp(DateTime.now())
                .withTechInfo("certificate rejected by charge point"),
            ocpp._2022._02.security.SecurityEventNotificationResponse.class
        );
        assertNotNull(securityEventResponse);

        chargePoint.close();
    }

    @Test
    public void test_TC_079_CSMS_GetSecurityLog() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var expectedJobId = dslContext.selectCount()
            .from(CHARGE_BOX_LOG_UPLOAD_JOB)
            .fetchOne(0, int.class) + 1;

        var params = new GetLogParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("https://example.com/security-log/upload");
        params.setLogType(ocpp._2022._02.security.GetLog.LogEnumType.SECURITY_LOG);
        params.setRetries(1);
        params.setRetryInterval(60);

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.getLog(params).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp._2022._02.security.GetLog()
                .withRequestId(expectedJobId)
                .withLogType(ocpp._2022._02.security.GetLog.LogEnumType.SECURITY_LOG)
                .withRetries(1)
                .withRetryInterval(60)
                .withLog(new ocpp._2022._02.security.LogParametersType().withRemoteLocation("https://example.com/security-log/upload")),
            new ocpp._2022._02.security.GetLogResponse()
                .withStatus(ocpp._2022._02.security.GetLogResponse.LogStatusEnumType.ACCEPTED)
                .withFilename("security.log")
        );
        assertEquals(ocpp._2022._02.security.GetLogResponse.LogStatusEnumType.ACCEPTED, future.join().getStatus());

        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.LogStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(ocpp._2022._02.security.LogStatusNotification.UploadLogStatusEnumType.UPLOADING),
            ocpp._2022._02.security.LogStatusNotificationResponse.class
        ));
        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.LogStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(ocpp._2022._02.security.LogStatusNotification.UploadLogStatusEnumType.UPLOADED),
            ocpp._2022._02.security.LogStatusNotificationResponse.class
        ));

        var eventCount = dslContext.selectCount()
            .from(CHARGE_BOX_LOG_UPLOAD_EVENT)
            .where(CHARGE_BOX_LOG_UPLOAD_EVENT.JOB_ID.eq(expectedJobId))
            .fetchOne(0, int.class);
        assertTrue(eventCount >= 2);

        chargePoint.close();
    }

    @Test
    public void test_TC_080_CSMS_SecureFirmwareUpdate() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var expectedJobId = dslContext.selectCount()
            .from(CHARGE_BOX_FIRMWARE_UPDATE_JOB)
            .fetchOne(0, int.class) + 1;

        var params = new SignedUpdateFirmwareParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("https://example.com/fw/secure.bin");
        params.setRetrieveDateTime(DateTime.now().plusMinutes(2));
        params.setInstallDateTime(DateTime.now().plusMinutes(7));
        params.setRetries(1);
        params.setRetryInterval(60);
        params.setSignature("valid-signature-" + getRandomString());
        params.setSigningCertificate("-----BEGIN CERTIFICATE-----\\nSIGNING-CERT-" + getRandomString() + "\\n-----END CERTIFICATE-----");

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.signedUpdateFirmware(params).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp._2022._02.security.SignedUpdateFirmware()
                .withRequestId(expectedJobId)
                .withRetries(1)
                .withRetryInterval(60)
                .withFirmware(new ocpp._2022._02.security.FirmwareType()
                    .withLocation("https://example.com/fw/secure.bin")
                    .withRetrieveDateTime(params.getRetrieveDateTime())
                    .withInstallDateTime(params.getInstallDateTime())
                    .withSignature(params.getSignature())
                    .withSigningCertificate(params.getSigningCertificate())),
            new ocpp._2022._02.security.SignedUpdateFirmwareResponse()
                .withStatus(ocpp._2022._02.security.SignedUpdateFirmwareResponse.UpdateFirmwareStatusEnumType.ACCEPTED)
        );
        assertEquals(ocpp._2022._02.security.SignedUpdateFirmwareResponse.UpdateFirmwareStatusEnumType.ACCEPTED, future.join());

        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.SignedFirmwareStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(ocpp._2022._02.security.SignedFirmwareStatusNotification.FirmwareStatusEnumType.DOWNLOADING),
            ocpp._2022._02.security.SignedFirmwareStatusNotificationResponse.class
        ));
        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.SignedFirmwareStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(ocpp._2022._02.security.SignedFirmwareStatusNotification.FirmwareStatusEnumType.DOWNLOADED),
            ocpp._2022._02.security.SignedFirmwareStatusNotificationResponse.class
        ));
        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.SignedFirmwareStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(ocpp._2022._02.security.SignedFirmwareStatusNotification.FirmwareStatusEnumType.SIGNATURE_VERIFIED),
            ocpp._2022._02.security.SignedFirmwareStatusNotificationResponse.class
        ));
        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.SignedFirmwareStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(ocpp._2022._02.security.SignedFirmwareStatusNotification.FirmwareStatusEnumType.INSTALLING),
            ocpp._2022._02.security.SignedFirmwareStatusNotificationResponse.class
        ));
        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.SignedFirmwareStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(ocpp._2022._02.security.SignedFirmwareStatusNotification.FirmwareStatusEnumType.INSTALL_REBOOTING),
            ocpp._2022._02.security.SignedFirmwareStatusNotificationResponse.class
        ));
        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.SecurityEventNotification().withType("FirmwareUpdated").withTimestamp(DateTime.now()),
            ocpp._2022._02.security.SecurityEventNotificationResponse.class
        ));
        assertNotNull(chargePoint.send(
            new StatusNotificationRequest()
                .withConnectorId(0)
                .withStatus(ChargePointStatus.AVAILABLE)
                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                .withTimestamp(DateTime.now()),
            StatusNotificationResponse.class
        ));
        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.SignedFirmwareStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(ocpp._2022._02.security.SignedFirmwareStatusNotification.FirmwareStatusEnumType.INSTALLED),
            ocpp._2022._02.security.SignedFirmwareStatusNotificationResponse.class
        ));

        var eventCount = dslContext.selectCount()
            .from(CHARGE_BOX_FIRMWARE_UPDATE_EVENT)
            .where(CHARGE_BOX_FIRMWARE_UPDATE_EVENT.JOB_ID.eq(expectedJobId))
            .fetchOne(0, int.class);
        assertTrue(eventCount >= 6);

        chargePoint.close();
    }

    @Test
    public void test_TC_081_CSMS_SecureFirmwareUpdateInvalidSignature() {
        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH).start();
        var expectedJobId = dslContext.selectCount()
            .from(CHARGE_BOX_FIRMWARE_UPDATE_JOB)
            .fetchOne(0, int.class) + 1;

        var params = new SignedUpdateFirmwareParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("https://example.com/fw/secure-invalid-signature.bin");
        params.setRetrieveDateTime(DateTime.now().plusMinutes(2));
        params.setInstallDateTime(DateTime.now().plusMinutes(7));
        params.setRetries(1);
        params.setRetryInterval(60);
        params.setSignature("invalid-signature-" + getRandomString());
        params.setSigningCertificate("-----BEGIN CERTIFICATE-----\\nSIGNING-CERT-" + getRandomString() + "\\n-----END CERTIFICATE-----");

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.signedUpdateFirmware(params).getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ocpp._2022._02.security.SignedUpdateFirmware()
                .withRequestId(expectedJobId)
                .withRetries(1)
                .withRetryInterval(60)
                .withFirmware(new ocpp._2022._02.security.FirmwareType()
                    .withLocation("https://example.com/fw/secure-invalid-signature.bin")
                    .withRetrieveDateTime(params.getRetrieveDateTime())
                    .withInstallDateTime(params.getInstallDateTime())
                    .withSignature(params.getSignature())
                    .withSigningCertificate(params.getSigningCertificate())),
            new ocpp._2022._02.security.SignedUpdateFirmwareResponse()
                .withStatus(ocpp._2022._02.security.SignedUpdateFirmwareResponse.UpdateFirmwareStatusEnumType.ACCEPTED)
        );
        assertEquals(ocpp._2022._02.security.SignedUpdateFirmwareResponse.UpdateFirmwareStatusEnumType.ACCEPTED, future.join());

        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.SignedFirmwareStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(ocpp._2022._02.security.SignedFirmwareStatusNotification.FirmwareStatusEnumType.DOWNLOADING),
            ocpp._2022._02.security.SignedFirmwareStatusNotificationResponse.class
        ));
        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.SignedFirmwareStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(ocpp._2022._02.security.SignedFirmwareStatusNotification.FirmwareStatusEnumType.DOWNLOADED),
            ocpp._2022._02.security.SignedFirmwareStatusNotificationResponse.class
        ));
        assertNotNull(chargePoint.send(
            new ocpp._2022._02.security.SignedFirmwareStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(ocpp._2022._02.security.SignedFirmwareStatusNotification.FirmwareStatusEnumType.INVALID_SIGNATURE),
            ocpp._2022._02.security.SignedFirmwareStatusNotificationResponse.class
        ));

        chargePoint.close();
    }

    @Test
    public void test_TC_083_CSMS_UpgradeChargePointSecurityProfileAccepted() {
        String password = "0123456789abcdef0123456789abcdef";

        dslContext.update(CHARGE_BOX)
            .set(CHARGE_BOX.SECURITY_PROFILE, 1)
            .set(CHARGE_BOX.AUTH_PASSWORD, passwordEncoder.encode(password))
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .execute();

        var chargePoint = new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH, password).start();

        expectGetConfCpoName(chargePoint);

        var changeConfig = new ChangeConfigurationParams();
        changeConfig.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        changeConfig.setKeyType(ChangeConfigurationParams.ConfigurationKeyType.PREDEFINED);
        changeConfig.setConfKey(SecurityProfile.name());
        changeConfig.setValue("2");

        var changeConfigFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.changeConfiguration(changeConfig)
                    .getSuccessResponsesByChargeBoxId()
                    .get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ChangeConfigurationRequest()
                .withKey(SecurityProfile.name())
                .withValue("2"),
            new ChangeConfigurationResponse().withStatus(ConfigurationStatus.ACCEPTED)
        );
        assertEquals(ConfigurationStatus.ACCEPTED, changeConfigFuture.join());

        var reset = new ResetParams();
        reset.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        reset.setResetType(ResetType.HARD);

        var resetFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return operationsService.reset(reset)
                    .getSuccessResponsesByChargeBoxId()
                    .get(REGISTERED_CHARGE_BOX_ID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        chargePoint.expectRequest(
            new ResetRequest().withType(ResetType.HARD),
            new ResetResponse().withStatus(ResetStatus.ACCEPTED)
        );
        assertEquals(ResetStatus.ACCEPTED, resetFuture.join());

        // disconnect and reconnect
        chargePoint.close();
        chargePoint.start();

        expectGetConfCpoName(chargePoint);

        var boot = chargePoint.send(
            new BootNotificationRequest()
                .withChargePointVendor(getRandomString())
                .withChargePointModel(getRandomString()),
            BootNotificationResponse.class
        );
        assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());

        assertNotNull(chargePoint.send(
            new StatusNotificationRequest()
                .withConnectorId(0)
                .withStatus(ChargePointStatus.AVAILABLE)
                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                .withTimestamp(DateTime.now()),
            StatusNotificationResponse.class
        ));

        chargePoint.close();
    }

    @Test
    public void test_TC_085_CSMS_BasicAuthenticationValidCredentials() {
        String password = "0123456789abcdef0123456789abcdef";

        dslContext.update(CHARGE_BOX)
            .set(CHARGE_BOX.SECURITY_PROFILE, 1)
            .set(CHARGE_BOX.AUTH_PASSWORD, passwordEncoder.encode(password))
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .execute();

        var chargePoint = new OcppJsonChargePoint(
            OcppVersion.V_16,
            REGISTERED_CHARGE_BOX_ID,
            PATH,
            password
        ).start();

        expectGetConfCpoName(chargePoint);

        var boot = chargePoint.send(
            new BootNotificationRequest().withChargePointVendor(getRandomString()).withChargePointModel(getRandomString()),
            BootNotificationResponse.class
        );
        assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());
        assertNotNull(chargePoint.send(
            new StatusNotificationRequest()
                .withConnectorId(0)
                .withStatus(ChargePointStatus.AVAILABLE)
                .withErrorCode(ChargePointErrorCode.NO_ERROR)
                .withTimestamp(DateTime.now()),
            StatusNotificationResponse.class
        ));

        chargePoint.close();
    }

    @Test
    @Disabled("Pending implementation")
    public void test_TC_086_CSMS_PENDING() {
        log.info("Skipping TC_086_CSMS until scenario harness is expanded");
    }

    @Test
    @Disabled("Pending implementation")
    public void test_TC_087_CSMS_PENDING() {
        log.info("Skipping TC_087_CSMS until scenario harness is expanded");
    }

    @Test
    public void test_TC_088_CSMS_WebSocketSubprotocolNegotiation() {
        assertThrows(
            RuntimeException.class,
            () -> new OcppJsonChargePoint(List.of("ocpp0.1"), REGISTERED_CHARGE_BOX_ID, PATH, null).start()
        );

        var chargePoint = new OcppJsonChargePoint(List.of("ocpp0.1", "ocpp1.6"), REGISTERED_CHARGE_BOX_ID, PATH, null).start();

        List<String> responseHeaders = chargePoint.getResponseHeader("Sec-WebSocket-Protocol");
        assertNotNull(responseHeaders);
        assertTrue(responseHeaders.contains("ocpp1.6"));

        var boot = chargePoint.send(
            new BootNotificationRequest().withChargePointVendor(getRandomString()).withChargePointModel(getRandomString()),
            BootNotificationResponse.class
        );
        assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());
        chargePoint.close();
    }

    /**
     * SteVe started asking for this configuration after each connection. So, we need to anticipate this request
     * in our test flows, since they are strict.
     */
    private static void expectGetConfCpoName(OcppJsonChargePoint chargePoint) {
        KeyValue kv = new KeyValue()
            .withKey(CpoName.name())
            .withValue("SteVe-CPO")
            .withReadonly(false);

        chargePoint.expectRequest(
            new GetConfigurationRequest().withKey(CpoName.name()),
            new GetConfigurationResponse().withConfigurationKey(List.of(kv))
        );
    }

}
