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

import de.rwth.idsg.steve.gateway.repository.GatewayPartnerRepository;
import de.rwth.idsg.steve.web.dto.GatewayPartnerForm;
import de.rwth.idsg.steve.web.dto.GatewayPartnerQueryForm;
import jooq.steve.db.tables.records.GatewayPartnerRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
@RequestMapping(value = "/manager/gateway")
public class GatewayController {

    private final GatewayPartnerRepository gatewayPartnerRepository;

    private static final String PARAMS = "params";

    @RequestMapping(value = "/partners", method = RequestMethod.GET)
    public String getPartners(@ModelAttribute(PARAMS) GatewayPartnerQueryForm params, Model model) {
        List<GatewayPartnerRecord> partners = gatewayPartnerRepository.getPartners();
        model.addAttribute("partnerList", partners);
        return "data-man/gatewayPartners";
    }

    @RequestMapping(value = "/partners/add", method = RequestMethod.GET)
    public String addPartnerGet(Model model) {
        model.addAttribute(PARAMS, new GatewayPartnerForm());
        return "data-man/gatewayPartnerAdd";
    }

    @RequestMapping(value = "/partners/add", method = RequestMethod.POST)
    public String addPartnerPost(@Valid @ModelAttribute(PARAMS) GatewayPartnerForm params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "data-man/gatewayPartnerAdd";
        }

        gatewayPartnerRepository.addPartner(params);
        return "redirect:/manager/gateway/partners";
    }

    @RequestMapping(value = "/partners/details/{partnerId}", method = RequestMethod.GET)
    public String getPartnerDetails(@PathVariable("partnerId") Integer partnerId, Model model) {
        GatewayPartnerRecord partner = gatewayPartnerRepository.getPartner(partnerId);
        model.addAttribute("partner", partner);
        return "data-man/gatewayPartnerDetails";
    }

    @RequestMapping(value = "/partners/update", method = RequestMethod.POST)
    public String updatePartner(@Valid @ModelAttribute(PARAMS) GatewayPartnerForm params,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "data-man/gatewayPartnerDetails";
        }

        gatewayPartnerRepository.updatePartner(params);
        return "redirect:/manager/gateway/partners";
    }

    @RequestMapping(value = "/partners/delete/{partnerId}", method = RequestMethod.POST)
    public String deletePartner(@PathVariable("partnerId") Integer partnerId) {
        gatewayPartnerRepository.deletePartner(partnerId);
        return "redirect:/manager/gateway/partners";
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String getStatus(Model model) {
        model.addAttribute("ocpiEnabled", isOcpiEnabled());
        model.addAttribute("oicpEnabled", isOicpEnabled());
        return "data-man/gatewayStatus";
    }

    private boolean isOcpiEnabled() {
        return !gatewayPartnerRepository.findByProtocolAndEnabled(
            jooq.steve.db.enums.GatewayPartnerProtocol.OCPI, true
        ).isEmpty();
    }

    private boolean isOicpEnabled() {
        return !gatewayPartnerRepository.findByProtocolAndEnabled(
            jooq.steve.db.enums.GatewayPartnerProtocol.OICP, true
        ).isEmpty();
    }
}