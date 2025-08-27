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
import de.rwth.idsg.steve.service.ChargePointRegistrationService;
import de.rwth.idsg.steve.service.ChargePointsService;
import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.utils.mapper.ChargePointDetailsMapper;
import de.rwth.idsg.steve.web.dto.ChargePointBatchInsertForm;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 */
@Controller
@RequestMapping(value = "/manager/chargepoints")
@RequiredArgsConstructor
public class ChargePointsController {

    private final ChargePointsService chargePointsService;
    private final ChargePointRegistrationService chargePointRegistrationService;

    private static final String PARAMS = "params";

    private static final List<String> upToOcpp15RegistrationStatusList = Arrays.stream(
                    ocpp.cs._2012._06.RegistrationStatus.values())
            .map(ocpp.cs._2012._06.RegistrationStatus::value)
            .collect(Collectors.toList());

    private static final List<String> ocpp16RegistrationStatusList = Arrays.stream(
                    ocpp.cs._2015._10.RegistrationStatus.values())
            .map(ocpp.cs._2015._10.RegistrationStatus::value)
            .collect(Collectors.toList());

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String QUERY_PATH = "/query";

    private static final String DETAILS_PATH = "/details/{chargeBoxPk}";
    private static final String DELETE_PATH = "/delete/{chargeBoxPk}";
    private static final String UPDATE_PATH = "/update";
    private static final String ADD_PATH = "/add";

    private static final String ADD_SINGLE_PATH = "/add/single";
    private static final String ADD_BATCH_PATH = "/add/batch";

    // We need the slash at the end to support chargeBoxIds with dots etc. in them
    // Issue: https://github.com/steve-community/steve/issues/270
    // Solution: https://stackoverflow.com/a/18378817
    private static final String UNKNOWN_REMOVE_PATH = "/unknown/remove/{chargeBoxId}/";
    private static final String UNKNOWN_ADD_PATH = "/unknown/add/{chargeBoxId}/";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @GetMapping
    public String getOverview(Model model) {
        initList(model, new ChargePointQueryForm());
        return "data-man/chargepoints";
    }

    @GetMapping(value = QUERY_PATH)
    public String getQuery(@ModelAttribute(PARAMS) ChargePointQueryForm params, Model model) {
        initList(model, params);
        return "data-man/chargepoints";
    }

    private void initList(Model model, ChargePointQueryForm params) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("cpList", chargePointsService.getOverview(params));
        model.addAttribute("unknownList", chargePointRegistrationService.getUnknownChargePoints());
    }

    @GetMapping(value = DETAILS_PATH)
    public String getDetails(@PathVariable("chargeBoxPk") int chargeBoxPk, Model model) {
        var cp = chargePointsService.getDetails(chargeBoxPk);
        var form = ChargePointDetailsMapper.mapToForm(cp);

        model.addAttribute("chargePointForm", form);
        model.addAttribute("cp", cp);
        model.addAttribute("registrationStatusList", getRegistrationStatusList(cp.getChargeBox()));
        addCountryCodes(model);

        return "data-man/chargepointDetails";
    }

    private static List<String> getRegistrationStatusList(ChargeBoxRecord chargeBoxRecord) {
        if (chargeBoxRecord.getOcppProtocol() == null) {
            return upToOcpp15RegistrationStatusList;
        }

        var protocol = OcppProtocol.fromCompositeValue(chargeBoxRecord.getOcppProtocol());
        switch (protocol.getVersion()) {
            case V_12, V_15 -> {
                return upToOcpp15RegistrationStatusList;
            }
            case V_16 -> {
                return ocpp16RegistrationStatusList;
            }
            default -> throw new IllegalArgumentException("Unknown OCPP version: " + protocol.getVersion());
        }
    }

    @GetMapping(value = ADD_PATH)
    public String addGet(Model model) {
        model.addAttribute("chargePointForm", new ChargePointForm());
        setCommonAttributesForSingleAdd(model);
        return "data-man/chargepointAdd";
    }

    @GetMapping(params = "add", value = ADD_SINGLE_PATH)
    public String addSinglePost(
            @Valid @ModelAttribute("chargePointForm") ChargePointForm chargePointForm,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            setCommonAttributesForSingleAdd(model);
            return "data-man/chargepointAdd";
        }

        add(chargePointForm);
        return toOverview();
    }

    @PostMapping(value = ADD_BATCH_PATH)
    public String addBatchPost(
            @Valid @ModelAttribute("batchChargePointForm") ChargePointBatchInsertForm form,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            addCountryCodes(model);
            model.addAttribute("chargePointForm", new ChargePointForm());
            return "data-man/chargepointAdd";
        }

        add(form.getIdList());
        return toOverview();
    }

    @PostMapping(params = "update", value = UPDATE_PATH)
    public String update(
            @Valid @ModelAttribute("chargePointForm") ChargePointForm chargePointForm,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            addCountryCodes(model);
            return "data-man/chargepointDetails";
        }

        chargePointsService.updateChargePoint(chargePointForm);
        return toOverview();
    }

    @PostMapping(value = DELETE_PATH)
    public String delete(@PathVariable("chargeBoxPk") int chargeBoxPk) {
        chargePointsService.deleteChargePoint(chargeBoxPk);
        return toOverview();
    }

    @PostMapping(value = UNKNOWN_ADD_PATH)
    public String addUnknownChargeBoxId(@PathVariable("chargeBoxId") String chargeBoxId) {
        add(Collections.singletonList(chargeBoxId));
        return toOverview();
    }

    @PostMapping(value = UNKNOWN_REMOVE_PATH)
    public String removeUnknownChargeBoxId(@PathVariable("chargeBoxId") String chargeBoxId) {
        chargePointRegistrationService.removeUnknown(Collections.singletonList(chargeBoxId));
        return toOverview();
    }

    private static void addCountryCodes(Model model) {
        model.addAttribute("countryCodes", ControllerHelper.COUNTRY_DROPDOWN);
    }

    // -------------------------------------------------------------------------
    // Back to Overview
    // -------------------------------------------------------------------------

    @PostMapping(params = "backToOverview", value = ADD_SINGLE_PATH)
    public String addBackToOverview() {
        return toOverview();
    }

    @PostMapping(params = "backToOverview", value = UPDATE_PATH)
    public String updateBackToOverview() {
        return toOverview();
    }

    private static String toOverview() {
        return "redirect:/manager/chargepoints";
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void setCommonAttributesForSingleAdd(Model model) {
        addCountryCodes(model);
        model.addAttribute("batchChargePointForm", new ChargePointBatchInsertForm());
        // we don't know the protocol yet. but, a list with only "accepted" and "rejected" is a good starting point.
        model.addAttribute("registrationStatusList", upToOcpp15RegistrationStatusList);
    }

    private void add(ChargePointForm form) {
        chargePointsService.addChargePoint(form);
        chargePointRegistrationService.removeUnknown(Collections.singletonList(form.getChargeBoxId()));
    }

    private void add(List<String> idList) {
        chargePointsService.addChargePointList(idList);
        chargePointRegistrationService.removeUnknown(idList);
    }
}
