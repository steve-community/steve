package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.service.ChargePointService12_Client;
import de.rwth.idsg.steve.web.dto.common.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.common.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.common.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.common.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.common.UpdateFirmwareParams;
import de.rwth.idsg.steve.web.dto.ocpp12.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp12.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp12.ResetParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

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

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String CHANGE_AVAIL_PATH = "/ChangeAvailability";
    private static final String CHANGE_CONF_PATH = "/ChangeConfiguration";
    private static final String CLEAR_CACHE_PATH = "/ClearCache";
    private static final String GET_DIAG_PATH = "/GetDiagnostics";
    private static final String REMOTE_START_TX_PATH = "/RemoteStartTransaction";
    private static final String REMOTE_STOP_TX_PATH = "/RemoteStopTransaction";
    private static final String RESET_PATH = "/Reset";
    private static final String UNLOCK_CON_PATH = "/UnlockConnector";
    private static final String UPDATE_FIRM_PATH = "/UpdateFirmware";

    private static final String PREFIX = "op12";
    private static final String REDIRECT_TASKS_PATH = "redirect:/manager/operations/tasks/";

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void setChargePointList(Model model) {
        model.addAttribute("cpList", chargePointRepository.getChargePointsV12());
    }

    private void setActiveUserIdTagList(Model model) {
        model.addAttribute("idTagList", userRepository.getActiveUserIdTags());
    }

    // -------------------------------------------------------------------------
    // Http methods (GET)
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
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
        model.addAttribute("params", new ChangeConfigurationParams());
        return PREFIX + CHANGE_CONF_PATH;
    }

    @RequestMapping(value = CLEAR_CACHE_PATH, method = RequestMethod.GET)
    public String getClearCache(Model model) {
        setChargePointList(model);
        model.addAttribute("params", new MultipleChargePointSelect());
        return PREFIX + CLEAR_CACHE_PATH;
    }

    @RequestMapping(value = GET_DIAG_PATH, method = RequestMethod.GET)
    public String getGetDiag(Model model) {
        setChargePointList(model);
        model.addAttribute("params", new GetDiagnosticsParams());
        return PREFIX + GET_DIAG_PATH;
    }

    @RequestMapping(value = REMOTE_START_TX_PATH, method = RequestMethod.GET)
    public String getRemoteStartTx(Model model) {
        setChargePointList(model);
        setActiveUserIdTagList(model);
        model.addAttribute("params", new RemoteStartTransactionParams());
        return PREFIX + REMOTE_START_TX_PATH;
    }

    @RequestMapping(value = REMOTE_STOP_TX_PATH, method = RequestMethod.GET)
    public String getRemoteStopTx(Model model) {
        setChargePointList(model);
        model.addAttribute("params", new RemoteStopTransactionParams());
        return PREFIX + REMOTE_STOP_TX_PATH;
    }

    @RequestMapping(value = RESET_PATH, method = RequestMethod.GET)
    public String getReset(Model model) {
        setChargePointList(model);
        model.addAttribute("params", new ResetParams());
        return PREFIX + RESET_PATH;
    }

    @RequestMapping(value = UNLOCK_CON_PATH, method = RequestMethod.GET)
    public String getUnlockCon(Model model) {
        setChargePointList(model);
        model.addAttribute("params", new UnlockConnectorParams());
        return PREFIX + UNLOCK_CON_PATH;
    }

    @RequestMapping(value = UPDATE_FIRM_PATH, method = RequestMethod.GET)
    public String getUpdateFirm(Model model) {
        setChargePointList(model);
        model.addAttribute("params", new UpdateFirmwareParams());
        return PREFIX + UPDATE_FIRM_PATH;
    }

    // -------------------------------------------------------------------------
    // Http methods (POST)
    // -------------------------------------------------------------------------

    @RequestMapping(value = CHANGE_AVAIL_PATH, method = RequestMethod.POST)
    public String postChangeAvail(@Valid @ModelAttribute("params") ChangeAvailabilityParams params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + CHANGE_AVAIL_PATH;
        }
        return REDIRECT_TASKS_PATH + client.changeAvailability(params);
    }

    @RequestMapping(value = CHANGE_CONF_PATH, method = RequestMethod.POST)
    public String postChangeConf(@Valid @ModelAttribute("params") ChangeConfigurationParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + CHANGE_CONF_PATH;
        }
        return REDIRECT_TASKS_PATH + client.changeConfiguration(params);
    }

    @RequestMapping(value = CLEAR_CACHE_PATH, method = RequestMethod.POST)
    public String postClearCache(@Valid @ModelAttribute("params") MultipleChargePointSelect params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + CLEAR_CACHE_PATH;
        }
        return REDIRECT_TASKS_PATH + client.clearCache(params);
    }

    @RequestMapping(value = GET_DIAG_PATH, method = RequestMethod.POST)
    public String postGetDiag(@Valid @ModelAttribute("params") GetDiagnosticsParams params,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + GET_DIAG_PATH;
        }
        return REDIRECT_TASKS_PATH + client.getDiagnostics(params);
    }

    @RequestMapping(value = REMOTE_START_TX_PATH, method = RequestMethod.POST)
    public String postRemoteStartTx(@Valid @ModelAttribute("params") RemoteStartTransactionParams params,
                                    BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            setActiveUserIdTagList(model);
            return PREFIX + REMOTE_START_TX_PATH;
        }
        return REDIRECT_TASKS_PATH + client.remoteStartTransaction(params);
    }

    @RequestMapping(value = REMOTE_STOP_TX_PATH, method = RequestMethod.POST)
    public String postRemoteStopTx(@Valid @ModelAttribute("params") RemoteStopTransactionParams params,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + REMOTE_STOP_TX_PATH;
        }
        return REDIRECT_TASKS_PATH + client.remoteStopTransaction(params);
    }

    @RequestMapping(value = RESET_PATH, method = RequestMethod.POST)
    public String postReset(@Valid @ModelAttribute("params") ResetParams params,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + RESET_PATH;
        }
        return REDIRECT_TASKS_PATH + client.reset(params);
    }

    @RequestMapping(value = UNLOCK_CON_PATH, method = RequestMethod.POST)
    public String postUnlockCon(@Valid @ModelAttribute("params") UnlockConnectorParams params,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + UNLOCK_CON_PATH;
        }
        return REDIRECT_TASKS_PATH + client.unlockConnector(params);
    }

    @RequestMapping(value = UPDATE_FIRM_PATH, method = RequestMethod.POST)
    public String postUpdateFirm(@Valid @ModelAttribute("params") UpdateFirmwareParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + UPDATE_FIRM_PATH;
        }
        return REDIRECT_TASKS_PATH + client.updateFirmware(params);
    }
}
