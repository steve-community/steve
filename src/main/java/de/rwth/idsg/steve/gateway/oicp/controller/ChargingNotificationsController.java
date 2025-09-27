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

import de.rwth.idsg.steve.gateway.adapter.OcppToOicpAdapter;
import de.rwth.idsg.steve.gateway.oicp.model.ChargeDetailRecord;
import de.rwth.idsg.steve.gateway.oicp.model.ChargingNotification;
import de.rwth.idsg.steve.gateway.oicp.model.ChargingNotificationResponse;
import de.rwth.idsg.steve.gateway.oicp.model.OicpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OICP v2.3 Charging Notifications REST Controller
 * Handles charging notifications and charge detail records
 *
 * @author Steve Community
 */
@Slf4j
@RestController
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequestMapping(value = "/oicp/notificationmgmt/v11", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "OICP Charging Notifications", description = "OICP v2.3 Charging Notifications API - Session events and charge detail records")
public class ChargingNotificationsController {

    private final OcppToOicpAdapter ocppToOicpAdapter;

    @PostMapping("/charging-notifications")
    @Operation(summary = "Send charging notification", description = "Send real-time notifications about charging session events (start, progress, end)")
    public OicpResponse<ChargingNotificationResponse> sendChargingNotification(@RequestBody ChargingNotification notification) {
        log.debug("Charging notification received: {}", notification);

        try {
            ChargingNotificationResponse response = ocppToOicpAdapter.processChargingNotification(notification);
            return OicpResponse.success(response);
        } catch (Exception e) {
            log.error("Error processing charging notification", e);
            return OicpResponse.error("4000", "Unable to process charging notification");
        }
    }

    @PostMapping("/charge-detail-record")
    @Operation(summary = "Send charge detail record", description = "Submit detailed charging session data for billing and settlement")
    public OicpResponse<Boolean> sendChargeDetailRecord(@RequestBody ChargeDetailRecord cdr) {
        log.debug("Charge detail record received: {}", cdr);

        try {
            boolean success = ocppToOicpAdapter.processChargeDetailRecord(cdr);
            return OicpResponse.success(success);
        } catch (Exception e) {
            log.error("Error processing charge detail record", e);
            return OicpResponse.error("4000", "Unable to process charge detail record");
        }
    }
}