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
package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.service.ChargePointServiceClient;
import de.rwth.idsg.steve.service.notification.OcppStationWebSocketConnected;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
            var registration = chargePointService.getRegistration(chargeBoxId);
            if (registration.isEmpty()) {
                return;
            }

            if (registration.get().securityProfile() != OcppSecurityProfile.Profile_3) {
                return;
            }

            var cpoName = getCpoName(chargeBoxId);
            chargePointService.updateCpoName(chargeBoxId, cpoName);
        } catch (Exception e) {
            log.error("Failed", e);
        }
    }

    /**
     * Fetches the CpoName config from the station by sending a GetConfigurationRequest and waiting for response.
     */
    @Nullable
    private String getCpoName(String chargeBoxId) throws Exception {
        var countDownLatch = new CountDownLatch(1);

        var callback = new OcppCallback<GetConfigurationTask.ResponseWrapper>() {

            private String cpoName = null;

            @Override
            public void success(String chargeBoxId, GetConfigurationTask.ResponseWrapper response) {
                for (GetConfigurationTask.KeyValue conf : response.getConfigurationKeys()) {
                    if (ConfigurationKeyEnum.CpoName.name().equals(conf.getKey())) {
                        cpoName = conf.getValue();
                        log.info("Received CpoName={}", cpoName);
                        break;
                    }
                }
                countDownLatch.countDown();
            }

            @Override
            public void success(String chargeBoxId, OcppJsonError error) {
                log.warn("Could not get configuration CpoName from charge point '{}', because: {}", chargeBoxId, error);
                countDownLatch.countDown();
            }

            @Override
            public void failed(String chargeBoxId, Exception e) {
                log.warn("Could not get configuration CpoName from charge point '{}'", chargeBoxId, e);
                countDownLatch.countDown();
            }
        };

        var params = new GetConfigurationParams();
        params.setChargePointSelectList(List.of(new ChargePointSelect(OcppProtocol.V_16_JSON, chargeBoxId)));
        params.setConfKeyList(List.of(ConfigurationKeyEnum.CpoName.name()));
        chargePointServiceClient.getConfiguration(params, callback);

        countDownLatch.await(30, TimeUnit.SECONDS);

        return callback.cpoName;
    }
}
