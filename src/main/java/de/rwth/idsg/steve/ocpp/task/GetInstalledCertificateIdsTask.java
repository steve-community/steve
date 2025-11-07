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
import de.rwth.idsg.steve.repository.SecurityRepository;
import de.rwth.idsg.steve.web.dto.ocpp.GetInstalledCertificateIdsParams;
import ocpp._2022._02.security.CertificateHashData;
import ocpp._2022._02.security.GetInstalledCertificateIds;
import ocpp._2022._02.security.GetInstalledCertificateIdsResponse;

import jakarta.xml.ws.AsyncHandler;
import java.util.Collections;
import java.util.List;

public class GetInstalledCertificateIdsTask extends Ocpp16AndAboveTask<GetInstalledCertificateIdsParams, String> {

    private final SecurityRepository securityRepository;

    public GetInstalledCertificateIdsTask(GetInstalledCertificateIdsParams params,
                                          SecurityRepository securityRepository) {
        super(params);
        this.securityRepository = securityRepository;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public GetInstalledCertificateIds getOcpp16Request() {
        var request = new GetInstalledCertificateIds();
        request.setCertificateType(params.getCertificateType());
        return request;
    }

    @Override
    public AsyncHandler<GetInstalledCertificateIdsResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                var response = res.get();
                var status = response.getStatus().value();

                List<CertificateHashData> certificateHashData = (response.getCertificateHashData() == null)
                    ? Collections.emptyList()
                    : response.getCertificateHashData();

                success(chargeBoxId, status + " (" + certificateHashData.size() + " certificates)");

                if (certificateHashData.isEmpty()) {
                    return;
                }

                String certType = params.getCertificateType().value();
                securityRepository.deleteInstalledCertificates(chargeBoxId, certType);
                securityRepository.insertInstalledCertificates(chargeBoxId, certType, certificateHashData);

            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
