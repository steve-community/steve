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

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.CertificateRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointServiceClient;
import de.rwth.idsg.steve.web.dto.InstalledCertificateQueryForm;
import de.rwth.idsg.steve.web.dto.SignedCertificateQueryForm;
import de.rwth.idsg.steve.web.dto.ocpp.DeleteCertificateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 07.11.2025
 */
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/manager/certificates")
public class CertificatesController {

    private final CertificateRepository securityRepository;
    private final ChargePointRepository chargePointRepository;
    private final ChargePointServiceClient chargePointServiceClient;

    private static final String PARAMS = "params";

    @RequestMapping(value = "/signed", method = RequestMethod.GET)
    public String getSignedCertificates(@Valid @ModelAttribute(PARAMS) SignedCertificateQueryForm params,
                                        BindingResult result, Model model) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());

        if (result.hasErrors()) {
            model.addAttribute("certificates", Collections.emptyList());
        } else {
            model.addAttribute("certificates", securityRepository.getSignedCertificates(params));
        }

        return "events-certs/certificatesSigned";
    }

    @RequestMapping(value = "/installed", method = RequestMethod.GET)
    public String getInstalledCertificates(@Valid @ModelAttribute(PARAMS) InstalledCertificateQueryForm params,
                                           BindingResult result, Model model) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());

        if (result.hasErrors()) {
            model.addAttribute("certificates", Collections.emptyList());
        } else {
            model.addAttribute("certificates", securityRepository.getInstalledCertificates(params));
        }

        return "events-certs/certificatesInstalled";
    }

    @RequestMapping(value = "/installed/{chargeBoxId}/delete/{installedCertificateId}", method = RequestMethod.POST)
    public String deleteInstalledCertificate(@PathVariable("chargeBoxId") String chargeBoxId,
                                             @PathVariable("installedCertificateId") long installedCertificateId) {
        DeleteCertificateParams params = new DeleteCertificateParams();
        params.setChargePointSelectList(List.of(new ChargePointSelect(OcppProtocol.V_16_JSON, chargeBoxId)));
        params.setInstalledCertificateId(installedCertificateId);
        int taskId = chargePointServiceClient.deleteCertificate(params);

        return "redirect:/manager/operations/tasks/" + taskId;
    }

}
