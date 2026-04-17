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
import de.rwth.idsg.steve.service.notification.OcppStationSecurityBasicAuthChanged;
import de.rwth.idsg.steve.service.notification.OcppStationSecurityProfileChanged;
import de.rwth.idsg.steve.web.dto.RestCallback;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.ConfigurationStatus;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.AuthorizationKey;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.SecurityProfile;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChargePointServiceConfigUpdater {

    private final OcppOperationsService ocppOperationsService;

    @EventListener
    public void tryToChangeSecurityProfileAtStation(OcppStationSecurityProfileChanged event) {
        tryToChangeConfiguration(event.chargeBoxId(), SecurityProfile.value(), String.valueOf(event.newSecurityProfile().getValue()));
    }

    @EventListener
    public void tryToChangeAuthorizationKeyAtStation(OcppStationSecurityBasicAuthChanged event) {
        tryToChangeConfiguration(event.chargeBoxId(), AuthorizationKey.value(), event.newRawPassword());
    }

    private void tryToChangeConfiguration(String chargeBoxId, String configKey, String configValue) {
        var params = new ChangeConfigurationParams();
        params.setChargeBoxIdList(List.of(chargeBoxId));
        params.setKeyType(ChangeConfigurationParams.ConfigurationKeyType.PREDEFINED);
        params.setConfKey(configKey);
        params.setValue(configValue);

        try {
            RestCallback<ConfigurationStatus> callback = ocppOperationsService.changeConfiguration(params);
            if (!callback.isFinished()) {
                throw new SteveException("Timed out while applying ChangeConfiguration for key '%s' to chargeBoxId '%s'".formatted(configKey, chargeBoxId));
            }

            var exception = callback.getExceptionsByChargeBoxId().get(chargeBoxId);
            if (exception != null) {
                throw new SteveException("Failed to apply ChangeConfiguration for key '%s' to chargeBoxId '%s'".formatted(configKey, chargeBoxId), exception);
            }

            var error = callback.getErrorResponsesByChargeBoxId().get(chargeBoxId);
            if (error != null) {
                throw new SteveException("Charge point '%s' returned CallError for ChangeConfiguration key '%s': %s".formatted(chargeBoxId, configKey, error));
            }

            var status = callback.getSuccessResponsesByChargeBoxId().get(chargeBoxId);
            if (status == null) {
                throw new SteveException("Missing ChangeConfiguration response for key '%s' from chargeBoxId '%s'".formatted(configKey, chargeBoxId));
            }

            if (status != ConfigurationStatus.ACCEPTED) {
                throw new SteveException("Charge point '%s' rejected ChangeConfiguration key '%s' with status '%s'".formatted(chargeBoxId, configKey, status.value()));
            }
        } catch (SteveException.BadRequest e) {
            log.warn("Skipping ChangeConfiguration for key={} on offline/unreachable chargeBoxId={}", configKey, chargeBoxId);
        } catch (SteveException e) {
            throw e;
        } catch (Exception e) {
            throw new SteveException("Failed to apply ChangeConfiguration for key '%s' to chargeBoxId '%s'".formatted(configKey, chargeBoxId), e);
        }
    }
}
