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
import de.rwth.idsg.steve.web.dto.ocpp.InstallCertificateParams;
import ocpp._2022._02.security.InstallCertificate;
import ocpp._2022._02.security.InstallCertificateResponse;
import ocpp._2022._02.security.InstallCertificateResponse.InstallCertificateStatusEnumType;

import jakarta.xml.ws.AsyncHandler;

public class InstallCertificateTask extends Ocpp16AndAboveTask<InstallCertificateParams, InstallCertificateStatusEnumType> {

    public InstallCertificateTask(InstallCertificateParams params) {
        super(params);
    }

    @Override
    public OcppCallback<InstallCertificateStatusEnumType> defaultCallback() {
        return new DefaultOcppCallback<InstallCertificateStatusEnumType>() {
            @Override
            public void success(String chargeBoxId, InstallCertificateStatusEnumType response) {
                addNewResponse(chargeBoxId, response.value());
            }
        };
    }

    @Override
    public InstallCertificate getOcpp16Request() {
        var request = new InstallCertificate();
        request.setCertificateType(params.getCertificateType());
        request.setCertificate(params.getCertificate());
        return request;
    }

    @Override
    public AsyncHandler<InstallCertificateResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
