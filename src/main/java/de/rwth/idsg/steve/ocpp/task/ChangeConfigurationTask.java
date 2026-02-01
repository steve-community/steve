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
package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import ocpp.cp._2015._10.ConfigurationStatus;

import jakarta.xml.ws.AsyncHandler;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.AuthorizationKey;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum.CpoName;
import static ocpp.cp._2015._10.ConfigurationStatus.ACCEPTED;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.03.2018
 */
public class ChangeConfigurationTask extends CommunicationTask<ChangeConfigurationParams, ConfigurationStatus> {

    private final ChargePointService chargePointService;

    public ChangeConfigurationTask(ChangeConfigurationParams params, ChargePointService chargePointService) {
        super(params);
        this.chargePointService = chargePointService;
    }

    @Override
    public OcppCallback<ConfigurationStatus> defaultCallback() {
        return new DefaultOcppCallback<ConfigurationStatus>() {
            @Override
            public void success(String chargeBoxId, ConfigurationStatus response) {
                addNewResponse(chargeBoxId, response.value());
            }
        };
    }

    @Override
    public ocpp.cp._2010._08.ChangeConfigurationRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.ChangeConfigurationRequest()
                .withKey(params.getKey())
                .withValue(params.getValue());
    }

    @Override
    public ocpp.cp._2012._06.ChangeConfigurationRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.ChangeConfigurationRequest()
                .withKey(params.getKey())
                .withValue(params.getValue());
    }

    @Override
    public ocpp.cp._2015._10.ChangeConfigurationRequest getOcpp16Request() {
        String value = params.getValue();

        // https://github.com/steve-community/steve/issues/1895
        if (AuthorizationKey.name().equals(params.getKey())) {
            value = HexFormat.of().formatHex(value.getBytes(StandardCharsets.UTF_8));
        }

        return new ocpp.cp._2015._10.ChangeConfigurationRequest()
                .withKey(params.getKey())
                .withValue(value);
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.ChangeConfigurationResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, ConfigurationStatus.fromValue(res.get().getStatus().value()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.ChangeConfigurationResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, ConfigurationStatus.fromValue(res.get().getStatus().value()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.ChangeConfigurationResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                var status = res.get().getStatus();
                if (status == ACCEPTED && CpoName.name().equals(params.getKey())) {
                    chargePointService.updateCpoName(chargeBoxId, params.getValue());
                }
                success(chargeBoxId, status);
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
