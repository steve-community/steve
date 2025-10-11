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
package de.rwth.idsg.steve.gateway.oicp.controller;

import de.rwth.idsg.steve.gateway.oicp.adapter.OcppToOicpAdapter;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingAcknowledgment;
import de.rwth.idsg.steve.gateway.oicp.model.cpo.ERoamingChargingNotificationsV11Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static de.rwth.idsg.steve.gateway.oicp.OicpResponse.errorCode;
import static de.rwth.idsg.steve.gateway.oicp.OicpResponse.toResponse;

/**
 * OICP v2.3 Charging Notifications REST Controller
 * Handles charging notifications and charge detail records
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class ChargingNotificationsController implements ERoamingChargingNotificationsApi {

    private final OcppToOicpAdapter ocppToOicpAdapter;

    @Override
    public ResponseEntity<ERoamingAcknowledgment> eRoamingChargingNotificationsV11(
            ERoamingChargingNotificationsV11Request request) {
        log.debug("Charging notification received: {}", request);

        try {
            return toResponse(ocppToOicpAdapter.processChargingNotification(request));
        } catch (Exception e) {
            log.error("Error processing charging notification", e);
            return toResponse(ERoamingAcknowledgment.builder()
                    .statusCode(errorCode("4000", "Unable to process charging notification"))
                    .result(false)
                    .build());
        }
    }
}
