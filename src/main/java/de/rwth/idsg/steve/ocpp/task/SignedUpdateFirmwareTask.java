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
package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.Ocpp16AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.ws.data.security.Firmware;
import de.rwth.idsg.steve.ocpp.ws.data.security.SignedUpdateFirmwareRequest;
import de.rwth.idsg.steve.ocpp.ws.data.security.SignedUpdateFirmwareResponse;
import de.rwth.idsg.steve.web.dto.ocpp.SignedUpdateFirmwareParams;

import jakarta.xml.ws.AsyncHandler;

public class SignedUpdateFirmwareTask extends Ocpp16AndAboveTask<SignedUpdateFirmwareParams, String> {

    public SignedUpdateFirmwareTask(SignedUpdateFirmwareParams params) {
        super(params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public SignedUpdateFirmwareRequest getOcpp16Request() {
        SignedUpdateFirmwareRequest request = new SignedUpdateFirmwareRequest();
        request.setRequestId(params.getRequestId());

        Firmware firmware = new Firmware();
        firmware.setLocation(params.getFirmwareLocation());
        firmware.setRetrieveDateTime(params.getRetrieveDateTime().toString());
        firmware.setInstallDateTime(params.getInstallDateTime().toString());
        firmware.setSigningCertificate(params.getSigningCertificate());
        firmware.setSignature(params.getFirmwareSignature());

        request.setFirmware(firmware);

        if (params.getRetries() != null) {
            request.setRetries(params.getRetries());
        }
        if (params.getRetryInterval() != null) {
            request.setRetryInterval(params.getRetryInterval());
        }

        return request;
    }

    @Override
    public AsyncHandler<SignedUpdateFirmwareResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                SignedUpdateFirmwareResponse response = res.get();
                String status = response.getStatus() != null ? response.getStatus().toString() : "Unknown";
                success(chargeBoxId, status);
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}