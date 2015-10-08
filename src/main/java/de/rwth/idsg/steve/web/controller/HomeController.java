package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Controller
@RequestMapping(value = "/manager/home", method = RequestMethod.GET)
public class HomeController {

    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private ChargePointHelperService chargePointHelperService;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String HEARTBEATS_PATH = "/heartbeats";
    private static final String CONNECTOR_STATUS_PATH = "/connectorStatus";
    private static final String OCPP_JSON_STATUS = "/ocppJsonStatus";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping
    public String getHome(Model model) {
        model.addAttribute("stats", chargePointHelperService.getStats());
        return "home";
    }

    @RequestMapping(value = HEARTBEATS_PATH)
    public String getHeartbeats(Model model) {
        model.addAttribute("heartbeatList", chargePointRepository.getChargePointHeartbeats());
        return "heartbeats";
    }

    @RequestMapping(value = CONNECTOR_STATUS_PATH)
    public String getConnectorStatus(Model model) {
        model.addAttribute("connectorStatusList", chargePointRepository.getChargePointConnectorStatus());
        return "connectorStatus";
    }

    @RequestMapping(value = OCPP_JSON_STATUS)
    public String getOcppJsonStatus(Model model) {
        model.addAttribute("ocppJsonStatusList", chargePointHelperService.getOcppJsonStatus());
        return "ocppJsonStatus";
    }
}
