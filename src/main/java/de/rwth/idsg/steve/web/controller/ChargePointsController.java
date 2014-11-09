package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Controller
public class ChargePointsController {

    @Autowired private ChargePointRepository chargePointRepository;

    @RequestMapping(value = "/chargepoints", method = RequestMethod.GET)
    public String getAbout(Model model) {
        model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());
        return "data-man/chargepoints";
    }

    @RequestMapping(value = "/chargepoints/add", method = RequestMethod.POST)
    public String add(@RequestParam String chargeBoxId) throws SteveException {
        chargePointRepository.addChargePoint(chargeBoxId);
        return "redirect:/manager/chargepoints";
    }

    @RequestMapping(value = "/chargepoints/delete", method = RequestMethod.POST)
    public String delete(@RequestParam String chargeBoxId) throws SteveException {
        chargePointRepository.deleteChargePoint(chargeBoxId);
        return "redirect:/manager/chargepoints";
    }
}