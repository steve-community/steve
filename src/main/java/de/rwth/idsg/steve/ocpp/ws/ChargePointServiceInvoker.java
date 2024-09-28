/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
 * All Rights Reserved.
 *
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.ws.cluster.ClusterCommunicationMode;
import de.rwth.idsg.steve.ocpp.ws.cluster.ClusteredInvokerClient;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.pipeline.OutgoingCallPipeline;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.SneakyThrows;

import java.util.UUID;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 20.03.2015
 */
@Slf4j
@RequiredArgsConstructor
public class ChargePointServiceInvoker {

    private final OutgoingCallPipeline outgoingCallPipeline;
    @Getter
    private final AbstractWebSocketEndpoint endpoint;
    private final ClusteredInvokerClient clusteredInvokerClient;
    private final TypeStore typeStore;

    /**
     * Just a wrapper to make try-catch block and exception handling stand out
     */
    public void runPipeline(ChargePointSelect cps, CommunicationTask task,
                            ClusterCommunicationMode clusterCommunicationMode) {
        String chargeBoxId = cps.getChargeBoxId();
        try {
            run(chargeBoxId, task, clusterCommunicationMode);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            // Outgoing call failed due to technical problems. Pass the exception to handler to inform the user
            task.defaultCallback().failed(chargeBoxId, e);
        }
    }

    /**
     * Actual processing
     */
    private void run(String chargeBoxId, CommunicationTask task,
                     ClusterCommunicationMode clusterCommunicationMode) {
        RequestType request = task.getRequest();

        ActionResponsePair pair = typeStore.findActionResponse(request);
        if (pair == null) {
            throw new SteveException("Action name is not found");
        }

        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(UUID.randomUUID().toString());
        call.setPayload(request);
        call.setAction(pair.getAction());

        FutureResponseContext frc = new FutureResponseContext(task, pair.getResponseClass(),
                false, null);

        CommunicationContext context;
        if (clusterCommunicationMode != null) {
            context = new CommunicationContext(null, clusteredInvokerClient, chargeBoxId,
                    clusterCommunicationMode);

        } else {
            context = new CommunicationContext(endpoint.getSession(chargeBoxId), null,
                    chargeBoxId, null);
        }
        context.setOutgoingMessage(call);
        context.setFutureResponseContext(frc);

        outgoingCallPipeline.accept(context);
    }

    @SneakyThrows
    public void runRemote(String chargeBoxId, String outgoingString, String responseClassName, String messageId, String originPodIp) {
        Class<? extends ResponseType> responseClass= (Class<? extends ResponseType>) Class.forName(responseClassName);
        FutureResponseContext frc = new FutureResponseContext(null, responseClass,
                true, originPodIp);

        CommunicationContext context = new CommunicationContext(endpoint.getSession(chargeBoxId), clusteredInvokerClient, chargeBoxId,
                    ClusterCommunicationMode.REMOTE_SERVER);

        context.setFutureResponseContext(frc);
        context.setOutgoingString(outgoingString);
        context.setRemoteMessageId(messageId);
        outgoingCallPipeline.acceptRemote(context);
    }
}
