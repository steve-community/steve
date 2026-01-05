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

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.service.ChargePointServiceClient;
import de.rwth.idsg.steve.service.notification.OcppStationWebSocketConnected;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChargePointConfigUpdater {

    private final ChargePointServiceClient chargePointServiceClient;
    private final ChargePointService chargePointService;

    @EventListener
    public void getAndUpdateCpoName(OcppStationWebSocketConnected notification) {
        try {
            var ocppVersion = notification.getOcppVersion();
            if (ocppVersion == OcppVersion.V_12 || ocppVersion == OcppVersion.V_15) {
                return;
            }

            var chargeBoxId = notification.getChargeBoxId();
            var registration = chargePointService.getRegistrationDirect(chargeBoxId);
            if (registration.isEmpty()) {
                return;
            }

            // CpoName came with ocpp 1.6 security extension, which also introduced the security profiles. profile 0
            // represents the old world before the security extension. since it is not relevant, skip this task.
            if (registration.get().securityProfile() == OcppSecurityProfile.Profile_0) {
                return;
            }

            // Send request to get CpoName. Default impl of GetConfigurationTask takes care of updating the database.
            var params = new GetConfigurationParams();
            params.setChargePointSelectList(List.of(new ChargePointSelect(OcppProtocol.V_16_JSON, chargeBoxId)));
            params.setConfKeyList(List.of(ConfigurationKeyEnum.CpoName.name()));
            chargePointServiceClient.getConfiguration(params);
        } catch (Exception e) {
            log.error("Failed", e);
        }
    }
}
