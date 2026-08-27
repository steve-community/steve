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
package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.pipeline.OutgoingPipeline;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Serializer;
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

    private final OutgoingPipeline outgoingPipeline;

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
        if (!cps.isJson()) {
            throw new SteveException("Not a JSON charge point");
        }

        var chargeBoxId = cps.getChargeBoxId();
        var request = task.getOcppRequest(chargeBoxId);
        var typeStore = TypeStore.getTypeStore(cps.getOcppProtocol().getVersion());

        var action = typeStore.findAction(request);
        if (action == null) {
            throw new SteveException("Action name is not found");
        }

        OcppJsonCall call = new OcppJsonCall();
        call.setMessageId(UUID.randomUUID().toString());
        call.setPayload(request);
        call.setAction(action);

        // Create the payload to send
        String callPayload = Serializer.INSTANCE.accept(call);

        var context = new CommunicationContext.OutCall(
            chargeBoxId,
            cps.getOcppProtocol(),
            task.getTaskUuid(),
            callPayload,
            call.getAction(),
            call.getMessageId()
        );

        outgoingPipeline.accept(context);
    }
}
