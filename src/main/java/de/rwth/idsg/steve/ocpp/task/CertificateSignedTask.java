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
import de.rwth.idsg.steve.repository.CertificateRepository;
import de.rwth.idsg.steve.web.dto.ocpp.CertificateSignedParams;
import lombok.extern.slf4j.Slf4j;
import ocpp._2022._02.security.CertificateSigned;
import ocpp._2022._02.security.CertificateSignedResponse;
import ocpp._2022._02.security.CertificateSignedResponse.CertificateSignedStatusEnumType;

import jakarta.xml.ws.AsyncHandler;

@Slf4j
public class CertificateSignedTask extends Ocpp16AndAboveTask<CertificateSignedParams, String> {

    private final CertificateRepository certificateRepository;

    public CertificateSignedTask(CertificateSignedParams params,
                                 CertificateRepository certificateRepository) {
        super(params);
        this.certificateRepository = certificateRepository;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public CertificateSigned getOcpp16Request() {
        var request = new CertificateSigned();
        request.setCertificateChain(params.getCertificateChain());
        return request;
    }

    @Override
    public AsyncHandler<CertificateSignedResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                var status = res.get().getStatus();
                success(chargeBoxId, status.value());

                switch (status) {
                    case ACCEPTED -> log.info("Request was {} by charge point '{}'", status, chargeBoxId);
                    case REJECTED -> log.warn("Request was {} by charge point '{}'", status, chargeBoxId);
                }

                boolean accepted = (status == CertificateSignedStatusEnumType.ACCEPTED);
                certificateRepository.insertCertificateSignResponse(chargeBoxId, params.getCertificateId(), accepted);
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
