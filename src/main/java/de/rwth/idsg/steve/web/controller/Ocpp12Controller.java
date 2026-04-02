/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
import de.rwth.idsg.steve.ocpp.OcppEnabledCondition;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.service.ChargePointServiceClient;
import de.rwth.idsg.steve.service.OcppTagService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import java.util.Map;

import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyReadWriteEnum.W;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2014
 */
@Controller
@RequestMapping(value = "/manager/operations/v1.2")
@RequiredArgsConstructor
@Conditional(OcppEnabledCondition.V12.class)
public class Ocpp12Controller {

    protected final OcppTagService ocppTagService;
    protected final ChargePointService chargePointService;
    protected final ChargePointServiceClient chargePointServiceClient;

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

    /**
     * https://github.com/steve-community/steve/issues/1759
     * used to create form in order to send charging profile within remote start tx for ocpp 1.6.
     */
    protected void setCommonAttributesForRemoteStartTx(Model model) {
        // nothing to do for versions below 1.6
    }

    protected void setCommonAttributesForTx(Model model) {
        setCommonAttributes(model);
    }

    protected void setCommonAttributes(Model model) {
        model.addAttribute("cpList", chargePointService.getChargePoints(OcppVersion.V_12));
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
        model.addAttribute("idTagList", ocppTagService.getActiveIdTags());
    }

    // -------------------------------------------------------------------------
    // Http methods (GET)
    // -------------------------------------------------------------------------

    @GetMapping
    public String getBase() {
        return getRedirectPath();
    }

    @GetMapping(CHANGE_AVAIL_PATH)
    public String getChangeAvail(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new ChangeAvailabilityParams());
        return getPrefix() + CHANGE_AVAIL_PATH;
    }

    @GetMapping(CHANGE_CONF_PATH)
    public String getChangeConf(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new ChangeConfigurationParams());
        model.addAttribute("ocppConfKeys", getConfigurationKeys(W));
        return getPrefix() + CHANGE_CONF_PATH;
    }

    @GetMapping(CLEAR_CACHE_PATH)
    public String getClearCache(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new MultipleChargePointSelect());
        return getPrefix() + CLEAR_CACHE_PATH;
    }

    @GetMapping(GET_DIAG_PATH)
    public String getGetDiag(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new GetDiagnosticsParams());
        return getPrefix() + GET_DIAG_PATH;
    }

    @GetMapping(REMOTE_START_TX_PATH)
    public String getRemoteStartTx(Model model) {
        setCommonAttributesForTx(model);
        setActiveUserIdTagList(model);
        setCommonAttributesForRemoteStartTx(model);
        model.addAttribute(PARAMS, new RemoteStartTransactionParams());
        return getPrefix() + REMOTE_START_TX_PATH;
    }

    @GetMapping(REMOTE_STOP_TX_PATH)
    public String getRemoteStopTx(Model model) {
        setCommonAttributesForTx(model);
        model.addAttribute(PARAMS, new RemoteStopTransactionParams());
        return getPrefix() + REMOTE_STOP_TX_PATH;
    }

    @GetMapping(RESET_PATH)
    public String getReset(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new ResetParams());
        return getPrefix() + RESET_PATH;
    }

    @GetMapping(UNLOCK_CON_PATH)
    public String getUnlockCon(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new UnlockConnectorParams());
        return getPrefix() + UNLOCK_CON_PATH;
    }

    @GetMapping(UPDATE_FIRM_PATH)
    public String getUpdateFirm(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new UpdateFirmwareParams());
        return getPrefix() + UPDATE_FIRM_PATH;
    }

    // -------------------------------------------------------------------------
    // Http methods (POST)
    // -------------------------------------------------------------------------

    @PostMapping(CHANGE_AVAIL_PATH)
    public String postChangeAvail(@Valid @ModelAttribute(PARAMS) ChangeAvailabilityParams params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + CHANGE_AVAIL_PATH;
        }
        return REDIRECT_TASKS_PATH + chargePointServiceClient.changeAvailability(params);
    }

    @PostMapping(CHANGE_CONF_PATH)
    public String postChangeConf(@Valid @ModelAttribute(PARAMS) ChangeConfigurationParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            model.addAttribute("ocppConfKeys", getConfigurationKeys(W));
            return getPrefix() + CHANGE_CONF_PATH;
        }
        return REDIRECT_TASKS_PATH + chargePointServiceClient.changeConfiguration(params);
    }

    @PostMapping(CLEAR_CACHE_PATH)
    public String postClearCache(@Valid @ModelAttribute(PARAMS) MultipleChargePointSelect params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + CLEAR_CACHE_PATH;
        }
        return REDIRECT_TASKS_PATH + chargePointServiceClient.clearCache(params);
    }

    @PostMapping(GET_DIAG_PATH)
    public String postGetDiag(@Valid @ModelAttribute(PARAMS) GetDiagnosticsParams params,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + GET_DIAG_PATH;
        }
        return REDIRECT_TASKS_PATH + chargePointServiceClient.getDiagnostics(params);
    }

    @PostMapping(REMOTE_START_TX_PATH)
    public String postRemoteStartTx(@Valid @ModelAttribute(PARAMS) RemoteStartTransactionParams params,
                                    BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributesForTx(model);
            setActiveUserIdTagList(model);
            setCommonAttributesForRemoteStartTx(model);
            return getPrefix() + REMOTE_START_TX_PATH;
        }
        return REDIRECT_TASKS_PATH + chargePointServiceClient.remoteStartTransaction(params);
    }

    @PostMapping(REMOTE_STOP_TX_PATH)
    public String postRemoteStopTx(@Valid @ModelAttribute(PARAMS) RemoteStopTransactionParams params,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributesForTx(model);
            return getPrefix() + REMOTE_STOP_TX_PATH;
        }
        return REDIRECT_TASKS_PATH + chargePointServiceClient.remoteStopTransaction(params);
    }

    @PostMapping(RESET_PATH)
    public String postReset(@Valid @ModelAttribute(PARAMS) ResetParams params,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + RESET_PATH;
        }
        return REDIRECT_TASKS_PATH + chargePointServiceClient.reset(params);
    }

    @PostMapping(UNLOCK_CON_PATH)
    public String postUnlockCon(@Valid @ModelAttribute(PARAMS) UnlockConnectorParams params,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + UNLOCK_CON_PATH;
        }
        return REDIRECT_TASKS_PATH + chargePointServiceClient.unlockConnector(params);
    }

    @PostMapping(UPDATE_FIRM_PATH)
    public String postUpdateFirm(@Valid @ModelAttribute(PARAMS) UpdateFirmwareParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + UPDATE_FIRM_PATH;
        }
        return REDIRECT_TASKS_PATH + chargePointServiceClient.updateFirmware(params);
    }
}
