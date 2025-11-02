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
package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.config.SecurityProfileConfiguration;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.SecurityRepository;
import de.rwth.idsg.steve.service.CertificateSigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/manager/security")
public class SecurityController {

    private final SecurityRepository securityRepository;
    private final ChargePointRepository chargePointRepository;
    private final SecurityProfileConfiguration securityConfig;
    private final CertificateSigningService certificateSigningService;

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public String getSecurityEvents(
            @RequestParam(value = "chargeBoxId", required = false) String chargeBoxId,
            @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            Model model) {

        model.addAttribute("events", securityRepository.getSecurityEvents(chargeBoxId, limit));
        model.addAttribute("chargeBoxIdList", chargePointRepository.getChargeBoxIds());
        model.addAttribute("selectedChargeBoxId", chargeBoxId);
        model.addAttribute("limit", limit);

        return "security/events";
    }

    @RequestMapping(value = "/certificates", method = RequestMethod.GET)
    public String getCertificates(
            @RequestParam(value = "chargeBoxId", required = false) String chargeBoxId,
            @RequestParam(value = "certificateType", required = false) String certificateType,
            Model model) {

        model.addAttribute("certificates", securityRepository.getInstalledCertificates(chargeBoxId, certificateType));
        model.addAttribute("chargeBoxIdList", chargePointRepository.getChargeBoxIds());
        model.addAttribute("selectedChargeBoxId", chargeBoxId);
        model.addAttribute("selectedCertificateType", certificateType);

        return "security/certificates";
    }

    @RequestMapping(value = "/certificates/delete/{certificateId}", method = RequestMethod.POST)
    public String deleteCertificate(@PathVariable("certificateId") int certificateId) {
        securityRepository.deleteCertificate(certificateId);
        return "redirect:/manager/security/certificates";
    }

    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    public String getConfiguration(Model model) {
        model.addAttribute("securityProfile", securityConfig.getSecurityProfile());
        model.addAttribute("tlsEnabled", securityConfig.isTlsEnabled());
        model.addAttribute("keystorePath", securityConfig.getKeystorePath());
        model.addAttribute("keystoreType", securityConfig.getKeystoreType());
        model.addAttribute("truststorePath", securityConfig.getTruststorePath());
        model.addAttribute("truststoreType", securityConfig.getTruststoreType());
        model.addAttribute("clientAuthRequired", securityConfig.isClientAuthRequired());
        model.addAttribute("tlsProtocols", String.join(", ", securityConfig.getTlsProtocols()));
        model.addAttribute("signingServiceInitialized", certificateSigningService.isInitialized());

        return "security/configuration";
    }

    @RequestMapping(value = "/firmware", method = RequestMethod.GET)
    public String getFirmwareUpdates(
            @RequestParam(value = "chargeBoxId", required = false) String chargeBoxId,
            Model model) {

        if (chargeBoxId != null && !chargeBoxId.isEmpty()) {
            model.addAttribute("currentUpdate", securityRepository.getCurrentFirmwareUpdate(chargeBoxId));
        }

        model.addAttribute("chargeBoxIdList", chargePointRepository.getChargeBoxIds());
        model.addAttribute("selectedChargeBoxId", chargeBoxId);

        return "security/firmware";
    }
}
