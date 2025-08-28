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
package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.config.DelegatingTaskScheduler;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.soap.CentralSystemService15_SoapServer;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStore;
import de.rwth.idsg.steve.ocpp.ws.WebSocketLogger;
import de.rwth.idsg.steve.ocpp.ws.pipeline.AbstractCallHandler;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Sender;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import lombok.RequiredArgsConstructor;
import ocpp.cs._2012._06.AuthorizeRequest;
import ocpp.cs._2012._06.BootNotificationRequest;
import ocpp.cs._2012._06.DataTransferRequest;
import ocpp.cs._2012._06.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2012._06.FirmwareStatusNotificationRequest;
import ocpp.cs._2012._06.HeartbeatRequest;
import ocpp.cs._2012._06.MeterValuesRequest;
import ocpp.cs._2012._06.StartTransactionRequest;
import ocpp.cs._2012._06.StatusNotificationRequest;
import ocpp.cs._2012._06.StopTransactionRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.03.2015
 */
@Component
public class Ocpp15WebSocketEndpoint extends AbstractWebSocketEndpoint {

    public Ocpp15WebSocketEndpoint(
            WebSocketLogger webSocketLogger,
            DelegatingTaskScheduler asyncTaskScheduler,
            OcppServerRepository ocppServerRepository,
            FutureResponseContextStore futureResponseContextStore,
            ApplicationEventPublisher applicationEventPublisher,
            SessionContextStore sessionContextStore,
            Sender sender,
            @Qualifier("ocppObjectMapper") ObjectMapper ocppMapper,
            CentralSystemService15_SoapServer ocpp15Server,
            Ocpp15TypeStore ocpp15TypeStore) {
        super(
                webSocketLogger,
                asyncTaskScheduler,
                ocppServerRepository,
                futureResponseContextStore,
                applicationEventPublisher,
                sessionContextStore,
                sender,
                ocppMapper,
                ocpp15TypeStore,
                new Ocpp15CallHandler(ocpp15Server));
    }

    @Override
    public OcppVersion getVersion() {
        return OcppVersion.V_15;
    }

    @RequiredArgsConstructor
    private static class Ocpp15CallHandler extends AbstractCallHandler {

        private final CentralSystemService15_SoapServer server;

        @Override
        protected ResponseType dispatch(RequestType params, String chargeBoxId) {
            return switch (params) {
                case BootNotificationRequest bootNotificationRequest ->
                    server.bootNotificationWithTransport(bootNotificationRequest, chargeBoxId, OcppProtocol.V_15_JSON);
                case FirmwareStatusNotificationRequest firmwareStatusNotificationRequest ->
                    server.firmwareStatusNotification(firmwareStatusNotificationRequest, chargeBoxId);
                case StatusNotificationRequest statusNotificationRequest ->
                    server.statusNotification(statusNotificationRequest, chargeBoxId);
                case MeterValuesRequest meterValuesRequest -> server.meterValues(meterValuesRequest, chargeBoxId);
                case DiagnosticsStatusNotificationRequest diagnosticsStatusNotificationRequest ->
                    server.diagnosticsStatusNotification(diagnosticsStatusNotificationRequest, chargeBoxId);
                case StartTransactionRequest startTransactionRequest ->
                    server.startTransaction(startTransactionRequest, chargeBoxId);
                case StopTransactionRequest stopTransactionRequest ->
                    server.stopTransaction(stopTransactionRequest, chargeBoxId);
                case HeartbeatRequest heartbeatRequest -> server.heartbeat(heartbeatRequest, chargeBoxId);
                case AuthorizeRequest authorizeRequest -> server.authorize(authorizeRequest, chargeBoxId);
                case DataTransferRequest dataTransferRequest -> server.dataTransfer(dataTransferRequest, chargeBoxId);
                default ->
                    throw new IllegalArgumentException(
                            "Unexpected RequestType, dispatch method not found for " + params);
            };
        }
    }
}
