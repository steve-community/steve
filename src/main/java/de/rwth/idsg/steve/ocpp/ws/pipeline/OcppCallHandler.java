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
package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.ErrorFactory;
import de.rwth.idsg.steve.ocpp.ws.TypeStore;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResponse;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import org.slf4j.Logger;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.03.2015
 */
public interface OcppCallHandler {

    default OcppJsonResponse accept(CommunicationContext.InCall data) {
        OcppJsonCall call = data.call();
        String messageId = call.getMessageId();

        ResponseType response;
        try {
            response = dispatch(call.getPayload(), data.in().chargeBoxId());
        } catch (Exception e) {
            getLogger().error("Exception occurred", e);
            return ErrorFactory.payloadProcessingError(messageId, null);
        }

        OcppJsonResult result = new OcppJsonResult();
        result.setPayload(response);
        result.setMessageId(messageId);
        return result;
    }

    OcppVersion getVersion();

    TypeStore getTypeStore();

    Logger getLogger();

    ResponseType dispatch(RequestType params, String chargeBoxId);
}
