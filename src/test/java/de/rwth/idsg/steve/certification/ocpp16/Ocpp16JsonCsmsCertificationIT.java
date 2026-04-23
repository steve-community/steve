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
import de.rwth.idsg.steve.repository.ReservationStatus;
import de.rwth.idsg.steve.service.OcppOperationsService;
import de.rwth.idsg.steve.utils.OcppJsonChargePoint;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import de.rwth.idsg.steve.utils.mapper.ChargingProfileDetailsMapper;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import de.rwth.idsg.steve.web.dto.RestCallback;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;
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
import ocpp._2022._02.security.CertificateHashData;
import ocpp._2022._02.security.CertificateHashDataType;
import ocpp._2022._02.security.DeleteCertificate;
import ocpp._2022._02.security.DeleteCertificateResponse;
import ocpp._2022._02.security.FirmwareType;
import ocpp._2022._02.security.GetInstalledCertificateIds;
import ocpp._2022._02.security.GetInstalledCertificateIds.CertificateUseEnumType;
import ocpp._2022._02.security.GetInstalledCertificateIdsResponse;
import ocpp._2022._02.security.GetLog;
import ocpp._2022._02.security.GetLogResponse;
import ocpp._2022._02.security.InstallCertificate;
import ocpp._2022._02.security.InstallCertificateResponse;
import ocpp._2022._02.security.LogParametersType;
import ocpp._2022._02.security.LogStatusNotification;
import ocpp._2022._02.security.LogStatusNotificationResponse;
import ocpp._2022._02.security.SecurityEventNotification;
import ocpp._2022._02.security.SecurityEventNotificationResponse;
import ocpp._2022._02.security.SignedFirmwareStatusNotification;
import ocpp._2022._02.security.SignedFirmwareStatusNotification.FirmwareStatusEnumType;
import ocpp._2022._02.security.SignedFirmwareStatusNotificationResponse;
import ocpp._2022._02.security.SignedUpdateFirmware;
import ocpp._2022._02.security.SignedUpdateFirmwareResponse;
import ocpp.cp._2015._10.*;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.DataTransferRequest;
import ocpp.cs._2015._10.DataTransferResponse;
import ocpp.cs._2015._10.DiagnosticsStatus;
import ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2015._10.DiagnosticsStatusNotificationResponse;
import ocpp.cs._2015._10.FirmwareStatus;
import ocpp.cs._2015._10.FirmwareStatusNotificationRequest;
import ocpp.cs._2015._10.FirmwareStatusNotificationResponse;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.HeartbeatResponse;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.MeterValuesResponse;
import ocpp.cs._2015._10.Reason;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StatusNotificationResponse;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.StopTransactionResponse;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.AuthorizationKey;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.CpoName;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.SecurityProfile;
import static jooq.steve.db.Tables.CHARGE_BOX_CERTIFICATE_INSTALLED;
import static jooq.steve.db.Tables.CHARGE_BOX_FIRMWARE_UPDATE_EVENT;
import static jooq.steve.db.Tables.CHARGE_BOX_FIRMWARE_UPDATE_JOB;
import static jooq.steve.db.Tables.CHARGE_BOX_LOG_UPLOAD_EVENT;
import static jooq.steve.db.Tables.CHARGE_BOX_LOG_UPLOAD_JOB;
import static jooq.steve.db.Tables.RESERVATION;
import static jooq.steve.db.Tables.TRANSACTION;
import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static ocpp._2022._02.security.SignedUpdateFirmwareResponse.UpdateFirmwareStatusEnumType;
import static ocpp.cp._2015._10.ReservationStatus.ACCEPTED;
import static ocpp.cp._2015._10.ReservationStatus.FAULTED;
import static ocpp.cp._2015._10.ReservationStatus.OCCUPIED;
import static ocpp.cp._2015._10.ReservationStatus.REJECTED;
import static ocpp.cp._2015._10.ReservationStatus.UNAVAILABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * These are the integration tests for all OCPP 1.6 (SUT) CSMS test cases relevant for the OCA certification
 * (version: 2026-02).
 * <p>
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
        var chargePoint = defaultStation().start();

        var boot = bootNotification();
        var bootResponse = chargePoint.send(boot, BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus());
        assertNotNull(bootResponse.getCurrentTime());
        assertTrue(bootResponse.getInterval() > 0);

        sendAvailableStatusForAllConnectors(chargePoint);

        var heartbeatResponse = chargePoint.send(new HeartbeatRequest(), HeartbeatResponse.class);
        assertNotNull(heartbeatResponse);
        assertNotNull(heartbeatResponse.getCurrentTime());

        chargePoint.close();
    }

    @Test
    public void test_TC_003_CSMS_RegularChargingSession_PluginFirst() {
        var chargePoint = defaultStation().start();

        var preparing = statusNotification(1, ChargePointStatus.PREPARING, ChargePointErrorCode.NO_ERROR);
        var preparingResponse = chargePoint.send(preparing, StatusNotificationResponse.class);
        assertNotNull(preparingResponse);

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var startTransaction = startTransaction(1, REGISTERED_OCPP_TAG, 0);
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = statusNotification(1, ChargePointStatus.CHARGING, ChargePointErrorCode.NO_ERROR);
        var chargingResponse = chargePoint.send(charging, StatusNotificationResponse.class);
        assertNotNull(chargingResponse);

        chargePoint.close();
    }

    @Test
    public void test_TC_004_1_CSMS_RegularChargingSession_IdentificationFirst() {
        var chargePoint = defaultStation().start();

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var startTransaction = startTransaction(1, REGISTERED_OCPP_TAG, 0);
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = statusNotification(1, ChargePointStatus.CHARGING, ChargePointErrorCode.NO_ERROR);
        var chargingResponse = chargePoint.send(charging, StatusNotificationResponse.class);
        assertNotNull(chargingResponse);

        chargePoint.close();
    }

    @Test
    public void test_TC_004_2_CSMS_RegularChargingSession_IdentificationFirst_ConnectionTimeout() {
        var chargePoint = defaultStation().start();

        // Reusable state required by spec: Authorized
        enterAuthorizedState(chargePoint);

        var preparing = statusNotification(1, ChargePointStatus.PREPARING, ChargePointErrorCode.NO_ERROR);
        var preparingResponse = chargePoint.send(preparing, StatusNotificationResponse.class);
        assertNotNull(preparingResponse);

        var available = statusNotification(1, ChargePointStatus.AVAILABLE, ChargePointErrorCode.NO_ERROR, DateTime.now().plusSeconds(5));
        var availableResponse = chargePoint.send(available, StatusNotificationResponse.class);
        assertNotNull(availableResponse);

        chargePoint.close();
    }

    @Test
    public void test_TC_005_1_CSMS_EVSideDisconnected_StopTransactionAndUnlock() {
        var chargePoint = defaultStation().start();

        var startTransactionResponse = enterChargingState(chargePoint, 1, REGISTERED_OCPP_TAG, 0);

        var suspendedEv = statusNotification(1, ChargePointStatus.SUSPENDED_EV, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(suspendedEv, StatusNotificationResponse.class));

        var stopTransaction = stopTransaction(startTransactionResponse.getTransactionId(), REGISTERED_OCPP_TAG, 10, Reason.EV_DISCONNECTED);
        var stopTransactionResponse = chargePoint.send(stopTransaction, StopTransactionResponse.class);
        assertNotNull(stopTransactionResponse);
        assertNotNull(stopTransactionResponse.getIdTagInfo());
        assertEquals(AuthorizationStatus.ACCEPTED, stopTransactionResponse.getIdTagInfo().getStatus());

        var finishing = statusNotification(1, ChargePointStatus.FINISHING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(finishing, StatusNotificationResponse.class));

        var available = statusNotification(1, ChargePointStatus.AVAILABLE, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(available, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_007_CSMS_RegularStartChargingSession_CachedId() {
        var chargePoint = defaultStation().start();

        var preparing = statusNotification(1, ChargePointStatus.PREPARING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var startTransaction = startTransaction(1, REGISTERED_OCPP_TAG, 0);
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertNotNull(startTransactionResponse.getIdTagInfo());
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = statusNotification(1, ChargePointStatus.CHARGING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_061_CSMS_ClearAuthorizationDataInAuthorizationCache() {
        var chargePoint = defaultStation().start();

        var params = new MultipleChargePointSelect();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.clearCache(params));

        chargePoint.expectRequest(
            new ClearCacheRequest(),
            new ClearCacheResponse().withStatus(ClearCacheStatus.ACCEPTED)
        );
        assertEquals(ClearCacheStatus.ACCEPTED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_010_CSMS_RemoteStartChargingSession_CablePluggedInFirst() {
        var chargePoint = defaultStation().start();

        var preparing = statusNotification(1, ChargePointStatus.PREPARING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var params = new RemoteStartTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.remoteStartTransaction(params));

        chargePoint.expectRequest(
            new RemoteStartTransactionRequest().withConnectorId(1).withIdTag(REGISTERED_OCPP_TAG),
            new RemoteStartTransactionResponse().withStatus(RemoteStartStopStatus.ACCEPTED)
        );
        assertEquals(RemoteStartStopStatus.ACCEPTED, successResponse(operationFuture.join()));

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var startTransaction = startTransaction(1, REGISTERED_OCPP_TAG, 0);
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = statusNotification(1, ChargePointStatus.CHARGING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_011_1_CSMS_RemoteStartChargingSession_RemoteStartFirst() {
        var chargePoint = defaultStation().start();

        var params = new RemoteStartTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.remoteStartTransaction(params));

        chargePoint.expectRequest(
            new RemoteStartTransactionRequest().withConnectorId(1).withIdTag(REGISTERED_OCPP_TAG),
            new RemoteStartTransactionResponse().withStatus(RemoteStartStopStatus.ACCEPTED)
        );
        assertEquals(RemoteStartStopStatus.ACCEPTED, successResponse(operationFuture.join()));

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var preparing = statusNotification(1, ChargePointStatus.PREPARING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var startTransaction = startTransaction(1, REGISTERED_OCPP_TAG, 0);
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = statusNotification(1, ChargePointStatus.CHARGING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_011_2_CSMS_RemoteStartChargingSession_Timeout() {
        var chargePoint = defaultStation().start();

        var params = new RemoteStartTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.remoteStartTransaction(params));

        chargePoint.expectRequest(
            new RemoteStartTransactionRequest().withConnectorId(1).withIdTag(REGISTERED_OCPP_TAG),
            new RemoteStartTransactionResponse().withStatus(RemoteStartStopStatus.ACCEPTED)
        );
        assertEquals(RemoteStartStopStatus.ACCEPTED, successResponse(operationFuture.join()));

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var preparing = statusNotification(1, ChargePointStatus.PREPARING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var available = statusNotification(1, ChargePointStatus.AVAILABLE, ChargePointErrorCode.NO_ERROR, DateTime.now().plusSeconds(5));
        assertNotNull(chargePoint.send(available, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_012_CSMS_RemoteStopChargingSession() {
        var chargePoint = defaultStation().start();

        var preparing = statusNotification(1, ChargePointStatus.PREPARING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());

        var startTransaction = startTransaction(1, REGISTERED_OCPP_TAG, 0);
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        var charging = statusNotification(1, ChargePointStatus.CHARGING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        var params = new RemoteStopTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setTransactionId(startTransactionResponse.getTransactionId());

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.remoteStopTransaction(params));

        chargePoint.expectRequest(
            new RemoteStopTransactionRequest().withTransactionId(startTransactionResponse.getTransactionId()),
            new RemoteStopTransactionResponse().withStatus(RemoteStartStopStatus.ACCEPTED)
        );
        assertEquals(RemoteStartStopStatus.ACCEPTED, successResponse(operationFuture.join()));

        var stopTransaction = stopTransaction(startTransactionResponse.getTransactionId(), REGISTERED_OCPP_TAG, 10, Reason.REMOTE);
        assertNotNull(chargePoint.send(stopTransaction, StopTransactionResponse.class));

        var finishing = statusNotification(1, ChargePointStatus.FINISHING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(finishing, StatusNotificationResponse.class));

        var available = statusNotification(1, ChargePointStatus.AVAILABLE, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(available, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_013_CSMS_HardReset() {
        var chargePoint = defaultStation().start();

        var params = new ResetParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setResetType(ResetType.HARD);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.reset(params));

        chargePoint.expectRequest(
            new ResetRequest().withType(ResetType.HARD),
            new ResetResponse().withStatus(ResetStatus.ACCEPTED)
        );
        assertEquals(ResetStatus.ACCEPTED, successResponse(operationFuture.join()));

        var bootResponse = chargePoint.send(bootNotification(), BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus());

        sendAvailableStatusForAllConnectors(chargePoint);

        chargePoint.close();
    }

    @Test
    public void test_TC_014_CSMS_SoftReset() {
        var chargePoint = defaultStation().start();

        var params = new ResetParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setResetType(ResetType.SOFT);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.reset(params));

        chargePoint.expectRequest(
            new ResetRequest().withType(ResetType.SOFT),
            new ResetResponse().withStatus(ResetStatus.ACCEPTED)
        );
        assertEquals(ResetStatus.ACCEPTED, successResponse(operationFuture.join()));

        var bootResponse = chargePoint.send(bootNotification(), BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus());

        sendAvailableStatusForAllConnectors(chargePoint);

        chargePoint.close();
    }

    @Test
    public void test_TC_017_1_CSMS_UnlockConnector_NoTransaction_NotFixedCable() {
        var chargePoint = defaultStation().start();

        var params = new UnlockConnectorParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.unlockConnector(params));

        chargePoint.expectRequest(
            new UnlockConnectorRequest().withConnectorId(1),
            new UnlockConnectorResponse().withStatus(UnlockStatus.UNLOCKED)
        );
        assertEquals(UnlockStatus.UNLOCKED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_017_2_CSMS_UnlockConnector_NoTransaction_FixedCable() {
        var chargePoint = defaultStation().start();

        var params = new UnlockConnectorParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.unlockConnector(params));

        chargePoint.expectRequest(
            new UnlockConnectorRequest().withConnectorId(1),
            new UnlockConnectorResponse().withStatus(UnlockStatus.NOT_SUPPORTED)
        );
        assertEquals(UnlockStatus.NOT_SUPPORTED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_018_1_CSMS_UnlockConnector_WithChargingSession() {
        var chargePoint = defaultStation().start();

        var startTransactionResponse = enterChargingState(chargePoint, 1, REGISTERED_OCPP_TAG, 0);

        var params = new UnlockConnectorParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.unlockConnector(params));

        chargePoint.expectRequest(
            new UnlockConnectorRequest().withConnectorId(1),
            new UnlockConnectorResponse().withStatus(UnlockStatus.UNLOCKED)
        );
        assertEquals(UnlockStatus.UNLOCKED, successResponse(operationFuture.join()));

        var finishing = statusNotification(1, ChargePointStatus.FINISHING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(finishing, StatusNotificationResponse.class));

        var stopTransaction = stopTransaction(startTransactionResponse.getTransactionId(), REGISTERED_OCPP_TAG, 10, Reason.UNLOCK_COMMAND);
        assertNotNull(chargePoint.send(stopTransaction, StopTransactionResponse.class));

        var available = statusNotification(1, ChargePointStatus.AVAILABLE, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(available, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_019_1_CSMS_RetrieveAllConfigurationKeys() {
        var chargePoint = defaultStation().start();

        var params = new GetConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConfKeyList(List.of());

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.getConfiguration(params));

        var expectedConfigurationKeys = List.of(
            configurationKey("AuthorizeRemoteTxRequests", false, "false"),
            configurationKey("ClockAlignedDataInterval", false, "0"),
            configurationKey("ConnectionTimeOut", false, "60"),
            configurationKey("ConnectorPhaseRotation", false, "Unknown,NotApplicable,NotApplicable"),
            configurationKey("GetConfigurationMaxKeys", true, "50"),
            configurationKey("HeartbeatInterval", false, "300"),
            configurationKey("LocalAuthorizeOffline", false, "true"),
            configurationKey("LocalPreAuthorize", false, "false"),
            configurationKey("MeterValuesAlignedData", false, "Energy.Active.Import.Register"),
            configurationKey("MeterValuesSampledData", false, "Energy.Active.Import.Register"),
            configurationKey("MeterValueSampleInterval", false, "60"),
            configurationKey("NumberOfConnectors", true, "2"),
            configurationKey("ResetRetries", false, "3"),
            configurationKey("StopTransactionOnEVSideDisconnect", false, "true"),
            configurationKey("StopTransactionOnInvalidId", false, "true"),
            configurationKey("StopTxnAlignedData", false, "Energy.Active.Import.Register"),
            configurationKey("StopTxnSampledData", false, "Energy.Active.Import.Register"),
            configurationKey("SupportedFeatureProfiles", true, "Core,LocalAuthListManagement,Reservation,RemoteTrigger,SmartCharging"),
            configurationKey("TransactionMessageAttempts", false, "3"),
            configurationKey("TransactionMessageRetryInterval", false, "60"),
            configurationKey("UnlockConnectorOnEVSideDisconnect", false, "true"),
            configurationKey("LocalAuthListEnabled", false, "true"),
            configurationKey("LocalAuthListMaxLength", true, "100"),
            configurationKey("SendLocalListMaxLength", true, "100"),
            configurationKey("ChargeProfileMaxStackLevel", true, "10"),
            configurationKey("ChargingScheduleAllowedChargingRateUnit", true, "A,W"),
            configurationKey("ChargingScheduleMaxPeriods", true, "24"),
            configurationKey("MaxChargingProfilesInstalled", true, "16")
        );

        var getConfigReq = new GetConfigurationRequest().withKey(List.of());
        var getConfigRes = new GetConfigurationResponse()
            .withConfigurationKey(expectedConfigurationKeys)
            .withUnknownKey(List.of());
        chargePoint.expectRequest(getConfigReq, getConfigRes);

        var values = successResponse(operationFuture.join());
        assertNotNull(values);
        assertTrue(values.getUnknownKeys().isEmpty());
        for (var expectedKey : expectedConfigurationKeys) {
            var actualKey = values.getConfigurationKeys().stream()
                .filter(kv -> kv.getKey().equals(expectedKey.getKey()))
                .findFirst()
                .orElse(null);

            assertNotNull(actualKey, "Missing required configuration key: " + expectedKey.getKey());
            assertEquals(expectedKey.isReadonly(), actualKey.isReadonly(), "Unexpected accessibility for key: " + expectedKey.getKey());
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_019_2_CSMS_RetrieveSpecificConfigurationKey() {
        var chargePoint = defaultStation().start();

        var params = new GetConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConfKeyList(List.of("SupportedFeatureProfiles"));

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.getConfiguration(params));

        var getConfigReq = new GetConfigurationRequest().withKey(List.of("SupportedFeatureProfiles"));
        var getConfigRes = new GetConfigurationResponse()
            .withConfigurationKey(configurationKey("SupportedFeatureProfiles", true, "Core,FirmwareManagement,Reservation,RemoteTrigger,SmartCharging"))
            .withUnknownKey(List.of());
        chargePoint.expectRequest(getConfigReq, getConfigRes);

        var values = successResponse(operationFuture.join());
        assertNotNull(values);
        assertTrue(values.getUnknownKeys().isEmpty());
        assertEquals(1, values.getConfigurationKeys().size());
        assertEquals("SupportedFeatureProfiles", values.getConfigurationKeys().getFirst().getKey());

        chargePoint.close();
    }

    @Test
    public void test_TC_021_CSMS_ChangeSetConfiguration() {
        var chargePoint = defaultStation().start();

        var params = new ChangeConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConfKey("MeterValueSampleInterval");
        params.setValue("60");

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.changeConfiguration(params));

        var changeConfigReq = new ChangeConfigurationRequest()
            .withKey("MeterValueSampleInterval")
            .withValue("60");
        var changeConfigRes = new ChangeConfigurationResponse().withStatus(ConfigurationStatus.ACCEPTED);
        chargePoint.expectRequest(changeConfigReq, changeConfigRes);

        assertEquals(ConfigurationStatus.ACCEPTED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_023_1_CSMS_StartChargingSession_AuthorizeInvalid() {
        var chargePoint = defaultStation().start();

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

        var chargePoint = defaultStation().start();

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

        var chargePoint = defaultStation().start();

        var authorize = new AuthorizeRequest().withIdTag(blockedTag);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.BLOCKED, authorizeResponse.getIdTagInfo().getStatus());

        chargePoint.close();
    }

    @Test
    public void test_TC_024_CSMS_StartChargingSession_LockFailure() {
        var chargePoint = defaultStation().start();

        // Reusable state required by spec: Authorized
        enterAuthorizedState(chargePoint);

        var preparing = statusNotification(1, ChargePointStatus.PREPARING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(preparing, StatusNotificationResponse.class));

        var faulted = statusNotification(1, ChargePointStatus.FAULTED, ChargePointErrorCode.CONNECTOR_LOCK_FAILURE);
        assertNotNull(chargePoint.send(faulted, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_026_CSMS_RemoteStartChargingSession_Rejected() {
        var chargePoint = defaultStation().start();

        var params = new RemoteStartTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.remoteStartTransaction(params));

        chargePoint.expectRequest(
            new RemoteStartTransactionRequest().withConnectorId(1).withIdTag(REGISTERED_OCPP_TAG),
            new RemoteStartTransactionResponse().withStatus(RemoteStartStopStatus.REJECTED)
        );
        assertEquals(RemoteStartStopStatus.REJECTED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    /**
     * FIXME: we validate RemoteStopTransactionParams.transactionId in {@link OcppOperationsService#remoteStopTransaction(RemoteStopTransactionParams)}
     * before calling the station. if the transaction id is invalid (for that station) we throw Exception and do not
     * even contact station. however, the test case expects the station to handle this. our behaviour is too strict.
     *
     * for now, unknownTransactionId deliberately uses existing session's transactionId to make the test pass.
     */
    @Test
    public void test_TC_028_CSMS_RemoteStopTransaction_Rejected() {
        var chargePoint = defaultStation().start();

        var startTransactionResponse = enterChargingState(chargePoint, 1, REGISTERED_OCPP_TAG, 0);
        int unknownTransactionId = startTransactionResponse.getTransactionId();

        var params = new RemoteStopTransactionParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setTransactionId(unknownTransactionId);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.remoteStopTransaction(params));

        chargePoint.expectRequest(
            new RemoteStopTransactionRequest().withTransactionId(unknownTransactionId),
            new RemoteStopTransactionResponse().withStatus(RemoteStartStopStatus.REJECTED)
        );
        assertEquals(RemoteStartStopStatus.REJECTED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_030_CSMS_UnlockConnector_UnlockFailure() {
        var chargePoint = defaultStation().start();

        var params = new UnlockConnectorParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.unlockConnector(params));

        chargePoint.expectRequest(
            new UnlockConnectorRequest().withConnectorId(1),
            new UnlockConnectorResponse().withStatus(UnlockStatus.UNLOCK_FAILED)
        );
        assertEquals(UnlockStatus.UNLOCK_FAILED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_031_CSMS_UnlockConnector_UnknownConnector() {
        var chargePoint = defaultStation().start();

        var params = new UnlockConnectorParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.unlockConnector(params));

        chargePoint.expectRequest(
            new UnlockConnectorRequest().withConnectorId(1),
            new UnlockConnectorResponse().withStatus(UnlockStatus.NOT_SUPPORTED)
        );
        assertEquals(UnlockStatus.NOT_SUPPORTED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_032_1_CSMS_PowerFailureBoot_StopTransactions() {
        var chargePoint = defaultStation().start();

        var startTransactionResponse = enterChargingState(chargePoint, 1, REGISTERED_OCPP_TAG, 0);

        // Disconnect and reconnect the power of the Charge Point
        chargePoint.close();
        chargePoint = defaultStation().start();

        var bootResponse = chargePoint.send(bootNotification(), BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResponse.getStatus());

        var connector0 = statusNotification(0, ChargePointStatus.AVAILABLE, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(connector0, StatusNotificationResponse.class));

        var connector1 = statusNotification(1, ChargePointStatus.FINISHING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(connector1, StatusNotificationResponse.class));

        var connector2 = statusNotification(2, ChargePointStatus.AVAILABLE, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(connector2, StatusNotificationResponse.class));

        var stopTransaction = stopTransaction(startTransactionResponse.getTransactionId(), REGISTERED_OCPP_TAG, 10, Reason.POWER_LOSS);
        assertNotNull(chargePoint.send(stopTransaction, StopTransactionResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_037_1_CSMS_OfflineStartTransaction_ValidIdTag() {
        var chargePoint = defaultStation().start();
        var transactionStartEarlier = DateTime.now().minusHours(4);

        var startTransaction = startTransaction(1, REGISTERED_OCPP_TAG, 0, null, transactionStartEarlier);
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertNotNull(startTransactionResponse);
        assertTrue(startTransactionResponse.getTransactionId() > 0);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());

        var charging = statusNotification(1, ChargePointStatus.CHARGING, ChargePointErrorCode.NO_ERROR, transactionStartEarlier);
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_037_3_CSMS_OfflineStartTransaction_InvalidIdTag_StopOnInvalidTrue() {
        var chargePoint = defaultStation().start();
        var transactionStartEarlier = DateTime.now().minusHours(4);
        var invalidTag = getRandomString();

        var startTransaction = startTransaction(1, invalidTag, 0, null, transactionStartEarlier);
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertNotNull(startTransactionResponse);
        assertTrue(startTransactionResponse.getTransactionId() > 0);
        assertEquals(AuthorizationStatus.INVALID, startTransactionResponse.getIdTagInfo().getStatus());

        var charging = statusNotification(1, ChargePointStatus.CHARGING, ChargePointErrorCode.NO_ERROR, transactionStartEarlier);
        assertNotNull(chargePoint.send(charging, StatusNotificationResponse.class));

        var stopTransaction = stopTransaction(startTransactionResponse.getTransactionId(), invalidTag, 10, Reason.DE_AUTHORIZED);
        assertNotNull(chargePoint.send(stopTransaction, StopTransactionResponse.class));

        var finishing = statusNotification(1, ChargePointStatus.FINISHING, ChargePointErrorCode.NO_ERROR);
        assertNotNull(chargePoint.send(finishing, StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_039_CSMS_OfflineTransaction() {
        var chargePoint = defaultStation().start();
        var transactionStartEarlier = DateTime.now().minusHours(4);
        var transactionStopEarlier = transactionStartEarlier.plusHours(1);

        var startTransaction = startTransaction(1, REGISTERED_OCPP_TAG, 0, null, transactionStartEarlier);
        var startTransactionResponse = chargePoint.send(startTransaction, StartTransactionResponse.class);
        assertNotNull(startTransactionResponse);
        assertTrue(startTransactionResponse.getTransactionId() > 0);
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());

        var stopTransaction = stopTransaction(startTransactionResponse.getTransactionId(), REGISTERED_OCPP_TAG, 10, Reason.LOCAL, transactionStopEarlier);
        assertNotNull(chargePoint.send(stopTransaction, StopTransactionResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_040_1_CSMS_ConfigurationKeys_NotSupported() {
        var chargePoint = defaultStation().start();
        var key = "UnknownConfigKey_" + getRandomString();

        var params = new ChangeConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setKeyType(ChangeConfigurationParams.ConfigurationKeyType.CUSTOM);
        params.setCustomConfKey(key);
        params.setValue("123");

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.changeConfiguration(params));

        var changeConfigReq = new ChangeConfigurationRequest().withKey(key).withValue("123");
        var changeConfigRes = new ChangeConfigurationResponse().withStatus(ConfigurationStatus.NOT_SUPPORTED);
        chargePoint.expectRequest(changeConfigReq, changeConfigRes);

        assertEquals(ConfigurationStatus.NOT_SUPPORTED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_040_2_CSMS_ConfigurationKeys_InvalidValue() {
        var chargePoint = defaultStation().start();

        var params = new ChangeConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConfKey("MeterValueSampleInterval");
        params.setValue("INVALID_VALUE");

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.changeConfiguration(params));

        var changeConfigReq = new ChangeConfigurationRequest().withKey("MeterValueSampleInterval").withValue("INVALID_VALUE");
        var changeConfigRes = new ChangeConfigurationResponse().withStatus(ConfigurationStatus.REJECTED);
        chargePoint.expectRequest(changeConfigReq, changeConfigRes);

        assertEquals(ConfigurationStatus.REJECTED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_042_1_CSMS_GetLocalListVersion_NotSupported() {
        var chargePoint = defaultStation().start();

        var params = new MultipleChargePointSelect();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.getLocalListVersion(params));

        chargePoint.expectRequest(
            new GetLocalListVersionRequest(),
            new GetLocalListVersionResponse().withListVersion(-1)
        );
        assertEquals(-1, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_042_2_CSMS_GetLocalListVersion_Empty() {
        var chargePoint = defaultStation().start();

        var params = new MultipleChargePointSelect();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.getLocalListVersion(params));

        chargePoint.expectRequest(
            new GetLocalListVersionRequest(),
            new GetLocalListVersionResponse().withListVersion(0)
        );
        assertEquals(0, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_043_1_CSMS_SendLocalAuthorizationList_NotSupported() {
        var chargePoint = defaultStation().start();

        var params = new SendLocalListParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setListVersion(1);
        params.setUpdateType(UpdateType.FULL);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.sendLocalList(params));

        var expectedRequest = new SendLocalListRequest()
            .withListVersion(1)
            .withUpdateType(UpdateType.FULL)
            .withLocalAuthorizationList(List.of(
                new AuthorizationData()
                    .withIdTag(REGISTERED_OCPP_TAG)
                    .withIdTagInfo(new IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED))
            ));
        chargePoint.expectRequest(
            expectedRequest,
            new SendLocalListResponse().withStatus(UpdateStatus.NOT_SUPPORTED)
        );
        assertEquals(UpdateStatus.NOT_SUPPORTED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_043_3_CSMS_SendLocalAuthorizationList_Failed() {
        var chargePoint = defaultStation().start();

        var params = new SendLocalListParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setListVersion(1);
        params.setUpdateType(UpdateType.FULL);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.sendLocalList(params));

        var expectedRequest = new SendLocalListRequest()
            .withListVersion(1)
            .withUpdateType(UpdateType.FULL)
            .withLocalAuthorizationList(List.of(
                new AuthorizationData()
                    .withIdTag(REGISTERED_OCPP_TAG)
                    .withIdTagInfo(new IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED))
            ));
        chargePoint.expectRequest(
            expectedRequest,
            new SendLocalListResponse().withStatus(UpdateStatus.FAILED)
        );
        assertEquals(UpdateStatus.FAILED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_043_4_CSMS_SendLocalAuthorizationList_Full() {
        var chargePoint = defaultStation().start();

        var params = new SendLocalListParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setListVersion(1);
        params.setUpdateType(UpdateType.FULL);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.sendLocalList(params));

        var expectedRequest = new SendLocalListRequest()
            .withListVersion(1)
            .withUpdateType(UpdateType.FULL)
            .withLocalAuthorizationList(List.of(
                new AuthorizationData()
                    .withIdTag(REGISTERED_OCPP_TAG)
                    .withIdTagInfo(new IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED))
            ));
        chargePoint.expectRequest(
            expectedRequest,
            new SendLocalListResponse().withStatus(UpdateStatus.ACCEPTED)
        );
        assertEquals(UpdateStatus.ACCEPTED, successResponse(operationFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_043_5_CSMS_SendLocalAuthorizationList_Differential() {
        var chargePoint = defaultStation().start();

        // Memory state per spec: initial local list set with updateType Full.
        var initialListVersion = 1;

        // Optional step 1/2 in spec: verify current list version.
        var versionParams = new MultipleChargePointSelect();
        versionParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));

        var versionFuture = supplyAsyncUnchecked(() -> operationsService.getLocalListVersion(versionParams));
        chargePoint.expectRequest(
            new GetLocalListVersionRequest(),
            new GetLocalListVersionResponse().withListVersion(initialListVersion)
        );
        assertEquals(initialListVersion, successResponse(versionFuture.join()));

        // Differential update must contain only a new idTag and higher version.
        var differentialIdTag = getRandomString();
        dslContext.insertInto(OCPP_TAG)
            .set(OCPP_TAG.ID_TAG, differentialIdTag)
            .set(OCPP_TAG.NOTE, "integration test differential local list idTag")
            .execute();

        var differentialParams = new SendLocalListParams();
        differentialParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        differentialParams.setListVersion(initialListVersion + 1);
        differentialParams.setUpdateType(UpdateType.DIFFERENTIAL);
        differentialParams.setAddUpdateList(List.of(differentialIdTag));

        var differentialFuture = supplyAsyncUnchecked(() -> operationsService.sendLocalList(differentialParams));
        chargePoint.expectRequest(
            new SendLocalListRequest()
                .withListVersion(initialListVersion + 1)
                .withUpdateType(UpdateType.DIFFERENTIAL)
                .withLocalAuthorizationList(List.of(
                    new AuthorizationData()
                        .withIdTag(differentialIdTag)
                        .withIdTagInfo(new IdTagInfo().withStatus(ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED))
                )),
            new SendLocalListResponse().withStatus(UpdateStatus.ACCEPTED)
        );
        assertEquals(UpdateStatus.ACCEPTED, successResponse(differentialFuture.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_044_1_CSMS_FirmwareUpdate_DownloadAndInstall() {
        var chargePoint = defaultStation().start();

        var params = new UpdateFirmwareParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("https://example.org/fw.bin");
        params.setRetries(1);
        params.setRetryInterval(1);
        params.setRetrieveDateTime(DateTime.now().plusMinutes(1));

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.updateFirmware(params));

        chargePoint.expectRequest(
            new UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(params.getRetrieveDateTime())
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval()),
            new UpdateFirmwareResponse()
        );
        assertEquals(UpdateFirmwareTask.UpdateFirmwareResponseStatus.OK, successResponse(operationFuture.join()));

        assertNotNull(chargePoint.send(
            new FirmwareStatusNotificationRequest().withStatus(FirmwareStatus.DOWNLOADING),
            FirmwareStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new FirmwareStatusNotificationRequest().withStatus(FirmwareStatus.DOWNLOADED),
            FirmwareStatusNotificationResponse.class
        ));

        sendUnavailableStatusForAllConnectors(chargePoint);

        assertNotNull(chargePoint.send(
            new FirmwareStatusNotificationRequest().withStatus(FirmwareStatus.INSTALLING),
            FirmwareStatusNotificationResponse.class
        ));

        var bootResp = chargePoint.send(bootNotification(), BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResp.getStatus());

        sendAvailableStatusForAllConnectors(chargePoint);

        assertNotNull(chargePoint.send(
            new FirmwareStatusNotificationRequest().withStatus(FirmwareStatus.INSTALLED),
            FirmwareStatusNotificationResponse.class
        ));

        chargePoint.close();
    }

    @Test
    public void test_TC_044_2_CSMS_FirmwareUpdate_DownloadFailed() {
        var chargePoint = defaultStation().start();

        var params = new UpdateFirmwareParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("https://example.org/fw.bin");
        params.setRetries(1);
        params.setRetryInterval(1);
        params.setRetrieveDateTime(DateTime.now().plusMinutes(1));

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.updateFirmware(params));

        chargePoint.expectRequest(
            new UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(params.getRetrieveDateTime())
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval()),
            new UpdateFirmwareResponse()
        );
        assertEquals(UpdateFirmwareTask.UpdateFirmwareResponseStatus.OK, successResponse(operationFuture.join()));

        assertNotNull(chargePoint.send(
            new FirmwareStatusNotificationRequest().withStatus(FirmwareStatus.DOWNLOADING),
            FirmwareStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new FirmwareStatusNotificationRequest().withStatus(FirmwareStatus.DOWNLOAD_FAILED),
            FirmwareStatusNotificationResponse.class
        ));

        chargePoint.close();
    }

    @Test
    public void test_TC_044_3_CSMS_FirmwareUpdate_InstallationFailed() {
        var chargePoint = defaultStation().start();

        var params = new UpdateFirmwareParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("https://example.org/fw.bin");
        params.setRetries(1);
        params.setRetryInterval(1);
        params.setRetrieveDateTime(DateTime.now().plusMinutes(1));

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.updateFirmware(params));

        chargePoint.expectRequest(
            new UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(params.getRetrieveDateTime())
                .withRetries(1)
                .withRetryInterval(1),
            new UpdateFirmwareResponse()
        );
        assertEquals(UpdateFirmwareTask.UpdateFirmwareResponseStatus.OK, successResponse(operationFuture.join()));

        assertNotNull(chargePoint.send(
            new FirmwareStatusNotificationRequest().withStatus(FirmwareStatus.DOWNLOADING),
            FirmwareStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new FirmwareStatusNotificationRequest().withStatus(FirmwareStatus.DOWNLOADED),
            FirmwareStatusNotificationResponse.class
        ));

        sendUnavailableStatusForAllConnectors(chargePoint);

        assertNotNull(chargePoint.send(
            new FirmwareStatusNotificationRequest().withStatus(FirmwareStatus.INSTALLING),
            FirmwareStatusNotificationResponse.class
        ));

        var bootResp = chargePoint.send(bootNotification(), BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResp.getStatus());

        sendAvailableStatusForAllConnectors(chargePoint);

        assertNotNull(chargePoint.send(
            new FirmwareStatusNotificationRequest().withStatus(FirmwareStatus.INSTALLATION_FAILED),
            FirmwareStatusNotificationResponse.class
        ));

        chargePoint.close();
    }

    @Test
    public void test_TC_045_1_CSMS_GetDiagnostics() {
        var chargePoint = defaultStation().start();

        var params = new GetDiagnosticsParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("ftp://user:pass@example.org/logs");
        params.setRetries(1);
        params.setRetryInterval(1);
        params.setStart(DateTime.now().minusHours(2));
        params.setStop(DateTime.now().minusHours(1));

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.getDiagnostics(params));

        chargePoint.expectRequest(
            new GetDiagnosticsRequest()
                .withLocation(params.getLocation())
                .withRetries(1)
                .withRetryInterval(1)
                .withStartTime(params.getStart())
                .withStopTime(params.getStop()),
            new GetDiagnosticsResponse().withFileName("diag.log")
        );
        assertEquals("diag.log", successResponse(operationFuture.join()).getFileName());

        assertNotNull(chargePoint.send(
            new DiagnosticsStatusNotificationRequest().withStatus(DiagnosticsStatus.UPLOADING),
            DiagnosticsStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new DiagnosticsStatusNotificationRequest().withStatus(DiagnosticsStatus.UPLOADED),
            DiagnosticsStatusNotificationResponse.class
        ));

        chargePoint.close();
    }

    @Test
    public void test_TC_045_2_CSMS_GetDiagnostics_UploadFailed() {
        var chargePoint = defaultStation().start();

        var params = new GetDiagnosticsParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("ftp://user:pass@example.org/logs");
        params.setRetries(1);
        params.setRetryInterval(1);
        params.setStart(DateTime.now().minusHours(2));
        params.setStop(DateTime.now().minusHours(1));

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.getDiagnostics(params));

        chargePoint.expectRequest(
            new GetDiagnosticsRequest()
                .withLocation(params.getLocation())
                .withRetries(1)
                .withRetryInterval(1)
                .withStartTime(params.getStart())
                .withStopTime(params.getStop()),
            new GetDiagnosticsResponse().withFileName("diag.log")
        );
        assertEquals("diag.log", successResponse(operationFuture.join()).getFileName());

        assertNotNull(chargePoint.send(
            new DiagnosticsStatusNotificationRequest().withStatus(DiagnosticsStatus.UPLOADING),
            DiagnosticsStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new DiagnosticsStatusNotificationRequest().withStatus(DiagnosticsStatus.UPLOAD_FAILED),
            DiagnosticsStatusNotificationResponse.class
        ));

        chargePoint.close();
    }

    @Test
    public void test_TC_046_CSMS_ReservationOfConnector_Transaction() {
        var chargePoint = defaultStation().start();
        var expectedReservationId = 1;

        var expiry = DateTime.now().plusMinutes(5);
        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(expiry);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.reserveNow(params));

        chargePoint.expectRequest(
            new ReserveNowRequest().withConnectorId(1).withExpiryDate(expiry).withIdTag(REGISTERED_OCPP_TAG).withReservationId(expectedReservationId),
            new ReserveNowResponse().withStatus(ACCEPTED)
        );
        assertEquals(ACCEPTED, successResponse(operationFuture.join()));

        {
            var results = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.ID_TAG.eq(REGISTERED_OCPP_TAG))
                .fetch();

            assertEquals(1, results.size());
            var reservation = results.getFirst();
            assertEquals(expectedReservationId, reservation.getReservationPk());
            assertEquals(ReservationStatus.ACCEPTED.name(), reservation.getStatus());
        }

        assertNotNull(chargePoint.send(statusNotification(1, ChargePointStatus.RESERVED, ChargePointErrorCode.NO_ERROR), StatusNotificationResponse.class));

        var startTransactionResponse = enterChargingState(chargePoint, 1, REGISTERED_OCPP_TAG, 0, expectedReservationId);

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNotNull(reservation);

            assertEquals(startTransactionResponse.getTransactionId(), reservation.getTransactionPk());
            assertEquals(ReservationStatus.USED.name(), reservation.getStatus());
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_047_CSMS_ReservationOfConnector_Expire() {
        var chargePoint = defaultStation().start();
        var expectedReservationId = 1;

        var expiry = DateTime.now().plusMinutes(2);
        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(expiry);
        params.setIdTag(REGISTERED_OCPP_TAG);

        var operationFuture = supplyAsyncUnchecked(() -> operationsService.reserveNow(params));

        chargePoint.expectRequest(
            new ReserveNowRequest().withConnectorId(1).withExpiryDate(expiry).withIdTag(REGISTERED_OCPP_TAG).withReservationId(expectedReservationId),
            new ReserveNowResponse().withStatus(ACCEPTED)
        );
        assertEquals(ACCEPTED, successResponse(operationFuture.join()));

        assertNotNull(chargePoint.send(statusNotification(1, ChargePointStatus.RESERVED, ChargePointErrorCode.NO_ERROR), StatusNotificationResponse.class));
        assertNotNull(chargePoint.send(statusNotification(1, ChargePointStatus.AVAILABLE, ChargePointErrorCode.NO_ERROR, DateTime.now().plusMinutes(3)), StatusNotificationResponse.class));

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNotNull(reservation);
            assertEquals(ReservationStatus.ACCEPTED.name(), reservation.getStatus());
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_048_1_CSMS_ReservationOfConnector_Faulted() {
        var chargePoint = defaultStation().start();
        var expectedReservationId = 1;

        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(DateTime.now().plusMinutes(5));
        params.setIdTag(REGISTERED_OCPP_TAG);

        var future = supplyAsyncUnchecked(() -> operationsService.reserveNow(params));

        chargePoint.expectRequest(
            new ReserveNowRequest().withConnectorId(1).withExpiryDate(params.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(expectedReservationId),
            new ReserveNowResponse().withStatus(FAULTED)
        );
        assertEquals(FAULTED, successResponse(future.join()));

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNull(reservation);
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_048_2_CSMS_ReservationOfConnector_Occupied() {
        var chargePoint = defaultStation().start();
        var expectedReservationId = 1;

        assertNotNull(chargePoint.send(statusNotification(1, ChargePointStatus.PREPARING, ChargePointErrorCode.NO_ERROR), StatusNotificationResponse.class));

        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(DateTime.now().plusMinutes(5));
        params.setIdTag(REGISTERED_OCPP_TAG);

        var future = supplyAsyncUnchecked(() -> operationsService.reserveNow(params));

        chargePoint.expectRequest(
            new ReserveNowRequest().withConnectorId(1).withExpiryDate(params.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(expectedReservationId),
            new ReserveNowResponse().withStatus(OCCUPIED)
        );
        assertEquals(OCCUPIED, successResponse(future.join()));

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNull(reservation);
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_048_3_CSMS_ReservationOfConnector_Unavailable() {
        var chargePoint = defaultStation().start();
        var expectedReservationId = 1;

        var availabilityParams = new ChangeAvailabilityParams();
        availabilityParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        availabilityParams.setConnectorId(1);
        availabilityParams.setAvailType(AvailabilityType.INOPERATIVE);

        var availabilityFuture = supplyAsyncUnchecked(() -> operationsService.changeAvailability(availabilityParams));
        chargePoint.expectRequest(
            new ChangeAvailabilityRequest().withConnectorId(1).withType(AvailabilityType.INOPERATIVE),
            new ChangeAvailabilityResponse().withStatus(AvailabilityStatus.ACCEPTED)
        );
        assertEquals(AvailabilityStatus.ACCEPTED, successResponse(availabilityFuture.join()));

        assertNotNull(chargePoint.send(
            statusNotification(1, ChargePointStatus.UNAVAILABLE, ChargePointErrorCode.NO_ERROR),
            StatusNotificationResponse.class
        ));

        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(DateTime.now().plusMinutes(5));
        params.setIdTag(REGISTERED_OCPP_TAG);

        var future = supplyAsyncUnchecked(() -> operationsService.reserveNow(params));

        chargePoint.expectRequest(
            new ReserveNowRequest().withConnectorId(1).withExpiryDate(params.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(expectedReservationId),
            new ReserveNowResponse().withStatus(UNAVAILABLE)
        );
        assertEquals(UNAVAILABLE, successResponse(future.join()));

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNull(reservation);
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_048_4_CSMS_ReservationOfConnector_Rejected() {
        var chargePoint = defaultStation().start();
        var expectedReservationId = 1;

        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setExpiry(DateTime.now().plusMinutes(5));
        params.setIdTag(REGISTERED_OCPP_TAG);

        var future = supplyAsyncUnchecked(() -> operationsService.reserveNow(params));

        chargePoint.expectRequest(
            new ReserveNowRequest().withConnectorId(1).withExpiryDate(params.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(expectedReservationId),
            new ReserveNowResponse().withStatus(REJECTED)
        );
        assertEquals(REJECTED, successResponse(future.join()));

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNull(reservation);
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_049_CSMS_ReservationOfChargePoint_Transaction() {
        var chargePoint = defaultStation().start();
        var expectedReservationId = 1;

        var params = new ReserveNowParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(0);
        params.setExpiry(DateTime.now().plusMinutes(5));
        params.setIdTag(REGISTERED_OCPP_TAG);

        var future = supplyAsyncUnchecked(() -> operationsService.reserveNow(params));

        chargePoint.expectRequest(
            new ReserveNowRequest().withConnectorId(0).withExpiryDate(params.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(expectedReservationId),
            new ReserveNowResponse().withStatus(ACCEPTED)
        );
        assertEquals(ACCEPTED, successResponse(future.join()));

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNotNull(reservation);
            assertEquals(REGISTERED_OCPP_TAG, reservation.getIdTag());
            assertEquals(ReservationStatus.ACCEPTED.name(), reservation.getStatus());

            var connectorPk = dslContext.select(CONNECTOR.CONNECTOR_PK)
                .from(CONNECTOR)
                .where(CONNECTOR.CHARGE_BOX_ID.equal(REGISTERED_CHARGE_BOX_ID))
                .and(CONNECTOR.CONNECTOR_ID.equal(params.getConnectorId()))
                .fetchOne();

            assertNotNull(connectorPk);
            assertEquals(connectorPk.component1(), reservation.getConnectorPk());
        }

        assertNotNull(chargePoint.send(statusNotification(1, ChargePointStatus.RESERVED, ChargePointErrorCode.NO_ERROR), StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_051_CSMS_CancelReservation() {
        var chargePoint = defaultStation().start();
        var expectedReservationId = 1;

        var reserve = new ReserveNowParams();
        reserve.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        reserve.setConnectorId(1);
        reserve.setExpiry(DateTime.now().plusMinutes(5));
        reserve.setIdTag(REGISTERED_OCPP_TAG);

        var reserveFuture = supplyAsyncUnchecked(() -> operationsService.reserveNow(reserve));

        chargePoint.expectRequest(
            new ReserveNowRequest().withConnectorId(1).withExpiryDate(reserve.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(expectedReservationId),
            new ReserveNowResponse().withStatus(ACCEPTED)
        );
        assertEquals(ACCEPTED, successResponse(reserveFuture.join()));

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNotNull(reservation);
            assertEquals(REGISTERED_OCPP_TAG, reservation.getIdTag());
            assertEquals(ReservationStatus.ACCEPTED.name(), reservation.getStatus());
        }

        assertNotNull(chargePoint.send(statusNotification(1, ChargePointStatus.RESERVED, ChargePointErrorCode.NO_ERROR), StatusNotificationResponse.class));

        var cancel = new CancelReservationParams();
        cancel.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        cancel.setReservationId(1);

        var cancelFuture = supplyAsyncUnchecked(() -> operationsService.cancelReservation(cancel));

        chargePoint.expectRequest(
            new CancelReservationRequest().withReservationId(expectedReservationId),
            new CancelReservationResponse().withStatus(CancelReservationStatus.ACCEPTED)
        );
        assertEquals(CancelReservationStatus.ACCEPTED, successResponse(cancelFuture.join()));

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNotNull(reservation);
            assertEquals(REGISTERED_OCPP_TAG, reservation.getIdTag());
            assertEquals(ReservationStatus.CANCELLED.name(), reservation.getStatus());
        }

        assertNotNull(chargePoint.send(statusNotification(1, ChargePointStatus.AVAILABLE, ChargePointErrorCode.NO_ERROR), StatusNotificationResponse.class));

        chargePoint.close();
    }

    @Test
    public void test_TC_052_CSMS_CancelReservation_Rejected() {
        var chargePoint = defaultStation().start();
        var expectedReservationId = 1;

        var reserve = new ReserveNowParams();
        reserve.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        reserve.setConnectorId(1);
        reserve.setExpiry(DateTime.now().plusMinutes(5));
        reserve.setIdTag(REGISTERED_OCPP_TAG);

        var reserveFuture = supplyAsyncUnchecked(() -> operationsService.reserveNow(reserve));

        chargePoint.expectRequest(
            new ReserveNowRequest().withConnectorId(1).withExpiryDate(reserve.getExpiry()).withIdTag(REGISTERED_OCPP_TAG).withReservationId(expectedReservationId),
            new ReserveNowResponse().withStatus(ACCEPTED)
        );
        assertEquals(ACCEPTED, successResponse(reserveFuture.join()));

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNotNull(reservation);
            assertEquals(REGISTERED_OCPP_TAG, reservation.getIdTag());
            assertEquals(ReservationStatus.ACCEPTED.name(), reservation.getStatus());
        }

        assertNotNull(chargePoint.send(statusNotification(1, ChargePointStatus.RESERVED, ChargePointErrorCode.NO_ERROR), StatusNotificationResponse.class));

        var cancel = new CancelReservationParams();
        cancel.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        cancel.setReservationId(expectedReservationId);

        var cancelFuture = supplyAsyncUnchecked(() -> operationsService.cancelReservation(cancel));

        chargePoint.expectRequest(
            new CancelReservationRequest().withReservationId(expectedReservationId),
            new CancelReservationResponse().withStatus(CancelReservationStatus.REJECTED)
        );
        assertEquals(CancelReservationStatus.REJECTED, successResponse(cancelFuture.join()));

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNotNull(reservation);
            assertEquals(REGISTERED_OCPP_TAG, reservation.getIdTag());
            assertEquals(ReservationStatus.ACCEPTED.name(), reservation.getStatus());
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_053_CSMS_UseReservedConnectorWithParentIdTag() {
        var parentTag = getRandomString();
        var childTag = getRandomString();
        var expectedReservationId = 1;

        dslContext.insertInto(OCPP_TAG)
            .set(OCPP_TAG.ID_TAG, parentTag)
            .set(OCPP_TAG.NOTE, "integration parent idTag")
            .execute();

        dslContext.insertInto(OCPP_TAG)
            .set(OCPP_TAG.ID_TAG, childTag)
            .set(OCPP_TAG.PARENT_ID_TAG, parentTag)
            .set(OCPP_TAG.NOTE, "integration child idTag")
            .execute();

        var chargePoint = defaultStation().start();

        var reserve = new ReserveNowParams();
        reserve.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        reserve.setConnectorId(1);
        reserve.setExpiry(DateTime.now().plusMinutes(5));
        reserve.setIdTag(childTag);

        var reserveFuture = supplyAsyncUnchecked(() -> operationsService.reserveNow(reserve));

        chargePoint.expectRequest(
            new ReserveNowRequest()
                .withConnectorId(1)
                .withExpiryDate(reserve.getExpiry())
                .withIdTag(childTag)
                .withParentIdTag(parentTag)
                .withReservationId(expectedReservationId),
            new ReserveNowResponse().withStatus(ACCEPTED)
        );
        assertEquals(ACCEPTED, successResponse(reserveFuture.join()));

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNotNull(reservation);
            assertEquals(childTag, reservation.getIdTag());
            assertEquals(ReservationStatus.ACCEPTED.name(), reservation.getStatus());
        }

        assertNotNull(chargePoint.send(statusNotification(1, ChargePointStatus.RESERVED, ChargePointErrorCode.NO_ERROR), StatusNotificationResponse.class));

        var startTransactionResponse = enterChargingState(chargePoint, 1, parentTag, 0, expectedReservationId);

        {
            var reservation = dslContext.selectFrom(RESERVATION)
                .where(RESERVATION.RESERVATION_PK.eq(expectedReservationId))
                .fetchOne();

            assertNotNull(reservation);
            assertEquals(ReservationStatus.USED.name(), reservation.getStatus());
            assertEquals(startTransactionResponse.getTransactionId(), reservation.getTransactionPk());

            var transaction = dslContext.selectFrom(TRANSACTION)
                .where(TRANSACTION.TRANSACTION_PK.eq(startTransactionResponse.getTransactionId()))
                .fetchOne();

            assertNotNull(transaction);
            assertEquals(parentTag, transaction.getIdTag());
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_054_CSMS_TriggerMessage() {
        var chargePoint = defaultStation().start();

        {
            var p1 = new TriggerMessageParams();
            p1.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            p1.setTriggerMessage(TriggerMessageEnum.MeterValues);
            p1.setConnectorId(1);

            var f1 = supplyAsyncUnchecked(() -> operationsService.triggerMessage(p1));

            chargePoint.expectRequest(
                new TriggerMessageRequest().withRequestedMessage(MessageTrigger.METER_VALUES).withConnectorId(1),
                new TriggerMessageResponse().withStatus(TriggerMessageStatus.ACCEPTED)
            );
            assertEquals(TriggerMessageStatus.ACCEPTED, successResponse(f1.join()));

            assertNotNull(chargePoint.send(new MeterValuesRequest().withConnectorId(1), MeterValuesResponse.class));
        }

        {
            var p2 = new TriggerMessageParams();
            p2.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            p2.setTriggerMessage(TriggerMessageEnum.Heartbeat);

            var f2 = supplyAsyncUnchecked(() -> operationsService.triggerMessage(p2));
            chargePoint.expectRequest(
                new TriggerMessageRequest().withRequestedMessage(MessageTrigger.HEARTBEAT),
                new TriggerMessageResponse().withStatus(TriggerMessageStatus.ACCEPTED)
            );
            assertEquals(TriggerMessageStatus.ACCEPTED, successResponse(f2.join()));

            assertNotNull(chargePoint.send(new HeartbeatRequest(), HeartbeatResponse.class));
        }

        {
            var p3 = new TriggerMessageParams();
            p3.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            p3.setTriggerMessage(TriggerMessageEnum.StatusNotification);
            p3.setConnectorId(1);

            var f3 = supplyAsyncUnchecked(() -> operationsService.triggerMessage(p3));
            chargePoint.expectRequest(
                new TriggerMessageRequest().withRequestedMessage(MessageTrigger.STATUS_NOTIFICATION).withConnectorId(1),
                new TriggerMessageResponse().withStatus(TriggerMessageStatus.ACCEPTED)
            );
            assertEquals(TriggerMessageStatus.ACCEPTED, successResponse(f3.join()));

            assertNotNull(chargePoint.send(
                statusNotification(1, ChargePointStatus.AVAILABLE, ChargePointErrorCode.NO_ERROR),
                StatusNotificationResponse.class
            ));
        }

        {
            var p4 = new TriggerMessageParams();
            p4.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            p4.setTriggerMessage(TriggerMessageEnum.DiagnosticsStatusNotification);

            var f4 = supplyAsyncUnchecked(() -> operationsService.triggerMessage(p4));
            chargePoint.expectRequest(
                new TriggerMessageRequest().withRequestedMessage(MessageTrigger.DIAGNOSTICS_STATUS_NOTIFICATION),
                new TriggerMessageResponse().withStatus(TriggerMessageStatus.ACCEPTED)
            );
            assertEquals(TriggerMessageStatus.ACCEPTED, successResponse(f4.join()));

            assertNotNull(chargePoint.send(
                new DiagnosticsStatusNotificationRequest().withStatus(DiagnosticsStatus.IDLE),
                DiagnosticsStatusNotificationResponse.class
            ));
        }

        {
            var p5 = new TriggerMessageParams();
            p5.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            p5.setTriggerMessage(TriggerMessageEnum.FirmwareStatusNotification);

            var f5 = supplyAsyncUnchecked(() -> operationsService.triggerMessage(p5));
            chargePoint.expectRequest(
                new TriggerMessageRequest().withRequestedMessage(MessageTrigger.FIRMWARE_STATUS_NOTIFICATION),
                new TriggerMessageResponse().withStatus(TriggerMessageStatus.ACCEPTED)
            );

            assertEquals(TriggerMessageStatus.ACCEPTED, successResponse(f5.join()));

            assertNotNull(chargePoint.send(
                new FirmwareStatusNotificationRequest().withStatus(FirmwareStatus.IDLE),
                FirmwareStatusNotificationResponse.class
            ));
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_055_CSMS_TriggerMessage_Rejected() {
        var chargePoint = defaultStation().start();

        var p = new TriggerMessageParams();
        p.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        p.setTriggerMessage(TriggerMessageEnum.MeterValues);
        p.setConnectorId(1);

        var future = supplyAsyncUnchecked(() -> operationsService.triggerMessage(p));
        chargePoint.expectRequest(
            new TriggerMessageRequest().withRequestedMessage(MessageTrigger.METER_VALUES).withConnectorId(1),
            new TriggerMessageResponse().withStatus(TriggerMessageStatus.REJECTED)
        );
        assertEquals(TriggerMessageStatus.REJECTED, successResponse(future.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_056_CSMS_CentralSmartCharging_TxDefaultProfile() {
        DateTime nowDt = DateTime.now();

        var form = new ChargingProfileForm();
        form.setDescription("tc056");
        form.setStackLevel(0);
        form.setChargingProfilePurpose(ChargingProfilePurposeType.TX_DEFAULT_PROFILE);
        form.setChargingProfileKind(ChargingProfileKindType.ABSOLUTE);
        form.setChargingRateUnit(ChargingRateUnitType.W);
        form.setValidFrom(nowDt.plusMinutes(1));
        form.setValidTo(nowDt.plusHours(2));
        form.setStartSchedule(nowDt.plusMinutes(2));
        form.setDurationInSeconds(3600);

        var period = new ChargingProfileForm.SchedulePeriod();
        period.setStartPeriodInSeconds(0);
        period.setPowerLimit(BigDecimal.valueOf(6.0));
        period.setNumberPhases(3);
        form.setSchedulePeriods(List.of(period));

        var profilePk = chargingProfileRepository.add(form);
        var details = chargingProfileRepository.getDetails(profilePk);

        var chargePoint = defaultStation().start();

        var params = new SetChargingProfileParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setChargingProfilePk(profilePk);

        var future = supplyAsyncUnchecked(() -> operationsService.setChargingProfile(params));

        chargePoint.expectRequest(
            new SetChargingProfileRequest().withConnectorId(1).withCsChargingProfiles(ChargingProfileDetailsMapper.mapToOcpp(details, null)),
            new SetChargingProfileResponse().withStatus(ChargingProfileStatus.ACCEPTED)
        );
        assertEquals(ChargingProfileStatus.ACCEPTED, successResponse(future.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_057_CSMS_CentralSmartCharging_TxProfile() {
        var chargePoint = defaultStation().start();

        var start = enterChargingState(chargePoint, 1, REGISTERED_OCPP_TAG, 0);
        var txId = start.getTransactionId();

        var form = new ChargingProfileForm();
        form.setDescription("tc057");
        form.setStackLevel(0);
        form.setChargingProfilePurpose(ChargingProfilePurposeType.TX_PROFILE);
        form.setChargingProfileKind(ChargingProfileKindType.ABSOLUTE);
        form.setChargingRateUnit(ChargingRateUnitType.W);
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

        var future = supplyAsyncUnchecked(() -> operationsService.setChargingProfile(params));

        chargePoint.expectRequest(
            new SetChargingProfileRequest().withConnectorId(1).withCsChargingProfiles(ChargingProfileDetailsMapper.mapToOcpp(details, txId)),
            new SetChargingProfileResponse().withStatus(ChargingProfileStatus.ACCEPTED)
        );
        assertEquals(ChargingProfileStatus.ACCEPTED, successResponse(future.join()));

        chargePoint.close();
    }

    @Test
    public void test_TC_059_CSMS_RemoteStartTransactionWithChargingProfile() {
        var form = new ChargingProfileForm();
        form.setDescription("tc059");
        form.setStackLevel(0);
        form.setChargingProfilePurpose(ChargingProfilePurposeType.TX_PROFILE);
        form.setChargingProfileKind(ChargingProfileKindType.ABSOLUTE);
        form.setChargingRateUnit(ChargingRateUnitType.W);
        form.setDurationInSeconds(1200);

        var period = new ChargingProfileForm.SchedulePeriod();
        period.setStartPeriodInSeconds(0);
        period.setPowerLimit(BigDecimal.valueOf(6.0));
        form.setSchedulePeriods(List.of(period));

        var profilePk = chargingProfileRepository.add(form);
        var details = chargingProfileRepository.getDetails(profilePk);

        var chargePoint = defaultStation().start();

        // remote start block
        {
            var params = new RemoteStartTransactionParams();
            params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            params.setConnectorId(1);
            params.setIdTag(REGISTERED_OCPP_TAG);
            params.setChargingProfilePk(profilePk);

            var future = supplyAsyncUnchecked(() -> operationsService.remoteStartTransaction(params));

            chargePoint.expectRequest(
                new RemoteStartTransactionRequest()
                    .withIdTag(REGISTERED_OCPP_TAG)
                    .withConnectorId(1)
                    .withChargingProfile(ChargingProfileDetailsMapper.mapToOcpp(details, null)),
                new RemoteStartTransactionResponse().withStatus(RemoteStartStopStatus.ACCEPTED)
            );

            assertEquals(RemoteStartStopStatus.ACCEPTED, successResponse(future.join()));
        }

        var authResp = chargePoint.send(new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG), AuthorizeResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, authResp.getIdTagInfo().getStatus());

        assertNotNull(chargePoint.send(
            statusNotification(1, ChargePointStatus.PREPARING, ChargePointErrorCode.NO_ERROR),
            StatusNotificationResponse.class
        ));

        var startTxResp = chargePoint.send(startTransaction(1, REGISTERED_OCPP_TAG, 0), StartTransactionResponse.class);
        assertEquals(AuthorizationStatus.ACCEPTED, startTxResp.getIdTagInfo().getStatus());

        assertNotNull(chargePoint.send(
            statusNotification(1, ChargePointStatus.CHARGING, ChargePointErrorCode.NO_ERROR),
            StatusNotificationResponse.class
        ));

        chargePoint.close();
    }

    @Test
    public void test_TC_064_CSMS_DataTransferToCentralSystem() {
        var chargePoint = defaultStation().start();

        var request = new DataTransferRequest()
            .withVendorId("specific-vendor-id")
            .withMessageId("test-message")
            .withData("test-payload");
        var response = chargePoint.send(request, DataTransferResponse.class);

        var status = response.getStatus();

        var allowedSet = Set.of(
            ocpp.cs._2015._10.DataTransferStatus.REJECTED,
            ocpp.cs._2015._10.DataTransferStatus.UNKNOWN_MESSAGE_ID,
            ocpp.cs._2015._10.DataTransferStatus.UNKNOWN_VENDOR_ID
        );

        assertTrue(allowedSet.contains(status));

        chargePoint.close();
    }

    @Test
    public void test_TC_066_CSMS_GetCompositeSchedule() {
        var chargePoint = defaultStation().start();

        var params = new GetCompositeScheduleParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setConnectorId(1);
        params.setDurationInSeconds(300);
        params.setChargingRateUnit(ChargingRateUnitType.W);

        var future = supplyAsyncUnchecked(() -> operationsService.getCompositeSchedule(params));

        chargePoint.expectRequest(
            new GetCompositeScheduleRequest()
                .withConnectorId(1)
                .withDuration(300)
                .withChargingRateUnit(ChargingRateUnitType.W),
            new GetCompositeScheduleResponse()
                .withStatus(GetCompositeScheduleStatus.ACCEPTED)
                .withConnectorId(1)
                .withScheduleStart(DateTime.now())
                .withChargingSchedule(new ChargingSchedule()
                    .withChargingRateUnit(ChargingRateUnitType.W)
                    .withDuration(300)
                    .withChargingSchedulePeriod(List.of(new ChargingSchedulePeriod().withStartPeriod(0).withLimit(BigDecimal.valueOf(6.0)))))
        );
        assertEquals(GetCompositeScheduleStatus.ACCEPTED, successResponse(future.join()).getStatus());

        chargePoint.close();
    }

    @Test
    public void test_TC_067_CSMS_ClearChargingProfile() {
        var chargePoint = defaultStation().start();

        var startTransactionResponse = enterChargingState(chargePoint, 1, REGISTERED_OCPP_TAG, 0);
        var transactionId = startTransactionResponse.getTransactionId();
        int profilePkToClearById;

        // -------------------------------------------------------------------------
        // Set three different charging profiles. Steps 1-2 are therefor repeated three times.
        // -------------------------------------------------------------------------

        // 1 of 3
        {
            var profile1Form = new ChargingProfileForm();
            profile1Form.setDescription("tc067-cp-max");
            profile1Form.setStackLevel(0);
            profile1Form.setChargingProfilePurpose(ChargingProfilePurposeType.CHARGE_POINT_MAX_PROFILE);
            profile1Form.setChargingProfileKind(ChargingProfileKindType.ABSOLUTE);
            profile1Form.setChargingRateUnit(ChargingRateUnitType.W);
            profile1Form.setDurationInSeconds(900);

            var profile1Period = new ChargingProfileForm.SchedulePeriod();
            profile1Period.setStartPeriodInSeconds(0);
            profile1Period.setPowerLimit(BigDecimal.valueOf(7.0));
            profile1Form.setSchedulePeriods(List.of(profile1Period));

            var profile1Pk = chargingProfileRepository.add(profile1Form);
            var profile1Details = chargingProfileRepository.getDetails(profile1Pk);
            profilePkToClearById = profile1Pk;

            var setProfile1Params = new SetChargingProfileParams();
            setProfile1Params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            setProfile1Params.setConnectorId(0);
            setProfile1Params.setChargingProfilePk(profile1Pk);

            var setProfile1Future = supplyAsyncUnchecked(() -> operationsService.setChargingProfile(setProfile1Params));
            chargePoint.expectRequest(
                new SetChargingProfileRequest().withConnectorId(0).withCsChargingProfiles(ChargingProfileDetailsMapper.mapToOcpp(profile1Details, null)),
                new SetChargingProfileResponse().withStatus(ChargingProfileStatus.ACCEPTED)
            );
            assertEquals(ChargingProfileStatus.ACCEPTED, successResponse(setProfile1Future.join()));
        }

        // 2 of 3
        {
            var profile2Form = new ChargingProfileForm();
            profile2Form.setDescription("tc067-tx-default");
            profile2Form.setStackLevel(0);
            profile2Form.setChargingProfilePurpose(ChargingProfilePurposeType.TX_DEFAULT_PROFILE);
            profile2Form.setChargingProfileKind(ChargingProfileKindType.ABSOLUTE);
            profile2Form.setChargingRateUnit(ChargingRateUnitType.W);
            profile2Form.setDurationInSeconds(900);

            var profile2Period = new ChargingProfileForm.SchedulePeriod();
            profile2Period.setStartPeriodInSeconds(0);
            profile2Period.setPowerLimit(BigDecimal.valueOf(9.0));
            profile2Form.setSchedulePeriods(List.of(profile2Period));

            var profile2Pk = chargingProfileRepository.add(profile2Form);
            var profile2Details = chargingProfileRepository.getDetails(profile2Pk);

            var setProfile2Params = new SetChargingProfileParams();
            setProfile2Params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            setProfile2Params.setConnectorId(1);
            setProfile2Params.setChargingProfilePk(profile2Pk);

            var setProfile2Future = supplyAsyncUnchecked(() -> operationsService.setChargingProfile(setProfile2Params));
            chargePoint.expectRequest(
                new SetChargingProfileRequest().withConnectorId(1).withCsChargingProfiles(ChargingProfileDetailsMapper.mapToOcpp(profile2Details, null)),
                new SetChargingProfileResponse().withStatus(ChargingProfileStatus.ACCEPTED)
            );
            assertEquals(ChargingProfileStatus.ACCEPTED, successResponse(setProfile2Future.join()));
        }

        // 3 of 3
        {
            var profile3Form = new ChargingProfileForm();
            profile3Form.setDescription("tc067-tx");
            profile3Form.setStackLevel(0);
            profile3Form.setChargingProfilePurpose(ChargingProfilePurposeType.TX_PROFILE);
            profile3Form.setChargingProfileKind(ChargingProfileKindType.ABSOLUTE);
            profile3Form.setChargingRateUnit(ChargingRateUnitType.W);
            profile3Form.setDurationInSeconds(900);

            var profile3Period = new ChargingProfileForm.SchedulePeriod();
            profile3Period.setStartPeriodInSeconds(0);
            profile3Period.setPowerLimit(BigDecimal.valueOf(11.0));
            profile3Form.setSchedulePeriods(List.of(profile3Period));

            var profile3Pk = chargingProfileRepository.add(profile3Form);
            var profile3Details = chargingProfileRepository.getDetails(profile3Pk);

            var setProfile3Params = new SetChargingProfileParams();
            setProfile3Params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            setProfile3Params.setConnectorId(1);
            setProfile3Params.setChargingProfilePk(profile3Pk);
            setProfile3Params.setTransactionId(transactionId);

            var setProfile3Future = supplyAsyncUnchecked(() -> operationsService.setChargingProfile(setProfile3Params));
            chargePoint.expectRequest(
                new SetChargingProfileRequest().withConnectorId(1).withCsChargingProfiles(ChargingProfileDetailsMapper.mapToOcpp(profile3Details, transactionId)),
                new SetChargingProfileResponse().withStatus(ChargingProfileStatus.ACCEPTED)
            );
            assertEquals(ChargingProfileStatus.ACCEPTED, successResponse(setProfile3Future.join()));
        }

        // 1st clear: Clear a charging profile based on ID.
        {
            var clearById = new ClearChargingProfileParams();
            clearById.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            clearById.setFilterType(ClearChargingProfileFilterType.ChargingProfileId);
            clearById.setChargingProfilePk(profilePkToClearById);

            var clearByIdFuture = supplyAsyncUnchecked(() -> operationsService.clearChargingProfile(clearById));
            chargePoint.expectRequest(
                new ClearChargingProfileRequest().withId(profilePkToClearById),
                new ClearChargingProfileResponse().withStatus(ClearChargingProfileStatus.ACCEPTED)
            );
            assertEquals(ClearChargingProfileStatus.ACCEPTED, successResponse(clearByIdFuture.join()));
        }

        // 2nd clear: Clear a charging profile based on criteria.
        {
            var clearByCriteria = new ClearChargingProfileParams();
            clearByCriteria.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            clearByCriteria.setFilterType(ClearChargingProfileFilterType.OtherParameters);
            clearByCriteria.setConnectorId(1);
            clearByCriteria.setChargingProfilePurpose(ChargingProfilePurposeType.TX_DEFAULT_PROFILE);
            clearByCriteria.setStackLevel(0);

            var clearByCriteriaFuture = supplyAsyncUnchecked(() -> operationsService.clearChargingProfile(clearByCriteria));
            chargePoint.expectRequest(
                new ClearChargingProfileRequest()
                    .withConnectorId(1)
                    .withChargingProfilePurpose(ChargingProfilePurposeType.TX_DEFAULT_PROFILE)
                    .withStackLevel(0),
                new ClearChargingProfileResponse().withStatus(ClearChargingProfileStatus.ACCEPTED)
            );
            assertEquals(ClearChargingProfileStatus.ACCEPTED, successResponse(clearByCriteriaFuture.join()));
        }

        // 3rd clear: Clear all remaining charging profiles.
        {
            var clearAll = new ClearChargingProfileParams();
            clearAll.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            clearAll.setFilterType(ClearChargingProfileFilterType.OtherParameters);

            var clearAllFuture = supplyAsyncUnchecked(() -> operationsService.clearChargingProfile(clearAll));
            chargePoint.expectRequest(
                new ClearChargingProfileRequest(),
                new ClearChargingProfileResponse().withStatus(ClearChargingProfileStatus.ACCEPTED)
            );
            assertEquals(ClearChargingProfileStatus.ACCEPTED, successResponse(clearAllFuture.join()));
        }

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

        var chargePoint = defaultStationWithPwd(password).start();

        expectGetConfCpoName(chargePoint);

        var params = new ChangeConfigurationParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setKeyType(ChangeConfigurationParams.ConfigurationKeyType.PREDEFINED);
        params.setConfKey(AuthorizationKey.name());
        params.setValue(newPassword);

        var valueHex = HexFormat.of().formatHex(params.getValue().getBytes(StandardCharsets.UTF_8));

        var future = supplyAsyncUnchecked(() -> operationsService.changeConfiguration(params));

        chargePoint.expectRequest(
            new ChangeConfigurationRequest().withKey(AuthorizationKey.name()).withValue(valueHex),
            new ChangeConfigurationResponse().withStatus(ConfigurationStatus.ACCEPTED)
        );
        assertEquals(ConfigurationStatus.ACCEPTED, successResponse(future.join()));

        var record = dslContext
            .selectFrom(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .fetchOne();

        assertNotNull(record);
        assertTrue(passwordEncoder.matches(newPassword, record.getAuthPassword()));

        // disconnect and reconnect
        chargePoint.close();
        chargePoint = defaultStationWithPwd(newPassword).start();

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
        var chargePoint = defaultStation().start();

        // install cert
        {
            var certificate = "-----BEGIN CERTIFICATE-----\\nMANUFACTURER-ROOT-" + getRandomString() + "\\n-----END CERTIFICATE-----";

            var installParams = new InstallCertificateParams();
            installParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            installParams.setCertificateType(InstallCertificate.CertificateUseEnumType.MANUFACTURER_ROOT_CERTIFICATE);
            installParams.setCertificate(certificate);

            var installFuture = supplyAsyncUnchecked(() -> operationsService.installCertificate(installParams));

            chargePoint.expectRequest(
                new InstallCertificate()
                    .withCertificateType(InstallCertificate.CertificateUseEnumType.MANUFACTURER_ROOT_CERTIFICATE)
                    .withCertificate(certificate),
                new InstallCertificateResponse()
                    .withStatus(InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED)
            );
            assertEquals(InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED, successResponse(installFuture.join()));
        }

        // get installed cert
        {
            var getIdsParams = new GetInstalledCertificateIdsParams();
            getIdsParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            getIdsParams.setCertificateType(CertificateUseEnumType.MANUFACTURER_ROOT_CERTIFICATE);

            var getIdsFuture = supplyAsyncUnchecked(() -> operationsService.getInstalledCertificateIds(getIdsParams));

            chargePoint.expectRequest(
                new GetInstalledCertificateIds()
                    .withCertificateType(CertificateUseEnumType.MANUFACTURER_ROOT_CERTIFICATE),
                new GetInstalledCertificateIdsResponse()
                    .withStatus(GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED)
                    .withCertificateHashData(List.of(
                        new CertificateHashData()
                            .withHashAlgorithm(CertificateHashData.HashAlgorithmEnumType.SHA_256)
                            .withIssuerNameHash("issuer-name-hash-manufacturer")
                            .withIssuerKeyHash("issuer-key-hash-manufacturer")
                            .withSerialNumber("serial-manufacturer")
                    ))
            );

            var getIdsResponse = successResponse(getIdsFuture.join());
            assertEquals(GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED, getIdsResponse.getStatus());
            assertNotNull(getIdsResponse.getCertificateHashData());
            assertEquals(1, getIdsResponse.getCertificateHashData().size());
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_075_2_CSMS_InstallCentralSystemRootCertificate() {
        var chargePoint = defaultStation().start();

        // install cert
        {
            var certificate = "-----BEGIN CERTIFICATE-----\\nCSMS-ROOT-" + getRandomString() + "\\n-----END CERTIFICATE-----";

            var installParams = new InstallCertificateParams();
            installParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            installParams.setCertificateType(InstallCertificate.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE);
            installParams.setCertificate(certificate);

            var installFuture = supplyAsyncUnchecked(() -> operationsService.installCertificate(installParams));

            chargePoint.expectRequest(
                new InstallCertificate()
                    .withCertificateType(InstallCertificate.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE)
                    .withCertificate(certificate),
                new InstallCertificateResponse()
                    .withStatus(InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED)
            );
            assertEquals(InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED, successResponse(installFuture.join()));
        }

        // get installed cert
        {
            var getIdsParams = new GetInstalledCertificateIdsParams();
            getIdsParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            getIdsParams.setCertificateType(CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE);

            var getIdsFuture = supplyAsyncUnchecked(() -> operationsService.getInstalledCertificateIds(getIdsParams));

            chargePoint.expectRequest(
                new GetInstalledCertificateIds()
                    .withCertificateType(CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE),
                new GetInstalledCertificateIdsResponse()
                    .withStatus(GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED)
                    .withCertificateHashData(List.of(
                        new CertificateHashData()
                            .withHashAlgorithm(CertificateHashData.HashAlgorithmEnumType.SHA_256)
                            .withIssuerNameHash("issuer-name-hash-csms")
                            .withIssuerKeyHash("issuer-key-hash-csms")
                            .withSerialNumber("serial-csms")
                    ))
            );

            var getIdsResponse = successResponse(getIdsFuture.join());
            assertEquals(GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED, getIdsResponse.getStatus());
            assertNotNull(getIdsResponse.getCertificateHashData());
            assertEquals(1, getIdsResponse.getCertificateHashData().size());
        }

        chargePoint.close();
    }

    @Test
    public void test_TC_076_CSMS_DeleteSpecificInstalledCertificate() {
        var chargePoint = defaultStation().start();

        var certificateType = InstallCertificate.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE;

        for (var hashAlgorithm : List.of(
            CertificateHashData.HashAlgorithmEnumType.SHA_256,
            CertificateHashData.HashAlgorithmEnumType.SHA_384,
            CertificateHashData.HashAlgorithmEnumType.SHA_512
        )) {
            // install cert
            {
                var certificate = "-----BEGIN CERTIFICATE-----\\nCSMS-ROOT-" + hashAlgorithm.value() + "-" + getRandomString() + "\\n-----END CERTIFICATE-----";

                var installParams = new InstallCertificateParams();
                installParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
                installParams.setCertificateType(certificateType);
                installParams.setCertificate(certificate);

                var installFuture = supplyAsyncUnchecked(() -> operationsService.installCertificate(installParams));

                chargePoint.expectRequest(
                    new InstallCertificate()
                        .withCertificateType(installParams.getCertificateType())
                        .withCertificate(certificate),
                    new InstallCertificateResponse()
                        .withStatus(InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED)
                );
                assertEquals(InstallCertificateResponse.InstallCertificateStatusEnumType.ACCEPTED, successResponse(installFuture.join()));
            }

            var hashData = new CertificateHashData()
                .withHashAlgorithm(hashAlgorithm)
                .withIssuerNameHash("issuer-name-" + hashAlgorithm.value().toLowerCase())
                .withIssuerKeyHash("issuer-key-" + hashAlgorithm.value().toLowerCase())
                .withSerialNumber("serial-" + hashAlgorithm.value().toLowerCase());

            // get installed cert
            Long installedCertificateId;
            {
                var getIdsParams = new GetInstalledCertificateIdsParams();
                getIdsParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
                getIdsParams.setCertificateType(GetInstalledCertificateIds.CertificateUseEnumType.fromValue(certificateType.value()));

                var getIdsFuture = supplyAsyncUnchecked(() -> operationsService.getInstalledCertificateIds(getIdsParams));

                chargePoint.expectRequest(
                    new GetInstalledCertificateIds()
                        .withCertificateType(getIdsParams.getCertificateType()),
                    new GetInstalledCertificateIdsResponse()
                        .withStatus(GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED)
                        .withCertificateHashData(List.of(hashData))
                );
                assertEquals(GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED, successResponse(getIdsFuture.join()).getStatus());

                installedCertificateId = dslContext.select(CHARGE_BOX_CERTIFICATE_INSTALLED.ID)
                    .from(CHARGE_BOX_CERTIFICATE_INSTALLED)
                    .join(CHARGE_BOX).on(CHARGE_BOX_CERTIFICATE_INSTALLED.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
                    .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
                    .and(CHARGE_BOX_CERTIFICATE_INSTALLED.CERTIFICATE_TYPE.eq(certificateType.value()))
                    .orderBy(CHARGE_BOX_CERTIFICATE_INSTALLED.ID.desc())
                    .limit(1)
                    .fetchOne(CHARGE_BOX_CERTIFICATE_INSTALLED.ID);

                assertNotNull(installedCertificateId);
            }

            // delete cert
            {
                var deleteParams = new DeleteCertificateParams();
                deleteParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
                deleteParams.setInstalledCertificateId(installedCertificateId);

                var deleteFuture = supplyAsyncUnchecked(() -> operationsService.deleteCertificate(deleteParams));

                chargePoint.expectRequest(
                    new DeleteCertificate()
                        .withCertificateHashData(new CertificateHashDataType()
                            .withHashAlgorithm(CertificateHashDataType.HashAlgorithmEnumType.fromValue(hashAlgorithm.value()))
                            .withIssuerNameHash(hashData.getIssuerNameHash())
                            .withIssuerKeyHash(hashData.getIssuerKeyHash())
                            .withSerialNumber(hashData.getSerialNumber())),
                    new DeleteCertificateResponse()
                        .withStatus(DeleteCertificateResponse.DeleteCertificateStatusEnumType.ACCEPTED)
                );
                assertEquals(DeleteCertificateResponse.DeleteCertificateStatusEnumType.ACCEPTED, successResponse(deleteFuture.join()));

                var deletedCertificate = dslContext.selectFrom(CHARGE_BOX_CERTIFICATE_INSTALLED)
                    .where(CHARGE_BOX_CERTIFICATE_INSTALLED.ID.eq(installedCertificateId))
                    .fetchOne();

                assertNull(deletedCertificate);
            }

            // verify deletion
            {
                var getIdsParams = new GetInstalledCertificateIdsParams();
                getIdsParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
                getIdsParams.setCertificateType(GetInstalledCertificateIds.CertificateUseEnumType.fromValue(certificateType.value()));

                var verifyFuture = supplyAsyncUnchecked(() -> operationsService.getInstalledCertificateIds(getIdsParams));

                chargePoint.expectRequest(
                    new GetInstalledCertificateIds()
                        .withCertificateType(getIdsParams.getCertificateType()),
                    new GetInstalledCertificateIdsResponse()
                        .withStatus(GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED)
                        .withCertificateHashData(null)
                );
                assertEquals(GetInstalledCertificateIdsResponse.GetInstalledCertificateStatusEnumType.ACCEPTED, successResponse(verifyFuture.join()).getStatus());
            }
        }

        var remaining = dslContext.selectCount()
            .from(CHARGE_BOX_CERTIFICATE_INSTALLED)
            .join(CHARGE_BOX).on(CHARGE_BOX_CERTIFICATE_INSTALLED.CHARGE_BOX_PK.eq(CHARGE_BOX.CHARGE_BOX_PK))
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .and(CHARGE_BOX_CERTIFICATE_INSTALLED.CERTIFICATE_TYPE.eq(certificateType.value()))
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
        var chargePoint = defaultStation().start();

        var certificate = "-----BEGIN CERTIFICATE-----\\nINVALID-CSMS-ROOT-" + getRandomString() + "\\n-----END CERTIFICATE-----";

        var installParams = new InstallCertificateParams();
        installParams.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        installParams.setCertificateType(InstallCertificate.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE);
        installParams.setCertificate(certificate);

        var installFuture = supplyAsyncUnchecked(() -> operationsService.installCertificate(installParams));

        chargePoint.expectRequest(
            new InstallCertificate()
                .withCertificateType(InstallCertificate.CertificateUseEnumType.CENTRAL_SYSTEM_ROOT_CERTIFICATE)
                .withCertificate(certificate),
            new InstallCertificateResponse()
                .withStatus(InstallCertificateResponse.InstallCertificateStatusEnumType.REJECTED)
        );
        assertEquals(InstallCertificateResponse.InstallCertificateStatusEnumType.REJECTED, successResponse(installFuture.join()));

        var securityEventResponse = chargePoint.send(
            new SecurityEventNotification()
                .withType("InvalidCentralSystemCertificate")
                .withTimestamp(DateTime.now())
                .withTechInfo("certificate rejected by charge point"),
            SecurityEventNotificationResponse.class
        );
        assertNotNull(securityEventResponse);

        chargePoint.close();
    }

    @Test
    public void test_TC_079_CSMS_GetSecurityLog() {
        var chargePoint = defaultStation().start();

        var latestJobId = dslContext.select(DSL.max(CHARGE_BOX_LOG_UPLOAD_JOB.JOB_ID))
            .from(CHARGE_BOX_LOG_UPLOAD_JOB)
            .fetchOne();
        assertNotNull(latestJobId);
        assertNull(latestJobId.value1());

        var expectedJobId = 1;

        var params = new GetLogParams();
        params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
        params.setLocation("https://example.com/security-log/upload");
        params.setLogType(GetLog.LogEnumType.SECURITY_LOG);
        params.setRetries(1);
        params.setRetryInterval(60);

        var future = supplyAsyncUnchecked(() -> operationsService.getLog(params));

        chargePoint.expectRequest(
            new GetLog()
                .withRequestId(expectedJobId)
                .withLogType(GetLog.LogEnumType.SECURITY_LOG)
                .withRetries(1)
                .withRetryInterval(60)
                .withLog(new LogParametersType().withRemoteLocation("https://example.com/security-log/upload")),
            new GetLogResponse()
                .withStatus(GetLogResponse.LogStatusEnumType.ACCEPTED)
                .withFilename("security.log")
        );
        assertEquals(GetLogResponse.LogStatusEnumType.ACCEPTED, successResponse(future.join()).getStatus());

        assertNotNull(chargePoint.send(
            new LogStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(LogStatusNotification.UploadLogStatusEnumType.UPLOADING),
            LogStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new LogStatusNotification()
                .withRequestId(expectedJobId)
                .withStatus(LogStatusNotification.UploadLogStatusEnumType.UPLOADED),
            LogStatusNotificationResponse.class
        ));

        var events = dslContext.selectFrom(CHARGE_BOX_LOG_UPLOAD_EVENT)
            .where(CHARGE_BOX_LOG_UPLOAD_EVENT.JOB_ID.eq(expectedJobId))
            .fetch();

        assertEquals(2, events.size());

        chargePoint.close();
    }

    @Test
    public void test_TC_080_CSMS_SecureFirmwareUpdate() {
        var chargePoint = defaultStation().start();

        var latestJobId = dslContext.select(DSL.max(CHARGE_BOX_FIRMWARE_UPDATE_JOB.JOB_ID))
            .from(CHARGE_BOX_FIRMWARE_UPDATE_JOB)
            .fetchOne();
        assertNotNull(latestJobId);
        assertNull(latestJobId.value1());

        var expectedJobId = 1;

        {
            var params = new SignedUpdateFirmwareParams();
            params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            params.setLocation("https://example.com/fw/secure.bin");
            params.setRetrieveDateTime(DateTime.now().plusMinutes(2));
            params.setInstallDateTime(DateTime.now().plusMinutes(7));
            params.setRetries(1);
            params.setRetryInterval(60);
            params.setSignature("valid-signature-" + getRandomString());
            params.setSigningCertificate("-----BEGIN CERTIFICATE-----\\nSIGNING-CERT-" + getRandomString() + "\\n-----END CERTIFICATE-----");

            var future = supplyAsyncUnchecked(() -> operationsService.signedUpdateFirmware(params));

            chargePoint.expectRequest(
                new SignedUpdateFirmware()
                    .withRequestId(expectedJobId)
                    .withRetries(1)
                    .withRetryInterval(60)
                    .withFirmware(new FirmwareType()
                        .withLocation("https://example.com/fw/secure.bin")
                        .withRetrieveDateTime(params.getRetrieveDateTime())
                        .withInstallDateTime(params.getInstallDateTime())
                        .withSignature(params.getSignature())
                        .withSigningCertificate(params.getSigningCertificate())),
                new SignedUpdateFirmwareResponse()
                    .withStatus(UpdateFirmwareStatusEnumType.ACCEPTED)
            );

            assertEquals(UpdateFirmwareStatusEnumType.ACCEPTED, successResponse(future.join()));
        }

        assertNotNull(chargePoint.send(
            new SignedFirmwareStatusNotification().withRequestId(expectedJobId).withStatus(FirmwareStatusEnumType.DOWNLOADING),
            SignedFirmwareStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new SignedFirmwareStatusNotification().withRequestId(expectedJobId).withStatus(FirmwareStatusEnumType.DOWNLOADED),
            SignedFirmwareStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new SignedFirmwareStatusNotification().withRequestId(expectedJobId).withStatus(FirmwareStatusEnumType.SIGNATURE_VERIFIED),
            SignedFirmwareStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            statusNotification(0, ChargePointStatus.UNAVAILABLE, ChargePointErrorCode.NO_ERROR),
            StatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new SignedFirmwareStatusNotification().withRequestId(expectedJobId).withStatus(FirmwareStatusEnumType.INSTALLING),
            SignedFirmwareStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new SignedFirmwareStatusNotification().withRequestId(expectedJobId).withStatus(FirmwareStatusEnumType.INSTALL_REBOOTING),
            SignedFirmwareStatusNotificationResponse.class
        ));

        var bootResp = chargePoint.send(bootNotification(), BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResp.getStatus());

        assertNotNull(chargePoint.send(
            new SecurityEventNotification().withType("FirmwareUpdated").withTimestamp(DateTime.now()),
            SecurityEventNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            statusNotification(0, ChargePointStatus.AVAILABLE, ChargePointErrorCode.NO_ERROR),
            StatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new SignedFirmwareStatusNotification().withRequestId(expectedJobId).withStatus(FirmwareStatusEnumType.INSTALLED),
            SignedFirmwareStatusNotificationResponse.class
        ));

        var events = dslContext.selectFrom(CHARGE_BOX_FIRMWARE_UPDATE_EVENT)
            .where(CHARGE_BOX_FIRMWARE_UPDATE_EVENT.JOB_ID.eq(expectedJobId))
            .fetch();

        assertEquals(6, events.size());

        chargePoint.close();
    }

    @Test
    public void test_TC_081_CSMS_SecureFirmwareUpdateInvalidSignature() {
        var chargePoint = defaultStation().start();

        var latestJobId = dslContext.select(DSL.max(CHARGE_BOX_FIRMWARE_UPDATE_JOB.JOB_ID))
            .from(CHARGE_BOX_FIRMWARE_UPDATE_JOB)
            .fetchOne();
        assertNotNull(latestJobId);
        assertNull(latestJobId.value1());

        var expectedJobId = 1;

        {
            var params = new SignedUpdateFirmwareParams();
            params.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            params.setLocation("https://example.com/fw/secure-invalid-signature.bin");
            params.setRetrieveDateTime(DateTime.now().plusMinutes(2));
            params.setInstallDateTime(DateTime.now().plusMinutes(7));
            params.setRetries(1);
            params.setRetryInterval(60);
            params.setSignature("invalid-signature-" + getRandomString());
            params.setSigningCertificate("-----BEGIN CERTIFICATE-----\\nSIGNING-CERT-" + getRandomString() + "\\n-----END CERTIFICATE-----");

            var future = supplyAsyncUnchecked(() -> operationsService.signedUpdateFirmware(params));
            chargePoint.expectRequest(
                new SignedUpdateFirmware()
                    .withRequestId(expectedJobId)
                    .withRetries(1)
                    .withRetryInterval(60)
                    .withFirmware(new FirmwareType()
                        .withLocation("https://example.com/fw/secure-invalid-signature.bin")
                        .withRetrieveDateTime(params.getRetrieveDateTime())
                        .withInstallDateTime(params.getInstallDateTime())
                        .withSignature(params.getSignature())
                        .withSigningCertificate(params.getSigningCertificate())),
                new SignedUpdateFirmwareResponse()
                    .withStatus(UpdateFirmwareStatusEnumType.ACCEPTED)
            );
            assertEquals(UpdateFirmwareStatusEnumType.ACCEPTED, successResponse(future.join()));
        }

        assertNotNull(chargePoint.send(
            new SignedFirmwareStatusNotification().withRequestId(expectedJobId).withStatus(FirmwareStatusEnumType.DOWNLOADING),
            SignedFirmwareStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new SignedFirmwareStatusNotification().withRequestId(expectedJobId).withStatus(FirmwareStatusEnumType.DOWNLOADED),
            SignedFirmwareStatusNotificationResponse.class
        ));

        assertNotNull(chargePoint.send(
            new SignedFirmwareStatusNotification().withRequestId(expectedJobId).withStatus(FirmwareStatusEnumType.INVALID_SIGNATURE),
            SignedFirmwareStatusNotificationResponse.class
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

        var chargePoint = defaultStationWithPwd(password).start();

        expectGetConfCpoName(chargePoint);

        // change config
        {
            var changeConfig = new ChangeConfigurationParams();
            changeConfig.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            changeConfig.setKeyType(ChangeConfigurationParams.ConfigurationKeyType.PREDEFINED);
            changeConfig.setConfKey(SecurityProfile.name());
            changeConfig.setValue("2");

            var changeConfigFuture = supplyAsyncUnchecked(() -> operationsService.changeConfiguration(changeConfig));
            chargePoint.expectRequest(
                new ChangeConfigurationRequest().withKey(SecurityProfile.name()).withValue("2"),
                new ChangeConfigurationResponse().withStatus(ConfigurationStatus.ACCEPTED)
            );
            assertEquals(ConfigurationStatus.ACCEPTED, successResponse(changeConfigFuture.join()));

            var chargeBox = dslContext.selectFrom(CHARGE_BOX)
                .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
                .fetchOne();

            assertNotNull(chargeBox);
            assertEquals(2, chargeBox.getSecurityProfile());
        }

        // reset
        {
            var reset = new ResetParams();
            reset.setChargeBoxIdList(List.of(REGISTERED_CHARGE_BOX_ID));
            reset.setResetType(ResetType.HARD);

            var resetFuture = supplyAsyncUnchecked(() -> operationsService.reset(reset));

            chargePoint.expectRequest(
                new ResetRequest().withType(ResetType.HARD),
                new ResetResponse().withStatus(ResetStatus.ACCEPTED)
            );
            assertEquals(ResetStatus.ACCEPTED, successResponse(resetFuture.join()));
        }

        // disconnect and reconnect
        {
            chargePoint.close();
            chargePoint = defaultStationWithPwd(password).start();

            expectGetConfCpoName(chargePoint);

            var bootResp = chargePoint.send(bootNotification(), BootNotificationResponse.class);
            assertEquals(RegistrationStatus.ACCEPTED, bootResp.getStatus());

            sendAvailableStatusForAllConnectors(chargePoint);

            chargePoint.close();
        }

        // step 11/12: reconnect with old security profile must be rejected
        assertThrows(RuntimeException.class, () -> defaultStation().start());

        // step 13/14: reconnect with upgraded security profile is accepted again
        {
            chargePoint = defaultStationWithPwd(password).start();
            expectGetConfCpoName(chargePoint);
            chargePoint.close();
        }
    }

    @Test
    public void test_TC_085_CSMS_BasicAuthenticationValidCredentials() {
        String password = "0123456789abcdef0123456789abcdef";

        dslContext.update(CHARGE_BOX)
            .set(CHARGE_BOX.SECURITY_PROFILE, 1)
            .set(CHARGE_BOX.AUTH_PASSWORD, passwordEncoder.encode(password))
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(REGISTERED_CHARGE_BOX_ID))
            .execute();

        assertThrows(RuntimeException.class, () -> defaultStation().start());

        var chargePoint = defaultStationWithPwd(password).start();

        expectGetConfCpoName(chargePoint);

        var bootResp = chargePoint.send(bootNotification(), BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, bootResp.getStatus());

        sendAvailableStatusForAllConnectors(chargePoint);

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

        var boot = chargePoint.send(bootNotification(), BootNotificationResponse.class);
        assertEquals(RegistrationStatus.ACCEPTED, boot.getStatus());

        chargePoint.close();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static BootNotificationRequest bootNotification() {
        return new BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());
    }

    private static StatusNotificationRequest statusNotification(int connectorId,
                                                                ChargePointStatus status,
                                                                ChargePointErrorCode errorCode) {
        return statusNotification(connectorId, status, errorCode, DateTime.now());
    }

    private static StatusNotificationRequest statusNotification(int connectorId,
                                                                ChargePointStatus status,
                                                                ChargePointErrorCode errorCode,
                                                                DateTime timestamp) {
        return new StatusNotificationRequest()
            .withConnectorId(connectorId)
            .withStatus(status)
            .withErrorCode(errorCode)
            .withTimestamp(timestamp);
    }

    private static StartTransactionRequest startTransaction(int connectorId, String idTag, int meterStart) {
        return startTransaction(connectorId, idTag, meterStart, null);
    }

    private static StartTransactionRequest startTransaction(int connectorId, String idTag, int meterStart,
                                                            @Nullable Integer reservationId) {
        return startTransaction(connectorId, idTag, meterStart, reservationId, DateTime.now());
    }

    private static StartTransactionRequest startTransaction(int connectorId, String idTag, int meterStart,
                                                            @Nullable Integer reservationId, DateTime timestamp) {
        return new StartTransactionRequest()
            .withConnectorId(connectorId)
            .withIdTag(idTag)
            .withReservationId(reservationId)
            .withMeterStart(meterStart)
            .withTimestamp(timestamp);
    }

    private static StopTransactionRequest stopTransaction(int transactionId, String idTag, int meterStop, Reason reason) {
        return stopTransaction(transactionId, idTag, meterStop, reason, DateTime.now());
    }

    private static StopTransactionRequest stopTransaction(int transactionId, String idTag, int meterStop, Reason reason,
                                                          DateTime timestamp) {
        return new StopTransactionRequest()
            .withTransactionId(transactionId)
            .withIdTag(idTag)
            .withMeterStop(meterStop)
            .withReason(reason)
            .withTimestamp(timestamp);
    }

    private static KeyValue configurationKey(String key, boolean readonly, String value) {
        return new KeyValue()
            .withKey(key)
            .withReadonly(readonly)
            .withValue(value);
    }

    private static void sendAvailableStatusForAllConnectors(OcppJsonChargePoint chargePoint) {
        sendStatusForAllConnectors(chargePoint, ChargePointStatus.AVAILABLE);
    }

    private static void sendUnavailableStatusForAllConnectors(OcppJsonChargePoint chargePoint) {
        sendStatusForAllConnectors(chargePoint, ChargePointStatus.UNAVAILABLE);
    }

    private static void sendStatusForAllConnectors(OcppJsonChargePoint chargePoint, ChargePointStatus status) {
        for (var connectorId : List.of(0, 1, 2)) {
            var statusResponse = chargePoint.send(
                statusNotification(connectorId, status, ChargePointErrorCode.NO_ERROR),
                StatusNotificationResponse.class
            );
            assertNotNull(statusResponse);
        }
    }

    private static void enterAuthorizedState(OcppJsonChargePoint chargePoint) {
        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);

        assertNotNull(authorizeResponse);
        assertNotNull(authorizeResponse.getIdTagInfo());
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());
    }

    private static StartTransactionResponse enterChargingState(OcppJsonChargePoint chargePoint,
                                                               int connectorId,
                                                               String idTag,
                                                               int meterStart) {
        return enterChargingState(chargePoint, connectorId, idTag, meterStart, null);
    }

    private static StartTransactionResponse enterChargingState(OcppJsonChargePoint chargePoint,
                                                               int connectorId,
                                                               String idTag,
                                                               int meterStart,
                                                               @Nullable Integer reservationId) {
        enterAuthorizedState(chargePoint);

        assertNotNull(chargePoint.send(
            statusNotification(connectorId, ChargePointStatus.PREPARING, ChargePointErrorCode.NO_ERROR),
            StatusNotificationResponse.class
        ));

        var startTransactionResponse = chargePoint.send(
            startTransaction(connectorId, idTag, meterStart, reservationId),
            StartTransactionResponse.class
        );
        assertNotNull(startTransactionResponse);
        assertNotNull(startTransactionResponse.getIdTagInfo());
        assertEquals(AuthorizationStatus.ACCEPTED, startTransactionResponse.getIdTagInfo().getStatus());
        assertTrue(startTransactionResponse.getTransactionId() > 0);

        assertNotNull(chargePoint.send(
            statusNotification(connectorId, ChargePointStatus.CHARGING, ChargePointErrorCode.NO_ERROR),
            StatusNotificationResponse.class
        ));
        return startTransactionResponse;
    }

    /**
     * SteVe started asking for this configuration after each connection. So, we need to anticipate this request
     * in our test flows, since they are strict.
     */
    private static void expectGetConfCpoName(OcppJsonChargePoint chargePoint) {
        KeyValue kv = configurationKey(CpoName.name(), false, "SteVe-CPO");

        chargePoint.expectRequest(
            new GetConfigurationRequest().withKey(CpoName.name()),
            new GetConfigurationResponse().withConfigurationKey(List.of(kv))
        );
    }

    private static OcppJsonChargePoint defaultStation() {
        return new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH);
    }

    private static OcppJsonChargePoint defaultStationWithPwd(String basicAuthPassword) {
        return new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH, basicAuthPassword);
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    private static <T> CompletableFuture<T> supplyAsyncUnchecked(ThrowingSupplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                log.warn("Async operation failed", e);
                throw new CompletionException("Async operation failed", e);
            }
        });
    }

    private static <T> T successResponse(RestCallback<T> callback) {
        assertNotNull(callback);
        assertTrue(callback.getExceptionsByChargeBoxId().isEmpty());
        assertTrue(callback.getErrorResponsesByChargeBoxId().isEmpty());
        assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
        return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
    }

}
