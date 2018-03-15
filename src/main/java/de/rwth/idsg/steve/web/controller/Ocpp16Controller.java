package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.service.ChargePointService12_Client;
import de.rwth.idsg.steve.service.ChargePointService15_Client;
import de.rwth.idsg.steve.service.ChargePointService16_Client;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.03.2018
 */
@Controller
@RequestMapping(value = "/manager/operations/v1.6")
public class Ocpp16Controller extends Ocpp15Controller {

    @Autowired
    @Qualifier("ChargePointService16_Client")
    private ChargePointService16_Client client16;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String GET_COMPOSITE_PATH = "/GetCompositeSchedule";
    private static final String CLEAR_CHARGING_PATH = "/ClearChargingProfile";
    private static final String SET_CHARGING_PATH = "/SetChargingProfile";
    private static final String TRIGGER_MESSAGE_PATH = "/TriggerMessage";

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    protected ChargePointService16_Client getClient16() {
        return client16;
    }

    @Override
    protected ChargePointService15_Client getClient15() {
        return client16;
    }

    @Override
    protected ChargePointService12_Client getClient12() {
        return client16;
    }

    @Override
    protected void setCommonAttributes(Model model) {
        model.addAttribute("cpList", chargePointHelperService.getChargePointsV16());
        model.addAttribute("opVersion", "v1.6");
    }

    @Override
    protected Map<String, String> getConfigurationKeys() {
        // TODO: will fix/change this later.
        return ConfigurationKeyEnum.OCPP_15_MAP;
    }

    @Override
    protected String getRedirectPath() {
        return "redirect:/manager/operations/v1.6/ChangeAvailability";
    }

    @Override
    protected String getPrefix() {
        return "op16";
    }

    // -------------------------------------------------------------------------
    // Http methods (GET)
    // -------------------------------------------------------------------------

    @RequestMapping(value = GET_COMPOSITE_PATH, method = RequestMethod.GET)
    public String getCompositePath(Model model) {
        model.addAttribute("opVersion", "v1.6");
        return getPrefix() + GET_COMPOSITE_PATH;
    }

    @RequestMapping(value = CLEAR_CHARGING_PATH, method = RequestMethod.GET)
    public String getClearChargingProfile(Model model) {
        model.addAttribute("opVersion", "v1.6");
        return getPrefix() + CLEAR_CHARGING_PATH;
    }

    @RequestMapping(value = SET_CHARGING_PATH, method = RequestMethod.GET)
    public String getSetChargingProfile(Model model) {
        model.addAttribute("opVersion", "v1.6");
        return getPrefix() + SET_CHARGING_PATH;
    }

    @RequestMapping(value = TRIGGER_MESSAGE_PATH, method = RequestMethod.GET)
    public String getTriggerMessage(Model model) {
        model.addAttribute("opVersion", "v1.6");
        return getPrefix() + TRIGGER_MESSAGE_PATH;
    }
}
