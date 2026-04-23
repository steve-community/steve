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
import de.rwth.idsg.steve.utils.OcppJsonChargePoint;
import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import de.rwth.idsg.steve.web.dto.RestCallback;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.GetConfigurationRequest;
import ocpp.cp._2015._10.GetConfigurationResponse;
import ocpp.cp._2015._10.KeyValue;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.Reason;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StatusNotificationResponse;
import ocpp.cs._2015._10.StopTransactionRequest;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static de.rwth.idsg.steve.utils.Helpers.getRandomString;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.CpoName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractOcpp16JsonCsms {

    static final String PATH = "ws://localhost:8080/steve/websocket/CentralSystemService/";

    static final String REGISTERED_CHARGE_BOX_ID = __DatabasePreparer__.getRegisteredChargeBoxId();
    static final String REGISTERED_OCPP_TAG = __DatabasePreparer__.getRegisteredOcppTag();

    static final String CPO_NAME = "SteVe-CPO";

    static BootNotificationRequest bootNotification() {
        return new BootNotificationRequest()
            .withChargePointVendor(getRandomString())
            .withChargePointModel(getRandomString());
    }

    static StatusNotificationRequest statusNotification(int connectorId,
                                                        ChargePointStatus status,
                                                        ChargePointErrorCode errorCode) {
        return statusNotification(connectorId, status, errorCode, DateTime.now());
    }

    static StatusNotificationRequest statusNotification(int connectorId,
                                                        ChargePointStatus status,
                                                        ChargePointErrorCode errorCode,
                                                        DateTime timestamp) {
        return new StatusNotificationRequest()
            .withConnectorId(connectorId)
            .withStatus(status)
            .withErrorCode(errorCode)
            .withTimestamp(timestamp);
    }

    static StartTransactionRequest startTransaction(int connectorId, String idTag, int meterStart) {
        return startTransaction(connectorId, idTag, meterStart, null);
    }

    static StartTransactionRequest startTransaction(int connectorId, String idTag, int meterStart,
                                                    @Nullable Integer reservationId) {
        return startTransaction(connectorId, idTag, meterStart, reservationId, DateTime.now());
    }

    static StartTransactionRequest startTransaction(int connectorId, String idTag, int meterStart,
                                                    @Nullable Integer reservationId, DateTime timestamp) {
        return new StartTransactionRequest()
            .withConnectorId(connectorId)
            .withIdTag(idTag)
            .withReservationId(reservationId)
            .withMeterStart(meterStart)
            .withTimestamp(timestamp);
    }

    static StopTransactionRequest stopTransaction(int transactionId, String idTag, int meterStop, Reason reason) {
        return stopTransaction(transactionId, idTag, meterStop, reason, DateTime.now());
    }

    static StopTransactionRequest stopTransaction(int transactionId, String idTag, int meterStop, Reason reason,
                                                  DateTime timestamp) {
        return new StopTransactionRequest()
            .withTransactionId(transactionId)
            .withIdTag(idTag)
            .withMeterStop(meterStop)
            .withReason(reason)
            .withTimestamp(timestamp);
    }

    static KeyValue configurationKey(String key, boolean readonly, String value) {
        return new KeyValue()
            .withKey(key)
            .withReadonly(readonly)
            .withValue(value);
    }

    static void sendAvailableStatusForAllConnectors(OcppJsonChargePoint chargePoint) {
        sendStatusForAllConnectors(chargePoint, ChargePointStatus.AVAILABLE);
    }

    static void sendUnavailableStatusForAllConnectors(OcppJsonChargePoint chargePoint) {
        sendStatusForAllConnectors(chargePoint, ChargePointStatus.UNAVAILABLE);
    }

    static void sendStatusForAllConnectors(OcppJsonChargePoint chargePoint, ChargePointStatus status) {
        for (var connectorId : List.of(0, 1, 2)) {
            var statusResponse = chargePoint.send(
                statusNotification(connectorId, status, ChargePointErrorCode.NO_ERROR),
                StatusNotificationResponse.class
            );
            assertNotNull(statusResponse);
        }
    }

    static void enterAuthorizedState(OcppJsonChargePoint chargePoint) {
        var authorize = new AuthorizeRequest().withIdTag(REGISTERED_OCPP_TAG);
        var authorizeResponse = chargePoint.send(authorize, AuthorizeResponse.class);

        assertNotNull(authorizeResponse);
        assertNotNull(authorizeResponse.getIdTagInfo());
        assertEquals(AuthorizationStatus.ACCEPTED, authorizeResponse.getIdTagInfo().getStatus());
    }

    static StartTransactionResponse enterChargingState(OcppJsonChargePoint chargePoint,
                                                       int connectorId,
                                                       String idTag,
                                                       int meterStart) {
        return enterChargingState(chargePoint, connectorId, idTag, meterStart, null);
    }

    static StartTransactionResponse enterChargingState(OcppJsonChargePoint chargePoint,
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
    static void expectGetConfCpoName(OcppJsonChargePoint chargePoint) {
        KeyValue kv = configurationKey(CpoName.name(), false, CPO_NAME);

        chargePoint.expectRequest(
            new GetConfigurationRequest().withKey(CpoName.name()),
            new GetConfigurationResponse().withConfigurationKey(List.of(kv))
        );
    }

    static OcppJsonChargePoint defaultStation() {
        return new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH);
    }

    static OcppJsonChargePoint defaultStationWithPwd(String basicAuthPassword) {
        return new OcppJsonChargePoint(OcppVersion.V_16, REGISTERED_CHARGE_BOX_ID, PATH, basicAuthPassword);
    }

    @FunctionalInterface
    interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    static <T> CompletableFuture<T> supplyAsyncUnchecked(ThrowingSupplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                log.warn("Async operation failed", e);
                throw new CompletionException("Async operation failed", e);
            }
        });
    }

    static <T> T successResponse(RestCallback<T> callback) {
        assertNotNull(callback);
        assertTrue(callback.getExceptionsByChargeBoxId().isEmpty());
        assertTrue(callback.getErrorResponsesByChargeBoxId().isEmpty());
        assertTrue(callback.getSuccessResponsesByChargeBoxId().containsKey(REGISTERED_CHARGE_BOX_ID));
        return callback.getSuccessResponsesByChargeBoxId().get(REGISTERED_CHARGE_BOX_ID);
    }

}
