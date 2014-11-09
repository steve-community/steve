package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.service.ChargePointService12_Client;
import de.rwth.idsg.steve.web.dto.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.CommunicationContext;
import ocpp.cp._2010._08.ChangeAvailabilityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Controller
@RequestMapping(value = "/operations/v1.2")
public class Ocpp12Controller {

    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ChargePointService12_Client client;

    private static final String PREFIX = "op12";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String BASE_PATH = "";
    private static final String CHANGE_AVAIL_PATH = "/ChangeAvailability";
    private static final String CHANGE_CONF_PATH = "/ChangeConfiguration";
    private static final String CLEAR_CACHE_PATH = "/ClearCache";
    private static final String GET_DIAG_PATH = "/GetDiagnostics";
    private static final String REMOTE_START_TX_PATH = "/RemoteStartTransaction";
    private static final String REMOTE_STOP_TX_PATH = "/RemoteStopTransaction";
    private static final String RESET_PATH = "/Reset";
    private static final String UNLOCK_CON_PATH = "/UnlockConnector";
    private static final String UPDATE_FIRM_PATH = "/UpdateFirmware";

    // -------------------------------------------------------------------------
    // Http methods (GET)
    // -------------------------------------------------------------------------

    private void setChargePointList(Model model) {
        model.addAttribute("cpList", chargePointRepository.getChargePointsV12());
    }

    @RequestMapping(value = BASE_PATH, method = RequestMethod.GET)
    public String getBase() {
        return "redirect:/manager/operations/v1.2/ChangeAvailability";
    }

    @RequestMapping(value = CHANGE_AVAIL_PATH, method = RequestMethod.GET)
    public String getChangeAvail(Model model) {
        setChargePointList(model);
        model.addAttribute("params", new ChangeAvailabilityParams());
        return PREFIX + CHANGE_AVAIL_PATH;
    }

    @RequestMapping(value = CHANGE_CONF_PATH, method = RequestMethod.GET)
    public String getChangeConf(Model model) {
        setChargePointList(model);
        return PREFIX + CHANGE_CONF_PATH;
    }

    @RequestMapping(value = CLEAR_CACHE_PATH, method = RequestMethod.GET)
    public String getClearCache(Model model) {
        setChargePointList(model);
        return PREFIX + CLEAR_CACHE_PATH;
    }

    @RequestMapping(value = GET_DIAG_PATH, method = RequestMethod.GET)
    public String getGetDiag(Model model) {
        setChargePointList(model);
        return PREFIX + GET_DIAG_PATH;
    }

    @RequestMapping(value = REMOTE_START_TX_PATH, method = RequestMethod.GET)
    public String getRemoteStartTx(Model model) {
        setChargePointList(model);
        model.addAttribute("userList", userRepository.getUsers());
        return PREFIX + REMOTE_START_TX_PATH;
    }

    @RequestMapping(value = REMOTE_STOP_TX_PATH, method = RequestMethod.GET)
    public String getRemoteStopTx(Model model) {
        setChargePointList(model);
        return PREFIX + REMOTE_STOP_TX_PATH;
    }

    @RequestMapping(value = RESET_PATH, method = RequestMethod.GET)
    public String getReset(Model model) {
        setChargePointList(model);
        return PREFIX + RESET_PATH;
    }

    @RequestMapping(value = UNLOCK_CON_PATH, method = RequestMethod.GET)
    public String getUnlockCon(Model model) {
        setChargePointList(model);
        return PREFIX + UNLOCK_CON_PATH;
    }

    @RequestMapping(value = UPDATE_FIRM_PATH, method = RequestMethod.GET)
    public String getUpdateFirm(Model model) {
        setChargePointList(model);
        return PREFIX + UPDATE_FIRM_PATH;
    }

    // -------------------------------------------------------------------------
    // Http methods (POST)
    //
    // chargePointItem[0] : chargebox id
    // chargePointItem[1] : endpoint (IP) address
    // -------------------------------------------------------------------------

    @RequestMapping(value = CHANGE_AVAIL_PATH, method = RequestMethod.POST)
    public String postChangeAvail(@Valid ChangeAvailabilityParams params, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return PREFIX + CHANGE_AVAIL_PATH;
        }

        ChangeAvailabilityRequest req = client.prepareChangeAvail(params);

        CommunicationContext context = new CommunicationContext("Change Availability");
        List<CommunicationContext.Response> responseList = context.getResponseList();
        for (String temp: params.getCp_items()) {
            String[] chargePointItem = temp.split(";");
            String responseValue = client.sendChangeAvailability(chargePointItem[0], chargePointItem[1], req);

            CommunicationContext.Response response = context.new Response(chargePointItem[0], responseValue);
            responseList.add(response);
        }

        model.addAttribute("context", context);

        //TODO add jsp
        return "response";
    }

}
