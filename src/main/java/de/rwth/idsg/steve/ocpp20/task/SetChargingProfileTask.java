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

import de.rwth.idsg.steve.ocpp20.model.*;
import lombok.Getter;
import java.util.List;

/**
 * OCPP 2.0 SetChargingProfile task implementation
 */
@Getter
public class SetChargingProfileTask extends Ocpp20Task<SetChargingProfileRequest, SetChargingProfileResponse> {

    private final Integer evseId;
    private final ChargingProfile chargingProfile;

    public SetChargingProfileTask(List<String> chargeBoxIds, Integer evseId, ChargingProfile chargingProfile) {
        super("SetChargingProfile", chargeBoxIds);
        this.evseId = evseId;
        this.chargingProfile = chargingProfile;
    }

    @Override
    public SetChargingProfileRequest createRequest() {
        SetChargingProfileRequest request = new SetChargingProfileRequest();
        request.setEvseId(evseId);
        request.setChargingProfile(chargingProfile);
        return request;
    }

    @Override
    public Class<SetChargingProfileResponse> getResponseClass() {
        return SetChargingProfileResponse.class;
    }
}