package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.service.ChargePointService15_Client;
import de.rwth.idsg.steve.web.dto.common.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.common.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.common.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.common.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.common.UpdateFirmwareParams;
import de.rwth.idsg.steve.web.dto.ocpp15.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ConfigurationKeyEnum;
import de.rwth.idsg.steve.web.dto.ocpp15.DataTransferParams;
import de.rwth.idsg.steve.web.dto.ocpp15.GetConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ReserveNowParams;
import de.rwth.idsg.steve.web.dto.ocpp15.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp15.SendLocalListParams;
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
 * @since 07.11.2014
 */
@Controller
@RequestMapping(value = "/operations/v1.5")
public class Ocpp15Controller {

    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ChargePointService15_Client client;

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

    private static final String RESERVE_PATH = "/ReserveNow";
    private static final String CANCEL_RESERV_PATH = "/CancelReservation";
    private static final String DATA_TRANSFER_PATH = "/DataTransfer";
    private static final String GET_CONF_PATH = "/GetConfiguration";
    private static final String GET_LIST_VERSION_PATH = "/GetLocalListVersion";
    private static final String SEND_LIST_PATH = "/SendLocalList";

    private static final String PREFIX = "op15";
    private static final String REDIRECT_TASKS_PATH = "redirect:/manager/operations/tasks/";

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void setChargePointList(Model model) {
        model.addAttribute("cpList", chargePointRepository.getChargePointsV15());
    }

    private void setUserIdTagList(Model model) {
        model.addAttribute("idTagList", userRepository.getUserIdTags());
    }

    // -------------------------------------------------------------------------
    // Http methods (GET)
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String getBase() {
        return "redirect:/manager/operations/v1.5/ChangeAvailability";
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
        setUserIdTagList(model);
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

    @RequestMapping(value = RESERVE_PATH, method = RequestMethod.GET)
    public String getReserveNow(Model model) {
        setChargePointList(model);
        setUserIdTagList(model);
        model.addAttribute("params", new ReserveNowParams());
        return PREFIX + RESERVE_PATH;
    }

    @RequestMapping(value = CANCEL_RESERV_PATH, method = RequestMethod.GET)
    public String getCancelReserv(Model model) {
        setChargePointList(model);
        model.addAttribute("params", new CancelReservationParams());
        return PREFIX + CANCEL_RESERV_PATH;
    }

    @RequestMapping(value = DATA_TRANSFER_PATH, method = RequestMethod.GET)
    public String getDataTransfer(Model model) {
        setChargePointList(model);
        model.addAttribute("params", new DataTransferParams());
        return PREFIX + DATA_TRANSFER_PATH;
    }

    @RequestMapping(value = GET_CONF_PATH, method = RequestMethod.GET)
    public String getGetConf(Model model) {
        setChargePointList(model);
        model.addAttribute("params", new GetConfigurationParams());
        model.addAttribute("confKeys", ConfigurationKeyEnum.values());
        return PREFIX + GET_CONF_PATH;
    }

    @RequestMapping(value = GET_LIST_VERSION_PATH, method = RequestMethod.GET)
    public String getListVersion(Model model) {
        setChargePointList(model);
        model.addAttribute("params", new MultipleChargePointSelect());
        return PREFIX + GET_LIST_VERSION_PATH;
    }

    @RequestMapping(value = SEND_LIST_PATH, method = RequestMethod.GET)
    public String getSendList(Model model) {
        setChargePointList(model);
        setUserIdTagList(model);
        model.addAttribute("params", new SendLocalListParams());
        return PREFIX + SEND_LIST_PATH;
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
            setUserIdTagList(model);
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

    @RequestMapping(value = RESERVE_PATH, method = RequestMethod.POST)
    public String postReserveNow(@Valid @ModelAttribute("params") ReserveNowParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            setUserIdTagList(model);
            return PREFIX + RESERVE_PATH;
        }
        return REDIRECT_TASKS_PATH + client.reserveNow(params);
    }

    @RequestMapping(value = CANCEL_RESERV_PATH, method = RequestMethod.POST)
    public String postCancelReserv(@Valid @ModelAttribute("params") CancelReservationParams params,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + CANCEL_RESERV_PATH;
        }
        return REDIRECT_TASKS_PATH + client.cancelReservation(params);
    }

    @RequestMapping(value = DATA_TRANSFER_PATH, method = RequestMethod.POST)
    public String postDataTransfer(@Valid @ModelAttribute("params") DataTransferParams params,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + DATA_TRANSFER_PATH;
        }
        return REDIRECT_TASKS_PATH + client.dataTransfer(params);
    }

    @RequestMapping(value = GET_CONF_PATH, method = RequestMethod.POST)
    public String postGetConf(@Valid @ModelAttribute("params") GetConfigurationParams params,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            model.addAttribute("confKeys", ConfigurationKeyEnum.values());
            return PREFIX + GET_CONF_PATH;
        }
        return REDIRECT_TASKS_PATH + client.getConfiguration(params);
    }

    @RequestMapping(value = GET_LIST_VERSION_PATH, method = RequestMethod.POST)
    public String postListVersion(@Valid @ModelAttribute("params") MultipleChargePointSelect params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + GET_LIST_VERSION_PATH;
        }
        return REDIRECT_TASKS_PATH + client.getLocalListVersion(params);
    }

    @RequestMapping(value = SEND_LIST_PATH, method = RequestMethod.POST)
    public String postSendList(@Valid @ModelAttribute("params") SendLocalListParams params,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            setUserIdTagList(model);
            return PREFIX + SEND_LIST_PATH;
        }
        return REDIRECT_TASKS_PATH + client.sendLocalList(params);
    }
}
