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

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.utils.ConnectorStatusCountFilter;
import de.rwth.idsg.steve.utils.ConnectorStatusFilter;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 */
@Controller
@RequestMapping(value = "/manager")
@RequiredArgsConstructor
public class HomeController {

    private final ChargePointRepository chargePointRepository;
    private final ChargePointHelperService chargePointHelperService;

    private static final String PARAMS = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String HOME_PREFIX = "/home";

    private static final String OCPP_JSON_STATUS = HOME_PREFIX + "/ocppJsonStatus";
    private static final String CONNECTOR_STATUS_PATH = HOME_PREFIX + "/connectorStatus";
    private static final String CONNECTOR_STATUS_QUERY_PATH = HOME_PREFIX + "/connectorStatus/query";
    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @GetMapping(value = {"", HOME_PREFIX})
    public String getHome(Model model) {
        model.addAttribute("stats", chargePointHelperService.getStats());
        return "home";
    }

    @GetMapping(value = CONNECTOR_STATUS_PATH)
    public String getConnectorStatus(Model model) {
        return getConnectorStatusQuery(new ConnectorStatusForm(), model);
    }

    @GetMapping(value = CONNECTOR_STATUS_QUERY_PATH)
    public String getConnectorStatusQuery(@ModelAttribute(PARAMS) ConnectorStatusForm params, Model model) {
        model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());
        model.addAttribute("statusValues", ConnectorStatusCountFilter.ALL_STATUS_VALUES);
        model.addAttribute(PARAMS, params);

        var latestList = chargePointHelperService.getChargePointConnectorStatus(params);
        List<ConnectorStatus> filteredList;
        if (params.getStrategy() == ConnectorStatusForm.Strategy.PreferZero) {
            filteredList = ConnectorStatusFilter.filterAndPreferZero(latestList);
        } else {
            filteredList = ConnectorStatusFilter.filterAndPreferOthersWithStatusOfZero(latestList);
        }
        model.addAttribute("connectorStatusList", filteredList);
        return "connectorStatus";
    }

    @GetMapping(value = OCPP_JSON_STATUS)
    public String getOcppJsonStatus(Model model) {
        model.addAttribute("ocppJsonStatusList", chargePointHelperService.getOcppJsonStatus());
        return "ocppJsonStatus";
    }
}
