package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.web.dto.ChargeBoxForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Controller
@RequestMapping(value = "/manager/chargepoints")
public class ChargePointsController {

    @Autowired private ChargePointRepository chargePointRepository;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String ADD_PATH = "/add";
    private static final String UPDATE_PATH = "/update";
    private static final String DELETE_PATH = "/delete";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String getAbout(Model model) {
        model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());
        model.addAttribute("chargeBoxAddForm", new ChargeBoxForm());
        model.addAttribute("chargeBoxUpdateForm", new ChargeBoxForm());
        return "data-man/chargepoints";
    }

    @RequestMapping(value = ADD_PATH, method = RequestMethod.POST)
    public String add(@Valid @ModelAttribute("chargeBoxAddForm") ChargeBoxForm chargeBoxForm,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());
            return "data-man/chargepoints";
        }

        chargePointRepository.addChargePoint(chargeBoxForm);
        return "redirect:/manager/chargepoints";
    }

    @RequestMapping(value = UPDATE_PATH, method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("chargeBoxUpdateForm") ChargeBoxForm chargeBoxForm,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());
            return "data-man/chargepoints";
        }

        chargePointRepository.updateChargePoint(chargeBoxForm);
        return "redirect:/manager/chargepoints";
    }

    @RequestMapping(value = DELETE_PATH, method = RequestMethod.POST)
    public String delete(@RequestParam String chargeBoxId) {
        chargePointRepository.deleteChargePoint(chargeBoxId);
        return "redirect:/manager/chargepoints";
    }
}