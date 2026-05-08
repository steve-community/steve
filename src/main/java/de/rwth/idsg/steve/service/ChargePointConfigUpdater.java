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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.notification.OcppStationSecurityBasicAuthChanged;
import de.rwth.idsg.steve.service.notification.OcppStationSecurityProfileChanged;
import de.rwth.idsg.steve.service.notification.OcppStationWebSocketConnected;
import de.rwth.idsg.steve.web.dto.RestCallback;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.ConfigurationStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.AuthorizationKey;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.SecurityProfile;

/**
 * Is excluded from test profile in order to not confuse ITs in {@link de.rwth.idsg.steve.certification.ocpp16} package.
 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class ChargePointConfigUpdater {

    private final ChargePointService chargePointService;

    // use this when we need to wait for the response to process it
    private final OcppOperationsService ocppOperationsService;
    // use this when fire-and-forget: no need to wait for or to process the response
    private final ChargePointServiceClient chargePointServiceClient;

    @EventListener
    public void getAllOcppConfigs(OcppStationWebSocketConnected notification) {
        try {
            // GetConfiguration was not there in Ocpp 1.2
            if (notification.getOcppVersion() == OcppVersion.V_12) {
                return;
            }

            var chargeBoxId = notification.getChargeBoxId();
            var protocol = notification.getOcppVersion().toProtocol(OcppTransport.JSON);

            var params = new GetConfigurationParams();
            params.setChargePointSelectList(List.of(new ChargePointSelect(protocol, chargeBoxId)));
            chargePointServiceClient.getConfiguration(params);
        } catch (Exception e) {
            log.error("Failed", e);
        }
    }

    /**
     * Needs to be blocking, must not be Async
     */
    @EventListener
    public void tryToChangeSecurityProfileAtStation(OcppStationSecurityProfileChanged event) {
        tryToChangeConfiguration(event.chargeBoxId(), SecurityProfile.value(), String.valueOf(event.newSecurityProfile().getValue()));
    }

    /**
     * Needs to be blocking, must not be Async
     */
    @EventListener
    public void tryToChangeAuthorizationKeyAtStation(OcppStationSecurityBasicAuthChanged event) {
        tryToChangeConfiguration(event.chargeBoxId(), AuthorizationKey.value(), event.newRawPassword());
    }

    private void tryToChangeConfiguration(String chargeBoxId, String configKey, String configValue) {
        log.debug("Trying to change config '{}' at station={}", configKey, chargeBoxId);

        var params = new ChangeConfigurationParams();
        params.setChargeBoxIdList(List.of(chargeBoxId));
        params.setKeyType(ChangeConfigurationParams.ConfigurationKeyType.PREDEFINED);
        params.setConfKey(configKey);
        params.setValue(configValue);

        final String prefix = "Database updated, but";

        try {
            RestCallback<ConfigurationStatus> callback = ocppOperationsService.changeConfiguration(params);
            if (!callback.isFinished()) {
                throw new SteveException("%s timed out while applying ChangeConfiguration for key '%s' at station '%s'".formatted(prefix, configKey, chargeBoxId));
            }

            var exception = callback.getExceptionsByChargeBoxId().get(chargeBoxId);
            if (exception != null) {
                throw new SteveException("%s failed to apply ChangeConfiguration for key '%s' at station '%s'".formatted(prefix, configKey, chargeBoxId), exception);
            }

            var error = callback.getErrorResponsesByChargeBoxId().get(chargeBoxId);
            if (error != null) {
                throw new SteveException("%s station '%s' returned CallError for ChangeConfiguration key '%s': %s".formatted(prefix, chargeBoxId, configKey, error));
            }

            var status = callback.getSuccessResponsesByChargeBoxId().get(chargeBoxId);
            if (status == null) {
                throw new SteveException("%s missing ChangeConfiguration response for key '%s' from station '%s'".formatted(prefix, configKey, chargeBoxId));
            }

            if (status != ConfigurationStatus.ACCEPTED) {
                throw new SteveException("%s station '%s' rejected ChangeConfiguration key '%s' with status '%s'".formatted(prefix, chargeBoxId, configKey, status.value()));
            }
        } catch (SteveException.BadRequest e) {
            log.warn("{} skipping ChangeConfiguration for key={} at offline/unreachable station={}", prefix, configKey, chargeBoxId);
        } catch (SteveException e) {
            throw e;
        } catch (Exception e) {
            throw new SteveException("%s failed to apply ChangeConfiguration for key '%s' at station '%s'".formatted(prefix, configKey, chargeBoxId), e);
        }
    }
}
