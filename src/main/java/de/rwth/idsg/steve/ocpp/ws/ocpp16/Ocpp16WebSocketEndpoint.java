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
package de.rwth.idsg.steve.ocpp.ws.ocpp16;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.config.DelegatingTaskScheduler;
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.soap.CentralSystemService16_SoapServer;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.DataTransferRequest;
import ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2015._10.FirmwareStatusNotificationRequest;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StopTransactionRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.03.2018
 */
@Component
public class Ocpp16WebSocketEndpoint extends AbstractWebSocketEndpoint {

    private final CentralSystemService16_SoapServer server;

    public Ocpp16WebSocketEndpoint(DelegatingTaskScheduler asyncTaskScheduler,
                                   OcppServerRepository ocppServerRepository,
                                   FutureResponseContextStore futureResponseContextStore,
                                   ApplicationEventPublisher applicationEventPublisher,
                                   CentralSystemService16_SoapServer server,
                                   SteveProperties steveProperties) {
        super(asyncTaskScheduler, ocppServerRepository, futureResponseContextStore, applicationEventPublisher, steveProperties, Ocpp16TypeStore.INSTANCE);
        this.server = server;
    }

    @Override
    public OcppVersion getVersion() {
        return OcppVersion.V_16;
    }

    @Override
    public ResponseType dispatch(RequestType params, String chargeBoxId) {
        return switch (params) {
            case BootNotificationRequest request -> server.bootNotificationWithTransport(request, chargeBoxId, OcppProtocol.V_16_JSON);
            case FirmwareStatusNotificationRequest request -> server.firmwareStatusNotification(request, chargeBoxId);
            case StatusNotificationRequest request -> server.statusNotification(request, chargeBoxId);
            case MeterValuesRequest request -> server.meterValues(request, chargeBoxId);
            case DiagnosticsStatusNotificationRequest request -> server.diagnosticsStatusNotification(request, chargeBoxId);
            case StartTransactionRequest request -> server.startTransaction(request, chargeBoxId);
            case StopTransactionRequest request -> server.stopTransaction(request, chargeBoxId);
            case HeartbeatRequest request -> server.heartbeat(request, chargeBoxId);
            case AuthorizeRequest request -> server.authorize(request, chargeBoxId);
            case DataTransferRequest request -> server.dataTransfer(request, chargeBoxId);
            case null, default ->
                throw new IllegalArgumentException("Unexpected RequestType, dispatch method not found");
        };
    }
}
