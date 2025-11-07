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
import de.rwth.idsg.steve.web.dto.ocpp.DeleteCertificateParams;
import lombok.extern.slf4j.Slf4j;
import ocpp._2022._02.security.CertificateHashDataType;
import ocpp._2022._02.security.DeleteCertificate;
import ocpp._2022._02.security.DeleteCertificateResponse;
import ocpp._2022._02.security.DeleteCertificateResponse.DeleteCertificateStatusEnumType;

import jakarta.xml.ws.AsyncHandler;

@Slf4j
public class DeleteCertificateTask extends Ocpp16AndAboveTask<DeleteCertificateParams, String> {

    private final SecurityRepository securityRepository;

    public DeleteCertificateTask(DeleteCertificateParams params,
                                 SecurityRepository securityRepository) {
        super(params);
        this.securityRepository = securityRepository;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public DeleteCertificate getOcpp16Request() {
        var record = securityRepository.getInstalledCertificateRecord(params.getInstalledCertificateId());

        var hashData = new CertificateHashDataType();
        hashData.setHashAlgorithm(CertificateHashDataType.HashAlgorithmEnumType.valueOf(record.getHashAlgorithm()));
        hashData.setIssuerNameHash(record.getIssuerNameHash());
        hashData.setIssuerKeyHash(record.getIssuerKeyHash());
        hashData.setSerialNumber(record.getSerialNumber());

        var request = new DeleteCertificate();
        request.setCertificateHashData(hashData);
        return request;
    }

    @Override
    public AsyncHandler<DeleteCertificateResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                var status = res.get().getStatus();
                success(chargeBoxId, status.value());

                if (status == DeleteCertificateStatusEnumType.ACCEPTED) {
                    log.info("Request accepted. Deleting from database...");
                    securityRepository.deleteInstalledCertificate(params.getInstalledCertificateId());
                }
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
