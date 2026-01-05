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
package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.OcppEnabledCondition;
import de.rwth.idsg.steve.ocpp.soap.CentralSystemService15_SoapServer;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.SessionContextStoreHolder;
import de.rwth.idsg.steve.repository.OcppServerRepository;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.03.2015
 */
@Component
@Conditional(OcppEnabledCondition.V15.Json.class)
public class Ocpp15WebSocketEndpoint extends AbstractWebSocketEndpoint {

    private final CentralSystemService15_SoapServer server;

    public Ocpp15WebSocketEndpoint(TaskScheduler taskScheduler,
                                   OcppServerRepository ocppServerRepository,
                                   FutureResponseContextStore futureResponseContextStore,
                                   ApplicationEventPublisher applicationEventPublisher,
                                   CentralSystemService15_SoapServer server,
                                   SessionContextStoreHolder sessionContextStoreHolder) {
        super(taskScheduler, ocppServerRepository, futureResponseContextStore, applicationEventPublisher, sessionContextStoreHolder, Ocpp15TypeStore.INSTANCE);
        this.server = server;
    }

    @Override
    public OcppVersion getVersion() {
        return OcppVersion.V_15;
    }

    @Override
    public ResponseType dispatch(RequestType params, String chargeBoxId) {
        return switch (params) {
            case BootNotificationRequest request -> server.bootNotificationWithTransport(request, chargeBoxId, OcppProtocol.V_15_JSON);
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
