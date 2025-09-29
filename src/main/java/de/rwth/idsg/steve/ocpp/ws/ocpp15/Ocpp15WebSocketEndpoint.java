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

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.config.DelegatingTaskScheduler;
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.soap.CentralSystemService15_SoapServer;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
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
import org.springframework.stereotype.Component;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.03.2015
 */
@Component
public class Ocpp15WebSocketEndpoint extends AbstractWebSocketEndpoint {

    private final CentralSystemService15_SoapServer server;

    public Ocpp15WebSocketEndpoint(DelegatingTaskScheduler asyncTaskScheduler,
                                   OcppServerRepository ocppServerRepository,
                                   FutureResponseContextStore futureResponseContextStore,
                                   ApplicationEventPublisher applicationEventPublisher,
                                   CentralSystemService15_SoapServer server,
                                   SteveProperties steveProperties) {
        super(asyncTaskScheduler, ocppServerRepository, futureResponseContextStore, applicationEventPublisher, steveProperties, Ocpp15TypeStore.INSTANCE);
        this.server = server;
    }

    @Override
    public OcppVersion getVersion() {
        return OcppVersion.V_15;
    }

    @Override
    public ResponseType dispatch(RequestType params, String chargeBoxId) {
        return switch (params) {
            case BootNotificationRequest p -> server.bootNotificationWithTransport(p, chargeBoxId, OcppProtocol.V_15_JSON);
            case FirmwareStatusNotificationRequest p -> server.firmwareStatusNotification(p, chargeBoxId);
            case StatusNotificationRequest p -> server.statusNotification(p, chargeBoxId);
            case MeterValuesRequest p -> server.meterValues(p, chargeBoxId);
            case DiagnosticsStatusNotificationRequest p -> server.diagnosticsStatusNotification(p, chargeBoxId);
            case StartTransactionRequest p -> server.startTransaction(p, chargeBoxId);
            case StopTransactionRequest p -> server.stopTransaction(p, chargeBoxId);
            case HeartbeatRequest p -> server.heartbeat(p, chargeBoxId);
            case AuthorizeRequest p -> server.authorize(p, chargeBoxId);
            case DataTransferRequest p -> server.dataTransfer(p, chargeBoxId);
            default -> throw new IllegalArgumentException("Unexpected RequestType, dispatch method not found");
        };
    }
}
