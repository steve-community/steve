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

import de.rwth.idsg.steve.ocpp20.model.UnlockConnectorRequest;
import de.rwth.idsg.steve.ocpp20.model.UnlockConnectorResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class UnlockConnectorTask extends Ocpp20Task<UnlockConnectorRequest, UnlockConnectorResponse> {

    private final Integer evseId;
    private final Integer connectorId;

    public UnlockConnectorTask(List<String> chargeBoxIds, Integer evseId, Integer connectorId) {
        super("UnlockConnector", chargeBoxIds);
        this.evseId = evseId;
        this.connectorId = connectorId;
    }

    @Override
    public UnlockConnectorRequest createRequest() {
        UnlockConnectorRequest request = new UnlockConnectorRequest();
        request.setEvseId(evseId);
        request.setConnectorId(connectorId);
        return request;
    }

    @Override
    public Class<UnlockConnectorResponse> getResponseClass() {
        return UnlockConnectorResponse.class;
    }
}