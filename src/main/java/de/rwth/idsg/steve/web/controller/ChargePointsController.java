package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.web.dto.ChargeBoxForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

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
        model.addAttribute("chargeBoxAddForm", new ChargeBoxForm());
        model.addAttribute("chargeBoxDeleteForm", new ChargeBoxForm());
        return "data-man/chargepoints";
    }

    @RequestMapping(value = "/chargepoints/add", method = RequestMethod.POST)
    public String add(@Valid @ModelAttribute("chargeBoxAddForm") ChargeBoxForm chargeBoxForm,
                      BindingResult result, Model model) throws SteveException {

        if (result.hasErrors()) {
            model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());
            model.addAttribute("chargeBoxDeleteForm", new ChargeBoxForm());
            return "data-man/chargepoints";
        }

        chargePointRepository.addChargePoint(chargeBoxForm.getChargeBoxId());
        return "redirect:/manager/chargepoints";
    }

    @RequestMapping(value = "/chargepoints/delete", method = RequestMethod.POST)
    public String delete(@Valid @ModelAttribute("chargeBoxDeleteForm") ChargeBoxForm chargeBoxForm,
                         BindingResult result, Model model) throws SteveException {

        if (result.hasErrors()) {
            model.addAttribute("cpList", chargePointRepository.getChargeBoxIds());
            model.addAttribute("chargeBoxAddForm", new ChargeBoxForm());
            return "data-man/chargepoints";
        }

        chargePointRepository.deleteChargePoint(chargeBoxForm.getChargeBoxId());
        return "redirect:/manager/chargepoints";
    }
}