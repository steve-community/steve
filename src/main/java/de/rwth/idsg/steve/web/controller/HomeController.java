package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.GenericRepository;
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
@RequestMapping(value = "/home", method = RequestMethod.GET)
public class HomeController {

    @Autowired private GenericRepository genericRepository;
    @Autowired private ChargePointRepository chargePointRepository;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String HEARTBEATS_PATH = "/heartbeats";
    private static final String CONNECTOR_STATUS_PATH = "/connectorStatus";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping
    public String getHome(Model model) {
        model.addAttribute("stats", genericRepository.getStats());
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
}