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
import de.rwth.idsg.steve.repository.dto.StatusEvent;
import de.rwth.idsg.steve.service.CertificateSigningService;
import de.rwth.idsg.steve.web.dto.SecurityEventsQueryForm;
import de.rwth.idsg.steve.web.dto.StatusEventType;
import de.rwth.idsg.steve.web.dto.StatusEventsQueryForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/manager/security")
public class SecurityController {

    private final SecurityRepository securityRepository;
    private final ChargePointRepository chargePointRepository;
    private final SecurityProfileConfiguration securityConfig;
    private final CertificateSigningService certificateSigningService;

    private static final String PARAMS = "params";

    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public String getSecurityEvents(@Valid @ModelAttribute(PARAMS) SecurityEventsQueryForm params,
                                    BindingResult result, Model model) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());

        if (result.hasErrors()) {
            model.addAttribute("events", Collections.emptyList());
        } else {
            model.addAttribute("events", securityRepository.getSecurityEvents(params));
        }

        return "security-man/events";
    }

    @RequestMapping(value = "/statusEvents", method = RequestMethod.GET)
    public String getStatusEvents(@Valid @ModelAttribute(PARAMS) StatusEventsQueryForm params,
                                  BindingResult result, Model model) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());

        if (result.hasErrors()) {
            model.addAttribute("events", Collections.emptyList());
        } else {
            List<StatusEvent> events = securityRepository.getStatusEvents(params);
            model.addAttribute("events", events);
        }

        return "security-man/statusEvents";
    }

    @RequestMapping(value = "/statusEvents/{eventType}/{jobId}", method = RequestMethod.GET)
    public String getStatusEventJobDetails(@PathVariable("eventType") StatusEventType eventType,
                                           @PathVariable("jobId") int jobId,
                                           Model model) {
        var details = switch (eventType) {
            case FirmwareUpdate -> securityRepository.getFirmwareUpdateDetails(jobId);
            case LogUpload -> securityRepository.getLogUploadDetails(jobId);
        };

        model.addAttribute("eventType", eventType.name());
        model.addAttribute("details", details);
        return "security-man/statusEventJobDetails";
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

        return "security-man/certificates";
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
        model.addAttribute("signingServiceInitialized", certificateSigningService.isEnabled());

        return "security-man/configuration";
    }

}
