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

import de.rwth.idsg.steve.ocpp.Ocpp16AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.web.dto.ocpp.SignedUpdateFirmwareParams;
import ocpp._2022._02.security.FirmwareType;
import ocpp._2022._02.security.SignedUpdateFirmware;
import ocpp._2022._02.security.SignedUpdateFirmwareResponse;

import jakarta.xml.ws.AsyncHandler;
import java.util.Map;

public class SignedUpdateFirmwareTask extends Ocpp16AndAboveTask<SignedUpdateFirmwareParams, String> {

    private final int requestId;

    public SignedUpdateFirmwareTask(SignedUpdateFirmwareParams params, int requestId) {
        super(params, Map.of("Job/Request ID", String.valueOf(requestId)));
        this.requestId = requestId;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public SignedUpdateFirmware getOcpp16Request() {
        var firmware = new FirmwareType();
        firmware.setLocation(params.getLocation());
        firmware.setRetrieveDateTime(params.getRetrieveDateTime());
        firmware.setInstallDateTime(params.getInstallDateTime());
        firmware.setSigningCertificate(params.getSigningCertificate());
        firmware.setSignature(params.getSignature());

        var request = new SignedUpdateFirmware();
        request.setRequestId(requestId);
        request.setFirmware(firmware);
        request.setRetries(params.getRetries());
        request.setRetryInterval(params.getRetryInterval());
        return request;
    }

    @Override
    public AsyncHandler<SignedUpdateFirmwareResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
