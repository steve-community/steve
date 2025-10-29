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
package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.Ocpp12TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.Ocpp15TypeStore;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.Ocpp16TypeStore;
import de.rwth.idsg.steve.ocpp.ws.pipeline.OutgoingCallPipeline;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 20.03.2015
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChargePointServiceJsonInvoker {

    private final OutgoingCallPipeline outgoingCallPipeline;
    private final SessionContextStoreHolder sessionContextStoreHolder;

    /**
     * Just a wrapper to make try-catch block and exception handling stand out
     */
    public void runPipeline(ChargePointSelect cps, CommunicationTask task) {
        try {
            run(cps, task);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            // Outgoing call failed due to technical problems. Pass the exception to handler to inform the user
            task.failed(cps.getChargeBoxId(), e);
        }
    }

    /**
     * Actual processing
     */
    private void run(ChargePointSelect cps, CommunicationTask task) {
        var chargeBoxId = cps.getChargeBoxId();

        var sessionStore = sessionContextStoreHolder.getOrCreate(cps.getOcppProtocol().getVersion());

        var typeStore = switch (cps.getOcppProtocol().getVersion()) {
            case V_12 -> Ocpp12TypeStore.INSTANCE;
            case V_15 -> Ocpp15TypeStore.INSTANCE;
            case V_16 -> Ocpp16TypeStore.INSTANCE;
        };

        RequestType request = switch (cps.getOcppProtocol().getVersion()) {
            case V_12 -> task.getOcpp12Request();
            case V_15 -> task.getOcpp15Request();
            case V_16 -> task.getOcpp16Request();
        };

        ActionResponsePair pair = typeStore.findActionResponse(request);
        if (pair == null) {
            throw new SteveException("Action name is not found");
        }

        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(UUID.randomUUID().toString());
        call.setPayload(request);
        call.setAction(pair.getAction());

        FutureResponseContext frc = new FutureResponseContext(task, pair.getResponseClass());

        CommunicationContext context = new CommunicationContext(sessionStore.getSession(chargeBoxId), chargeBoxId);
        context.setOutgoingMessage(call);
        context.setFutureResponseContext(frc);

        outgoingCallPipeline.accept(context);
    }
}
