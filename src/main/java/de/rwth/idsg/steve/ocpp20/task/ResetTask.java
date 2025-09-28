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

import de.rwth.idsg.steve.ocpp20.model.ResetEnum;
import de.rwth.idsg.steve.ocpp20.model.ResetRequest;
import de.rwth.idsg.steve.ocpp20.model.ResetResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class ResetTask extends Ocpp20Task<ResetRequest, ResetResponse> {

    private final ResetEnum resetType;
    private final Integer evseId;

    public ResetTask(List<String> chargeBoxIds, ResetEnum resetType) {
        this(chargeBoxIds, resetType, null);
    }

    public ResetTask(List<String> chargeBoxIds, ResetEnum resetType, Integer evseId) {
        super("Reset", chargeBoxIds);
        this.resetType = resetType;
        this.evseId = evseId;
    }

    @Override
    public ResetRequest createRequest() {
        ResetRequest request = new ResetRequest();
        request.setType(resetType);
        if (evseId != null) {
            request.setEvseId(evseId);
        }
        return request;
    }

    @Override
    public Class<ResetResponse> getResponseClass() {
        return ResetResponse.class;
    }
}