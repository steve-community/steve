/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.service.ChargePointService12_Client;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyReadWriteEnum;
import de.rwth.idsg.steve.web.dto.ocpp.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
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
import java.util.Map;

import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyReadWriteEnum.RW;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2014
 */
@Controller
@RequestMapping(value = "/manager/operations/v1.2")
public class Ocpp12Controller {

    @Autowired protected ChargePointHelperService chargePointHelperService;
    @Autowired protected OcppTagRepository ocppTagRepository;

    @Autowired
    @Qualifier("ChargePointService12_Client")
    private ChargePointService12_Client client12;

    protected static final String PARAMS = "params";

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String CHANGE_AVAIL_PATH = "/ChangeAvailability";
    protected static final String CHANGE_CONF_PATH = "/ChangeConfiguration";
    private static final String CLEAR_CACHE_PATH = "/ClearCache";
    private static final String GET_DIAG_PATH = "/GetDiagnostics";
    private static final String REMOTE_START_TX_PATH = "/RemoteStartTransaction";
    private static final String REMOTE_STOP_TX_PATH = "/RemoteStopTransaction";
    private static final String RESET_PATH = "/Reset";
    private static final String UNLOCK_CON_PATH = "/UnlockConnector";
    private static final String UPDATE_FIRM_PATH = "/UpdateFirmware";

    protected static final String REDIRECT_TASKS_PATH = "redirect:/manager/operations/tasks/";

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    protected ChargePointService12_Client getClient12() {
        return client12;
    }

    protected void setCommonAttributesForTx(Model model) {
        setCommonAttributes(model);
    }

    protected void setCommonAttributes(Model model) {
        model.addAttribute("cpList", chargePointHelperService.getChargePoints(OcppVersion.V_12));
        model.addAttribute("opVersion", "v1.2");
    }

    protected Map<String, String> getConfigurationKeys(ConfigurationKeyReadWriteEnum confEnum) {
        // this conf enum is only relevant for versions >= occp 1.6
        return ConfigurationKeyEnum.OCPP_12_MAP;
    }

    protected String getRedirectPath() {
        return "redirect:/manager/operations/v1.2/ChangeAvailability";
    }

    protected String getPrefix() {
        return "op12";
    }

    protected void setActiveUserIdTagList(Model model) {
        model.addAttribute("idTagList", ocppTagRepository.getActiveIdTags());
    }

    // -------------------------------------------------------------------------
    // Http methods (GET)
    // -------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String getBase() {
        return getRedirectPath();
    }

    @RequestMapping(value = CHANGE_AVAIL_PATH, method = RequestMethod.GET)
    public String getChangeAvail(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new ChangeAvailabilityParams());
        return getPrefix() + CHANGE_AVAIL_PATH;
    }

    @RequestMapping(value = CHANGE_CONF_PATH, method = RequestMethod.GET)
    public String getChangeConf(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new ChangeConfigurationParams());
        model.addAttribute("ocppConfKeys", getConfigurationKeys(RW));
        return getPrefix() + CHANGE_CONF_PATH;
    }

    @RequestMapping(value = CLEAR_CACHE_PATH, method = RequestMethod.GET)
    public String getClearCache(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new MultipleChargePointSelect());
        return getPrefix() + CLEAR_CACHE_PATH;
    }

    @RequestMapping(value = GET_DIAG_PATH, method = RequestMethod.GET)
    public String getGetDiag(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new GetDiagnosticsParams());
        return getPrefix() + GET_DIAG_PATH;
    }

    @RequestMapping(value = REMOTE_START_TX_PATH, method = RequestMethod.GET)
    public String getRemoteStartTx(Model model) {
        setCommonAttributesForTx(model);
        setActiveUserIdTagList(model);
        model.addAttribute(PARAMS, new RemoteStartTransactionParams());
        return getPrefix() + REMOTE_START_TX_PATH;
    }

    @RequestMapping(value = REMOTE_STOP_TX_PATH, method = RequestMethod.GET)
    public String getRemoteStopTx(Model model) {
        setCommonAttributesForTx(model);
        model.addAttribute(PARAMS, new RemoteStopTransactionParams());
        return getPrefix() + REMOTE_STOP_TX_PATH;
    }

    @RequestMapping(value = RESET_PATH, method = RequestMethod.GET)
    public String getReset(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new ResetParams());
        return getPrefix() + RESET_PATH;
    }

    @RequestMapping(value = UNLOCK_CON_PATH, method = RequestMethod.GET)
    public String getUnlockCon(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new UnlockConnectorParams());
        return getPrefix() + UNLOCK_CON_PATH;
    }

    @RequestMapping(value = UPDATE_FIRM_PATH, method = RequestMethod.GET)
    public String getUpdateFirm(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new UpdateFirmwareParams());
        return getPrefix() + UPDATE_FIRM_PATH;
    }

    // -------------------------------------------------------------------------
    // Http methods (POST)
    // -------------------------------------------------------------------------

    @RequestMapping(value = CHANGE_AVAIL_PATH, method = RequestMethod.POST)
    public String postChangeAvail(@Valid @ModelAttribute(PARAMS) ChangeAvailabilityParams params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + CHANGE_AVAIL_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient12().changeAvailability(params);
    }

    @RequestMapping(value = CHANGE_CONF_PATH, method = RequestMethod.POST)
    public String postChangeConf(@Valid @ModelAttribute(PARAMS) ChangeConfigurationParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            model.addAttribute("ocppConfKeys", getConfigurationKeys(RW));
            return getPrefix() + CHANGE_CONF_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient12().changeConfiguration(params);
    }

    @RequestMapping(value = CLEAR_CACHE_PATH, method = RequestMethod.POST)
    public String postClearCache(@Valid @ModelAttribute(PARAMS) MultipleChargePointSelect params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + CLEAR_CACHE_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient12().clearCache(params);
    }

    @RequestMapping(value = GET_DIAG_PATH, method = RequestMethod.POST)
    public String postGetDiag(@Valid @ModelAttribute(PARAMS) GetDiagnosticsParams params,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + GET_DIAG_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient12().getDiagnostics(params);
    }

    @RequestMapping(value = REMOTE_START_TX_PATH, method = RequestMethod.POST)
    public String postRemoteStartTx(@Valid @ModelAttribute(PARAMS) RemoteStartTransactionParams params,
                                    BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributesForTx(model);
            setActiveUserIdTagList(model);
            return getPrefix() + REMOTE_START_TX_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient12().remoteStartTransaction(params);
    }

    @RequestMapping(value = REMOTE_STOP_TX_PATH, method = RequestMethod.POST)
    public String postRemoteStopTx(@Valid @ModelAttribute(PARAMS) RemoteStopTransactionParams params,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributesForTx(model);
            return getPrefix() + REMOTE_STOP_TX_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient12().remoteStopTransaction(params);
    }

    @RequestMapping(value = RESET_PATH, method = RequestMethod.POST)
    public String postReset(@Valid @ModelAttribute(PARAMS) ResetParams params,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + RESET_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient12().reset(params);
    }

    @RequestMapping(value = UNLOCK_CON_PATH, method = RequestMethod.POST)
    public String postUnlockCon(@Valid @ModelAttribute(PARAMS) UnlockConnectorParams params,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + UNLOCK_CON_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient12().unlockConnector(params);
    }

    @RequestMapping(value = UPDATE_FIRM_PATH, method = RequestMethod.POST)
    public String postUpdateFirm(@Valid @ModelAttribute(PARAMS) UpdateFirmwareParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + UPDATE_FIRM_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient12().updateFirmware(params);
    }
}
