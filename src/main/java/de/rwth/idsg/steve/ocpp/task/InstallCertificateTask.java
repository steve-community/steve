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
import de.rwth.idsg.steve.web.dto.ocpp.InstallCertificateParams;

import jakarta.xml.ws.AsyncHandler;
import ocpp._2020._03.InstallCertificateRequest;
import ocpp._2020._03.InstallCertificateResponse;

public class InstallCertificateTask extends Ocpp16AndAboveTask<InstallCertificateParams, String> {

    public InstallCertificateTask(InstallCertificateParams params) {
        super(params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public InstallCertificateRequest getOcpp16Request() {
        var request = new InstallCertificateRequest();
        request.setCertificateType(InstallCertificateRequest.CertificateUseEnumType.valueOf(
                params.getCertificateType().toString()));
        request.setCertificate(params.getCertificate());
        return request;
    }

    @Override
    public AsyncHandler<InstallCertificateResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                var response = res.get();
                var status = response.getStatus() != null ? response.getStatus().toString() : "Unknown";
                success(chargeBoxId, status);
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
