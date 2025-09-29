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

import de.rwth.idsg.steve.ocpp20.model.ChangeAvailabilityRequest;
import de.rwth.idsg.steve.ocpp20.model.ChangeAvailabilityResponse;
import de.rwth.idsg.steve.ocpp20.model.EVSE;
import de.rwth.idsg.steve.ocpp20.model.OperationalStatusEnum;
import lombok.Getter;

import java.util.List;

@Getter
public class ChangeAvailabilityTask extends Ocpp20Task<ChangeAvailabilityRequest, ChangeAvailabilityResponse> {

    private final OperationalStatusEnum operationalStatus;
    private final Integer evseId;
    private final Integer connectorId;

    public ChangeAvailabilityTask(List<String> chargeBoxIds, OperationalStatusEnum operationalStatus, Integer evseId, Integer connectorId) {
        super("ChangeAvailability", chargeBoxIds);
        this.operationalStatus = operationalStatus;
        this.evseId = evseId;
        this.connectorId = connectorId;
    }

    @Override
    public ChangeAvailabilityRequest createRequest() {
        ChangeAvailabilityRequest request = new ChangeAvailabilityRequest();
        request.setOperationalStatus(operationalStatus);

        if (evseId != null) {
            EVSE evse = new EVSE();
            evse.setId(evseId);
            if (connectorId != null) {
                evse.setConnectorId(connectorId);
            }
            request.setEvse(evse);
        }

        return request;
    }

    @Override
    public Class<ChangeAvailabilityResponse> getResponseClass() {
        return ChangeAvailabilityResponse.class;
    }
}