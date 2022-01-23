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
package de.rwth.idsg.steve.ocpp.ws.ocpp12;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.soap.CentralSystemService12_SoapServer;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.pipeline.AbstractCallHandler;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Deserializer;
import de.rwth.idsg.steve.ocpp.ws.pipeline.IncomingPipeline;
import lombok.AccessLevel;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.03.2015
 */
@Component
public class Ocpp12WebSocketEndpoint extends AbstractWebSocketEndpoint {

    @Autowired private CentralSystemService12_SoapServer server;
    @Autowired private FutureResponseContextStore futureResponseContextStore;

    @PostConstruct
    public void init() {
        Deserializer deserializer = new Deserializer(futureResponseContextStore, Ocpp12TypeStore.INSTANCE);
        IncomingPipeline pipeline = new IncomingPipeline(deserializer, new Ocpp12CallHandler(server));
        super.init(pipeline);
    }

    @Override
    public OcppVersion getVersion() {
        return OcppVersion.V_12;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Ocpp12CallHandler extends AbstractCallHandler {

        private final CentralSystemService12_SoapServer server;

        @Override
        protected ResponseType dispatch(RequestType params, String chargeBoxId) {
            ResponseType r;

            if (params instanceof BootNotificationRequest) {
                r = server.bootNotificationWithTransport((BootNotificationRequest) params, chargeBoxId, OcppProtocol.V_12_JSON);

            } else if (params instanceof FirmwareStatusNotificationRequest) {
                r = server.firmwareStatusNotification((FirmwareStatusNotificationRequest) params, chargeBoxId);

            } else if (params instanceof StatusNotificationRequest) {
                r = server.statusNotification((StatusNotificationRequest) params, chargeBoxId);

            } else if (params instanceof MeterValuesRequest) {
                r = server.meterValues((MeterValuesRequest) params, chargeBoxId);

            } else if (params instanceof DiagnosticsStatusNotificationRequest) {
                r = server.diagnosticsStatusNotification((DiagnosticsStatusNotificationRequest) params, chargeBoxId);

            } else if (params instanceof StartTransactionRequest) {
                r = server.startTransaction((StartTransactionRequest) params, chargeBoxId);

            } else if (params instanceof StopTransactionRequest) {
                r = server.stopTransaction((StopTransactionRequest) params, chargeBoxId);

            } else if (params instanceof HeartbeatRequest) {
                r = server.heartbeat((HeartbeatRequest) params, chargeBoxId);

            } else if (params instanceof AuthorizeRequest) {
                r = server.authorize((AuthorizeRequest) params, chargeBoxId);
            } else {
                throw new IllegalArgumentException("Unexpected RequestType, dispatch method not found");
            }

            return r;
        }
    }
}
