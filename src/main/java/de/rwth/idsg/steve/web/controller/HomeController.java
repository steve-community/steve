package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.GenericRepository;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping(method = RequestMethod.GET)
@Slf4j
public class HomeController {

    @Autowired private GenericRepository genericRepository;
    @Autowired private ChargePointRepository chargePointRepository;

    @RequestMapping(value = {"", "/", "/home"})
    public String getHome(Model model) {
        model.addAttribute("stats", genericRepository.getStats());
        return "home";
    }

    @RequestMapping(value = "/home/heartbeats")
    public String getHeartbeats(Model model) {
        model.addAttribute("heartbeatList", chargePointRepository.getChargePointHeartbeats());
        return "heartbeats";
    }

    @RequestMapping(value = "/home/connectorStatus")
    public String getConnectorStatus(Model model) {
        model.addAttribute("connectorStatusList", chargePointRepository.getChargePointConnectorStatus());
        return "connectorStatus";
    }
}