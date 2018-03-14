package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.service.ChargePointService15_Client;
import de.rwth.idsg.steve.service.ChargePointService16_Client;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import de.rwth.idsg.steve.web.dto.ocpp.DataTransferParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ReserveNowParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;
import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.ocpp.UpdateFirmwareParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/manager/operations/v1.6")
public class Ocpp16Controller
{
    @Autowired private ChargePointHelperService chargePointHelperService;
    @Autowired private OcppTagRepository ocppTagRepository;

    @Autowired
    @Qualifier("ChargePointService16_Client")
    private ChargePointService16_Client client;

    private static final String PARAMS = "params";

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

    private static final String CLEAR_PROFILE_PATH = "/ClearChargingProfile";
    private static final String SET_PROFILE_PATH = "/SetChargingProfile";
    private static final String GET_SCHEDULE_PATH = "/GetCompositeSchedule";
    private static final String TRIGGER_MESSAGE_PATH = "/TriggerMessage";

    private static final String PREFIX = "op16";
    private static final String REDIRECT_TASKS_PATH = "redirect:/manager/operations/tasks/";

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void setChargePointList(Model model) {
        model.addAttribute("cpList", chargePointHelperService.getChargePointsV16());
    }

    private void setActiveUserIdTagList(Model model) {
        model.addAttribute("idTagList", ocppTagRepository.getActiveIdTags());
    }

    private void setAllUserIdTagList(Model model) {
        model.addAttribute("idTagList", ocppTagRepository.getIdTags());
    }

    private void setConfKeys(Model model) {
        model.addAttribute("confKeys", ConfigurationKeyEnum.values());
    }

    // -------------------------------------------------------------------------
    // Http methods (GET)
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String getBase() {
        return "redirect:/manager/operations/v1.6/ChangeAvailability";
    }

    @RequestMapping(value = CHANGE_AVAIL_PATH, method = RequestMethod.GET)
    public String getChangeAvail(Model model) {
        setChargePointList(model);
        model.addAttribute(PARAMS, new ChangeAvailabilityParams());
        return PREFIX + CHANGE_AVAIL_PATH;
    }

    @RequestMapping(value = CHANGE_CONF_PATH, method = RequestMethod.GET)
    public String getChangeConf(Model model) {
        setChargePointList(model);
        model.addAttribute(PARAMS, new ChangeConfigurationParams());
        model.addAttribute("ocpp16ConfKeys", ConfigurationKeyEnum.OCPP_16_MAP);
        return PREFIX + CHANGE_CONF_PATH;
    }

    @RequestMapping(value = CLEAR_CACHE_PATH, method = RequestMethod.GET)
    public String getClearCache(Model model) {
        setChargePointList(model);
        model.addAttribute(PARAMS, new MultipleChargePointSelect());
        return PREFIX + CLEAR_CACHE_PATH;
    }

    @RequestMapping(value = GET_DIAG_PATH, method = RequestMethod.GET)
    public String getGetDiag(Model model) {
        setChargePointList(model);
        model.addAttribute(PARAMS, new GetDiagnosticsParams());
        return PREFIX + GET_DIAG_PATH;
    }

    @RequestMapping(value = REMOTE_START_TX_PATH, method = RequestMethod.GET)
    public String getRemoteStartTx(Model model) {
        setChargePointList(model);
        setActiveUserIdTagList(model);
        model.addAttribute(PARAMS, new RemoteStartTransactionParams());
        return PREFIX + REMOTE_START_TX_PATH;
    }

    @RequestMapping(value = REMOTE_STOP_TX_PATH, method = RequestMethod.GET)
    public String getRemoteStopTx(Model model) {
        setChargePointList(model);
        model.addAttribute(PARAMS, new RemoteStopTransactionParams());
        return PREFIX + REMOTE_STOP_TX_PATH;
    }

    @RequestMapping(value = RESET_PATH, method = RequestMethod.GET)
    public String getReset(Model model) {
        setChargePointList(model);
        model.addAttribute(PARAMS, new ResetParams());
        return PREFIX + RESET_PATH;
    }

    @RequestMapping(value = UNLOCK_CON_PATH, method = RequestMethod.GET)
    public String getUnlockCon(Model model) {
        setChargePointList(model);
        model.addAttribute(PARAMS, new UnlockConnectorParams());
        return PREFIX + UNLOCK_CON_PATH;
    }

    @RequestMapping(value = UPDATE_FIRM_PATH, method = RequestMethod.GET)
    public String getUpdateFirm(Model model) {
        setChargePointList(model);
        model.addAttribute(PARAMS, new UpdateFirmwareParams());
        return PREFIX + UPDATE_FIRM_PATH;
    }

    @RequestMapping(value = RESERVE_PATH, method = RequestMethod.GET)
    public String getReserveNow(Model model) {
        setChargePointList(model);
        setActiveUserIdTagList(model);
        model.addAttribute(PARAMS, new ReserveNowParams());
        return PREFIX + RESERVE_PATH;
    }

    @RequestMapping(value = CANCEL_RESERV_PATH, method = RequestMethod.GET)
    public String getCancelReserv(Model model) {
        setChargePointList(model);
        model.addAttribute(PARAMS, new CancelReservationParams());
        return PREFIX + CANCEL_RESERV_PATH;
    }

    @RequestMapping(value = DATA_TRANSFER_PATH, method = RequestMethod.GET)
    public String getDataTransfer(Model model) {
        setChargePointList(model);
        model.addAttribute(PARAMS, new DataTransferParams());
        return PREFIX + DATA_TRANSFER_PATH;
    }

    @RequestMapping(value = GET_CONF_PATH, method = RequestMethod.GET)
    public String getGetConf(Model model) {
        setChargePointList(model);
        setConfKeys(model);
        model.addAttribute(PARAMS, new GetConfigurationParams());
        model.addAttribute("ocpp16ConfKeys", ConfigurationKeyEnum.OCPP_15_MAP);
        return PREFIX + GET_CONF_PATH;
    }

    @RequestMapping(value = GET_LIST_VERSION_PATH, method = RequestMethod.GET)
    public String getListVersion(Model model) {
        setChargePointList(model);
        model.addAttribute(PARAMS, new MultipleChargePointSelect());
        return PREFIX + GET_LIST_VERSION_PATH;
    }

    @RequestMapping(value = SEND_LIST_PATH, method = RequestMethod.GET)
    public String getSendList(Model model) {
        setChargePointList(model);
        setAllUserIdTagList(model);
        model.addAttribute(PARAMS, new SendLocalListParams());
        return PREFIX + SEND_LIST_PATH;
    }

    // -------------------------------------------------------------------------
    // Http methods (POST)
    // -------------------------------------------------------------------------

    @RequestMapping(value = CHANGE_AVAIL_PATH, method = RequestMethod.POST)
    public String postChangeAvail(@Valid @ModelAttribute(PARAMS) ChangeAvailabilityParams params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + CHANGE_AVAIL_PATH;
        }
        return REDIRECT_TASKS_PATH + client.changeAvailability(params);
    }

    @RequestMapping(value = CHANGE_CONF_PATH, method = RequestMethod.POST)
    public String postChangeConf(@Valid @ModelAttribute(PARAMS) ChangeConfigurationParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + CHANGE_CONF_PATH;
        }
        return REDIRECT_TASKS_PATH + client.changeConfiguration(params);
    }

    @RequestMapping(value = CLEAR_CACHE_PATH, method = RequestMethod.POST)
    public String postClearCache(@Valid @ModelAttribute(PARAMS) MultipleChargePointSelect params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + CLEAR_CACHE_PATH;
        }
        return REDIRECT_TASKS_PATH + client.clearCache(params);
    }

    @RequestMapping(value = GET_DIAG_PATH, method = RequestMethod.POST)
    public String postGetDiag(@Valid @ModelAttribute(PARAMS) GetDiagnosticsParams params,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + GET_DIAG_PATH;
        }
        return REDIRECT_TASKS_PATH + client.getDiagnostics(params);
    }

    @RequestMapping(value = REMOTE_START_TX_PATH, method = RequestMethod.POST)
    public String postRemoteStartTx(@Valid @ModelAttribute(PARAMS) RemoteStartTransactionParams params,
                                    BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            setActiveUserIdTagList(model);
            return PREFIX + REMOTE_START_TX_PATH;
        }
        return REDIRECT_TASKS_PATH + client.remoteStartTransaction(params);
    }

    @RequestMapping(value = REMOTE_STOP_TX_PATH, method = RequestMethod.POST)
    public String postRemoteStopTx(@Valid @ModelAttribute(PARAMS) RemoteStopTransactionParams params,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + REMOTE_STOP_TX_PATH;
        }
        return REDIRECT_TASKS_PATH + client.remoteStopTransaction(params);
    }

    @RequestMapping(value = RESET_PATH, method = RequestMethod.POST)
    public String postReset(@Valid @ModelAttribute(PARAMS) ResetParams params,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + RESET_PATH;
        }
        return REDIRECT_TASKS_PATH + client.reset(params);
    }

    @RequestMapping(value = UNLOCK_CON_PATH, method = RequestMethod.POST)
    public String postUnlockCon(@Valid @ModelAttribute(PARAMS) UnlockConnectorParams params,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + UNLOCK_CON_PATH;
        }
        return REDIRECT_TASKS_PATH + client.unlockConnector(params);
    }

    @RequestMapping(value = UPDATE_FIRM_PATH, method = RequestMethod.POST)
    public String postUpdateFirm(@Valid @ModelAttribute(PARAMS) UpdateFirmwareParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + UPDATE_FIRM_PATH;
        }
        return REDIRECT_TASKS_PATH + client.updateFirmware(params);
    }

    @RequestMapping(value = RESERVE_PATH, method = RequestMethod.POST)
    public String postReserveNow(@Valid @ModelAttribute(PARAMS) ReserveNowParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            setActiveUserIdTagList(model);
            return PREFIX + RESERVE_PATH;
        }
        return REDIRECT_TASKS_PATH + client.reserveNow(params);
    }

    @RequestMapping(value = CANCEL_RESERV_PATH, method = RequestMethod.POST)
    public String postCancelReserv(@Valid @ModelAttribute(PARAMS) CancelReservationParams params,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + CANCEL_RESERV_PATH;
        }
        return REDIRECT_TASKS_PATH + client.cancelReservation(params);
    }

    @RequestMapping(value = DATA_TRANSFER_PATH, method = RequestMethod.POST)
    public String postDataTransfer(@Valid @ModelAttribute(PARAMS) DataTransferParams params,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + DATA_TRANSFER_PATH;
        }
        return REDIRECT_TASKS_PATH + client.dataTransfer(params);
    }

    @RequestMapping(value = GET_CONF_PATH, method = RequestMethod.POST)
    public String postGetConf(@Valid @ModelAttribute(PARAMS) GetConfigurationParams params,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            setConfKeys(model);
            return PREFIX + GET_CONF_PATH;
        }
        return REDIRECT_TASKS_PATH + client.getConfiguration(params);
    }

    @RequestMapping(value = GET_LIST_VERSION_PATH, method = RequestMethod.POST)
    public String postListVersion(@Valid @ModelAttribute(PARAMS) MultipleChargePointSelect params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            return PREFIX + GET_LIST_VERSION_PATH;
        }
        return REDIRECT_TASKS_PATH + client.getLocalListVersion(params);
    }

    @RequestMapping(value = SEND_LIST_PATH, method = RequestMethod.POST)
    public String postSendList(@Valid @ModelAttribute(PARAMS) SendLocalListParams params,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            setChargePointList(model);
            setAllUserIdTagList(model);
            return PREFIX + SEND_LIST_PATH;
        }
        return REDIRECT_TASKS_PATH + client.sendLocalList(params);
    }
}
