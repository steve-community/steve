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
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.service.ChargePointServiceClient;
import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.web.dto.ocpp.ExtendedTriggerMessageParams;
import ocpp.cs._2015._10.RegistrationStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Collections;

/**
 * Improved security for OCPP 1.6-J
 */
@Controller
@RequestMapping(value = "/manager/operations/v1.6")
public class Ocpp16ImprovedSecurityController extends Ocpp16Controller {

    public Ocpp16ImprovedSecurityController(OcppTagService ocppTagService,
                                            ChargePointService chargePointService,
                                            ChargePointServiceClient chargePointServiceClient,
                                            ChargingProfileRepository chargingProfileRepository) {
        super(ocppTagService, chargePointService, chargePointServiceClient, chargingProfileRepository);
    }

    private static final String EXTENDED_TRIGGER_MESSAGE_PATH = "/ExtendedTriggerMessage";

    /**
     * "Improved security for OCPP 1.6-J" white paper defines new operations only for JSON. Therefore, our station
     * selection should only contain JSON.
     */
    protected void setCommonAttributesForImprovedSecurity(Model model) {
        var inStatusFilter = Arrays.asList(RegistrationStatus.ACCEPTED, RegistrationStatus.PENDING);
        var chargePoints = chargePointService.getChargePoints(OcppProtocol.V_16_JSON, inStatusFilter, Collections.emptyList());

        model.addAttribute("cpList", chargePoints);
        model.addAttribute("opVersion", "v1.6");
    }

    @RequestMapping(value = EXTENDED_TRIGGER_MESSAGE_PATH, method = RequestMethod.GET)
    public String getExtendedTriggerMessage(Model model) {
        setCommonAttributesForImprovedSecurity(model);
        model.addAttribute(PARAMS, new ExtendedTriggerMessageParams());
        return getPrefix() + EXTENDED_TRIGGER_MESSAGE_PATH;
    }

    @RequestMapping(value = EXTENDED_TRIGGER_MESSAGE_PATH, method = RequestMethod.POST)
    public String postTriggerMessage(@Valid @ModelAttribute(PARAMS) ExtendedTriggerMessageParams params,
                                     BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributesForImprovedSecurity(model);
            return getPrefix() + EXTENDED_TRIGGER_MESSAGE_PATH;
        }
        return REDIRECT_TASKS_PATH + chargePointServiceClient.extendedTriggerMessage(params);
    }


}
