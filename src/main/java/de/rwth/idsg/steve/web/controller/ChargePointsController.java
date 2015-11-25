package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.web.dto.Address;
import de.rwth.idsg.steve.web.dto.ChargeBoxForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    private static final String DETAILS_PATH = "/details/{chargeBoxId}";
    private static final String DELETE_PATH = "/delete/{chargeBoxId}";
    private static final String UPDATE_PATH = "/update";
    private static final String ADD_PATH = "/add";

    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String getOverview(Model model) {
        model.addAttribute("cpList", chargePointRepository.getOverview());
        return "data-man/chargepoints";
    }

    @RequestMapping(value = DETAILS_PATH, method = RequestMethod.GET)
    public String getDetails(@PathVariable("chargeBoxId") String chargeBoxId, Model model) {
        ChargePoint.Details cp = chargePointRepository.getDetails(chargeBoxId);

        ChargeBoxForm form = new ChargeBoxForm();
        form.setChargeBoxId(cp.getChargeBox().getChargeBoxId());
        form.setNote(cp.getChargeBox().getNote());
        form.setDescription(cp.getChargeBox().getDescription());
        form.setLocationLatitude(cp.getChargeBox().getLocationLatitude());
        form.setLocationLongitude(cp.getChargeBox().getLocationLongitude());

        Address address = new Address();
        if (cp.getAddress() != null) {
            address.setStreetAndHouseNumber(cp.getAddress().getStreetAndHouseNumber());
            address.setZipCode(cp.getAddress().getZipCode());
            address.setCity(cp.getAddress().getCity());
            address.setCountry(cp.getAddress().getCountry());
        }
        form.setAddress(address);

        model.addAttribute("chargeBoxUpdateForm", form);
        model.addAttribute("cp", cp);
        return "data-man/chargepointDetails";
    }

    @RequestMapping(value = ADD_PATH, method = RequestMethod.GET)
    public String addGet(Model model) {
        model.addAttribute("chargeBoxAddForm", new ChargeBoxForm());
        return "data-man/chargepointAdd";
    }

    @RequestMapping(params = "add", value = ADD_PATH, method = RequestMethod.POST)
    public String addPost(@Valid @ModelAttribute("chargeBoxAddForm") ChargeBoxForm chargeBoxForm,
                      BindingResult result) {
        if (result.hasErrors()) {
            return "data-man/chargepointAdd";
        }

        chargePointRepository.addChargePoint(chargeBoxForm);
        return toOverview();
    }

    @RequestMapping(params = "update", value = UPDATE_PATH, method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("chargeBoxUpdateForm") ChargeBoxForm chargeBoxForm,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "data-man/chargepointDetails";
        }

        chargePointRepository.updateChargePoint(chargeBoxForm);
        return toOverview();
    }

    @RequestMapping(value = DELETE_PATH, method = RequestMethod.POST)
    public String delete(@PathVariable("chargeBoxId") String chargeBoxId) {
        chargePointRepository.deleteChargePoint(chargeBoxId);
        return toOverview();
    }

    // -------------------------------------------------------------------------
    // Back to Overview
    // -------------------------------------------------------------------------

    @RequestMapping(params = "backToOverview", value = ADD_PATH, method = RequestMethod.POST)
    public String addBackToOverview() {
        return toOverview();
    }

    @RequestMapping(params = "backToOverview", value = UPDATE_PATH, method = RequestMethod.POST)
    public String updateBackToOverview() {
        return toOverview();
    }

    private String toOverview() {
        return "redirect:/manager/chargepoints";
    }
}
