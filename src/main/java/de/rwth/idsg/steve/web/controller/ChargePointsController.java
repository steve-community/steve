package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.web.dto.ChargePointBatchInsertForm;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
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

    @Autowired protected ChargePointRepository chargePointRepository;
    @Autowired protected ChargePointHelperService chargePointHelperService;

    protected static final String PARAMS = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    protected static final String QUERY_PATH = "/query";

    protected static final String DETAILS_PATH = "/details/{chargeBoxPk}";
    protected static final String DELETE_PATH = "/delete/{chargeBoxPk}";
    protected static final String UPDATE_PATH = "/update";
    protected static final String ADD_PATH = "/add";

    protected static final String ADD_SINGLE_PATH = "/add/single";
    protected static final String ADD_BATCH_PATH = "/add/batch";


    // -------------------------------------------------------------------------
    // HTTP methods
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String getOverview(Model model) {
        initList(model, new ChargePointQueryForm());
        return "data-man/chargepoints";
    }

    @RequestMapping(value = QUERY_PATH, method = RequestMethod.GET)
    public String getQuery(@ModelAttribute(PARAMS) ChargePointQueryForm params, Model model) {
        initList(model, params);
        return "data-man/chargepoints";
    }

    private void initList(Model model, ChargePointQueryForm params) {
        model.addAttribute(PARAMS, params);
        model.addAttribute("cpList", chargePointRepository.getOverview(params));
        model.addAttribute("unknownList", chargePointHelperService.getUnknownChargePoints());
    }

    @RequestMapping(value = DETAILS_PATH, method = RequestMethod.GET)
    public String getDetails(@PathVariable("chargeBoxPk") int chargeBoxPk, Model model) {
        ChargePoint.Details cp = chargePointRepository.getDetails(chargeBoxPk);

        ChargePointForm form = new ChargePointForm();
        form.setChargeBoxPk(cp.getChargeBox().getChargeBoxPk());
        form.setChargeBoxId(cp.getChargeBox().getChargeBoxId());
        form.setNote(cp.getChargeBox().getNote());
        form.setDescription(cp.getChargeBox().getDescription());
        form.setLocationLatitude(cp.getChargeBox().getLocationLatitude());
        form.setLocationLongitude(cp.getChargeBox().getLocationLongitude());

        form.setAddress(ControllerHelper.recordToDto(cp.getAddress()));

        model.addAttribute("chargePointForm", form);
        model.addAttribute("cp", cp);
        addCountryCodes(model);

        return "data-man/chargepointDetails";
    }

    @RequestMapping(value = ADD_PATH, method = RequestMethod.GET)
    public String addGet(Model model) {
        model.addAttribute("chargePointForm", new ChargePointForm());
        model.addAttribute("batchChargePointForm", new ChargePointBatchInsertForm());
        addCountryCodes(model);
        return "data-man/chargepointAdd";
    }

    @RequestMapping(params = "add", value = ADD_SINGLE_PATH, method = RequestMethod.POST)
    public String addSinglePost(@Valid @ModelAttribute("chargePointForm") ChargePointForm chargePointForm,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            addCountryCodes(model);
            model.addAttribute("batchChargePointForm", new ChargePointBatchInsertForm());
            return "data-man/chargepointAdd";
        }

        chargePointRepository.addChargePoint(chargePointForm);
        chargePointHelperService.removeUnknown(chargePointForm.getChargeBoxId());
        return toOverview();
    }

    @RequestMapping(value = ADD_BATCH_PATH, method = RequestMethod.POST)
    public String addBatchPost(@Valid @ModelAttribute("batchChargePointForm") ChargePointBatchInsertForm form,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            addCountryCodes(model);
            model.addAttribute("chargePointForm", new ChargePointForm());
            return "data-man/chargepointAdd";
        }

        chargePointRepository.addChargePointList(form.getIdList());
        chargePointHelperService.removeUnknown(form.getIdList());
        return toOverview();
    }

    @RequestMapping(params = "update", value = UPDATE_PATH, method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("chargePointForm") ChargePointForm chargePointForm,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            addCountryCodes(model);
            return "data-man/chargepointDetails";
        }

        chargePointRepository.updateChargePoint(chargePointForm);
        return toOverview();
    }

    @RequestMapping(value = DELETE_PATH, method = RequestMethod.POST)
    public String delete(@PathVariable("chargeBoxPk") int chargeBoxPk) {
        chargePointRepository.deleteChargePoint(chargeBoxPk);
        return toOverview();
    }

    protected void addCountryCodes(Model model) {
        model.addAttribute("countryCodes", ControllerHelper.COUNTRY_DROPDOWN);
    }

    // -------------------------------------------------------------------------
    // Back to Overview
    // -------------------------------------------------------------------------

    @RequestMapping(params = "backToOverview", value = ADD_SINGLE_PATH, method = RequestMethod.POST)
    public String addBackToOverview() {
        return toOverview();
    }

    @RequestMapping(params = "backToOverview", value = UPDATE_PATH, method = RequestMethod.POST)
    public String updateBackToOverview() {
        return toOverview();
    }

    protected String toOverview() {
        return "redirect:/manager/chargepoints";
    }
}
