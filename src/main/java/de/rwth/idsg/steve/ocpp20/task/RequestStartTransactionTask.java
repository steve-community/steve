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
package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.IdToken;
import de.rwth.idsg.steve.ocpp20.model.IdTokenEnum;
import de.rwth.idsg.steve.ocpp20.model.RequestStartTransactionRequest;
import de.rwth.idsg.steve.ocpp20.model.RequestStartTransactionResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class RequestStartTransactionTask extends Ocpp20Task<RequestStartTransactionRequest, RequestStartTransactionResponse> {

    private final Integer evseId;
    private final String idToken;
    private final String idTokenType;
    private final Integer remoteStartId;

    public RequestStartTransactionTask(List<String> chargeBoxIds, String idToken, Integer remoteStartId, Integer evseId) {
        super("RequestStartTransaction", chargeBoxIds);
        this.idToken = idToken;
        this.idTokenType = "ISO14443";
        this.remoteStartId = remoteStartId;
        this.evseId = evseId;
    }

    @Override
    public RequestStartTransactionRequest createRequest() {
        RequestStartTransactionRequest request = new RequestStartTransactionRequest();
        request.setEvseId(evseId);
        request.setRemoteStartId(remoteStartId);

        IdToken token = new IdToken();
        token.setIdToken(idToken);
        token.setType(IdTokenEnum.fromValue(idTokenType));
        request.setIdToken(token);

        return request;
    }

    @Override
    public Class<RequestStartTransactionResponse> getResponseClass() {
        return RequestStartTransactionResponse.class;
    }
}