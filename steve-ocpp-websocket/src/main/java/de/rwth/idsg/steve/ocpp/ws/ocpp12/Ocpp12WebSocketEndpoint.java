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
package de.rwth.idsg.steve.ocpp.ws.ocpp12;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.config.DelegatingTaskScheduler;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.soap.CentralSystemService12_SoapServer;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStore;
import de.rwth.idsg.steve.ocpp.ws.pipeline.AbstractCallHandler;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Deserializer;
import de.rwth.idsg.steve.ocpp.ws.pipeline.IncomingPipeline;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Sender;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Serializer;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import lombok.RequiredArgsConstructor;
import ocpp.cs._2010._08.AuthorizeRequest;
import ocpp.cs._2010._08.BootNotificationRequest;
import ocpp.cs._2010._08.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2010._08.FirmwareStatusNotificationRequest;
import ocpp.cs._2010._08.HeartbeatRequest;
import ocpp.cs._2010._08.MeterValuesRequest;
import ocpp.cs._2010._08.StartTransactionRequest;
import ocpp.cs._2010._08.StatusNotificationRequest;
import ocpp.cs._2010._08.StopTransactionRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.03.2015
 */
@Component
public class Ocpp12WebSocketEndpoint extends AbstractWebSocketEndpoint {

    private final IncomingPipeline pipeline;

    public Ocpp12WebSocketEndpoint(
            DelegatingTaskScheduler asyncTaskScheduler,
            OcppServerRepository ocppServerRepository,
            FutureResponseContextStore futureResponseContextStore,
            ApplicationEventPublisher applicationEventPublisher,
            CentralSystemService12_SoapServer server,
            Ocpp12TypeStore typeStore,
            SessionContextStore sessionContextStore,
            @Qualifier("ocppObjectMapper") ObjectMapper ocppMapper,
            Sender sender) {
        super(
                asyncTaskScheduler,
                ocppServerRepository,
                futureResponseContextStore,
                applicationEventPublisher,
                sessionContextStore);
        var serializer = new Serializer(ocppMapper);
        var deserializer = new Deserializer(ocppMapper, futureResponseContextStore, typeStore);
        this.pipeline = new IncomingPipeline(serializer, deserializer, sender, new Ocpp12CallHandler(server));
    }

    @PostConstruct
    public void init() {
        super.init(pipeline);
    }

    @Override
    public OcppVersion getVersion() {
        return OcppVersion.V_12;
    }

    @RequiredArgsConstructor
    private static class Ocpp12CallHandler extends AbstractCallHandler {

        private final CentralSystemService12_SoapServer server;

        @Override
        protected ResponseType dispatch(RequestType params, String chargeBoxId) {
            return switch (params) {
                case BootNotificationRequest boot ->
                    server.bootNotificationWithTransport(boot, chargeBoxId, OcppProtocol.V_12_JSON);
                case FirmwareStatusNotificationRequest firmware ->
                    server.firmwareStatusNotification(firmware, chargeBoxId);
                case StatusNotificationRequest status -> server.statusNotification(status, chargeBoxId);
                case MeterValuesRequest mv -> server.meterValues(mv, chargeBoxId);
                case DiagnosticsStatusNotificationRequest diag ->
                    server.diagnosticsStatusNotification(diag, chargeBoxId);
                case StartTransactionRequest start -> server.startTransaction(start, chargeBoxId);
                case StopTransactionRequest stop -> server.stopTransaction(stop, chargeBoxId);
                case HeartbeatRequest hb -> server.heartbeat(hb, chargeBoxId);
                case AuthorizeRequest auth -> server.authorize(auth, chargeBoxId);
                default ->
                    throw new IllegalArgumentException(
                            "Unexpected RequestType, dispatch method not found for " + params);
            };
        }
    }
}
