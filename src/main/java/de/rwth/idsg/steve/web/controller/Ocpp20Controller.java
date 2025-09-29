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

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp20.service.Ocpp20TaskExecutor;
import de.rwth.idsg.steve.ocpp20.task.*;
import de.rwth.idsg.steve.ocpp20.model.*;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.OcppTagRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp20.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/manager/operations/v2.0")
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class Ocpp20Controller {

    private final ChargePointRepository chargePointRepository;
    private final OcppTagRepository ocppTagRepository;
    private final Ocpp20TaskExecutor taskExecutor;

    private static final String RESET_PATH = "/Reset";
    private static final String UNLOCK_CONNECTOR_PATH = "/UnlockConnector";
    private static final String REQUEST_START_PATH = "/RequestStartTransaction";
    private static final String REQUEST_STOP_PATH = "/RequestStopTransaction";
    private static final String GET_VARIABLES_PATH = "/GetVariables";
    private static final String SET_VARIABLES_PATH = "/SetVariables";
    private static final String TRIGGER_MESSAGE_PATH = "/TriggerMessage";
    private static final String CHANGE_AVAILABILITY_PATH = "/ChangeAvailability";
    private static final String CLEAR_CACHE_PATH = "/ClearCache";
    private static final String DATA_TRANSFER_PATH = "/DataTransfer";

    @ModelAttribute("ocppVersion")
    public OcppVersion getOcppVersion() {
        return OcppVersion.V_20;
    }

    @ModelAttribute("cpList")
    public List<ChargePointSelect> getChargePoints() {
        List<String> regStatusFilter = Collections.singletonList("Accepted");
        return chargePointRepository.getChargePointSelect(OcppProtocol.V_20_JSON, regStatusFilter);
    }

    @ModelAttribute("idTagList")
    public List<String> getIdTags() {
        return ocppTagRepository.getIdTags();
    }

    @RequestMapping(value = RESET_PATH, method = RequestMethod.GET)
    public String resetGet(Model model) {
        model.addAttribute("params", new ResetParams());
        return "op20/Reset";
    }

    @RequestMapping(value = RESET_PATH, method = RequestMethod.POST)
    public String resetPost(@Valid @ModelAttribute("params") ResetParams params,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/Reset";
        }

        ResetTask task = new ResetTask(params.getChargePointSelectList(), params.getResetType(), params.getEvseId());
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + RESET_PATH;
    }

    @RequestMapping(value = UNLOCK_CONNECTOR_PATH, method = RequestMethod.GET)
    public String unlockConnectorGet(Model model) {
        model.addAttribute("params", new UnlockConnectorParams());
        return "op20/UnlockConnector";
    }

    @RequestMapping(value = UNLOCK_CONNECTOR_PATH, method = RequestMethod.POST)
    public String unlockConnectorPost(@Valid @ModelAttribute("params") UnlockConnectorParams params,
                                     BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/UnlockConnector";
        }

        UnlockConnectorTask task = new UnlockConnectorTask(params.getChargePointSelectList(),
                                                           params.getEvseId(),
                                                           params.getConnectorId());
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + UNLOCK_CONNECTOR_PATH;
    }

    @RequestMapping(value = REQUEST_START_PATH, method = RequestMethod.GET)
    public String requestStartGet(Model model) {
        model.addAttribute("params", new RequestStartTransactionParams());
        return "op20/RequestStartTransaction";
    }

    @RequestMapping(value = REQUEST_START_PATH, method = RequestMethod.POST)
    public String requestStartPost(@Valid @ModelAttribute("params") RequestStartTransactionParams params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/RequestStartTransaction";
        }

        Integer remoteStartId = params.getRemoteStartId();
        RequestStartTransactionTask task = new RequestStartTransactionTask(
            params.getChargePointSelectList(),
            params.getIdTag(),
            remoteStartId,
            params.getEvseId()
        );
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + REQUEST_START_PATH;
    }

    @RequestMapping(value = REQUEST_STOP_PATH, method = RequestMethod.GET)
    public String requestStopGet(Model model) {
        model.addAttribute("params", new RequestStopTransactionParams());
        return "op20/RequestStopTransaction";
    }

    @RequestMapping(value = REQUEST_STOP_PATH, method = RequestMethod.POST)
    public String requestStopPost(@Valid @ModelAttribute("params") RequestStopTransactionParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/RequestStopTransaction";
        }

        RequestStopTransactionTask task = new RequestStopTransactionTask(
            params.getChargePointSelectList(),
            params.getTransactionId()
        );
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + REQUEST_STOP_PATH;
    }

    @RequestMapping(value = GET_VARIABLES_PATH, method = RequestMethod.GET)
    public String getVariablesGet(Model model) {
        model.addAttribute("params", new GetVariablesParams());
        return "op20/GetVariables";
    }

    @RequestMapping(value = GET_VARIABLES_PATH, method = RequestMethod.POST)
    public String getVariablesPost(@Valid @ModelAttribute("params") GetVariablesParams params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/GetVariables";
        }

        List<GetVariablesTask.VariableRequest> requests = new ArrayList<>();
        requests.add(new GetVariablesTask.VariableRequest(
            params.getComponentName(),
            params.getVariableName(),
            params.getAttributeType()
        ));

        GetVariablesTask task = new GetVariablesTask(params.getChargePointSelectList(), requests);
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_VARIABLES_PATH;
    }

    @RequestMapping(value = SET_VARIABLES_PATH, method = RequestMethod.GET)
    public String setVariablesGet(Model model) {
        model.addAttribute("params", new SetVariablesParams());
        return "op20/SetVariables";
    }

    @RequestMapping(value = SET_VARIABLES_PATH, method = RequestMethod.POST)
    public String setVariablesPost(@Valid @ModelAttribute("params") SetVariablesParams params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/SetVariables";
        }

        List<SetVariablesTask.VariableSet> sets = new ArrayList<>();
        sets.add(new SetVariablesTask.VariableSet(
            params.getComponentName(),
            params.getVariableName(),
            params.getAttributeValue(),
            params.getAttributeType()
        ));

        SetVariablesTask task = new SetVariablesTask(params.getChargePointSelectList(), sets);
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + SET_VARIABLES_PATH;
    }

    @RequestMapping(value = TRIGGER_MESSAGE_PATH, method = RequestMethod.GET)
    public String triggerMessageGet(Model model) {
        model.addAttribute("params", new TriggerMessageParams());
        return "op20/TriggerMessage";
    }

    @RequestMapping(value = TRIGGER_MESSAGE_PATH, method = RequestMethod.POST)
    public String triggerMessagePost(@Valid @ModelAttribute("params") TriggerMessageParams params,
                                    BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/TriggerMessage";
        }

        TriggerMessageTask task = new TriggerMessageTask(
            params.getChargePointSelectList(),
            params.getRequestedMessage(),
            params.getEvseId(),
            params.getConnectorId()
        );
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + TRIGGER_MESSAGE_PATH;
    }

    @RequestMapping(value = CHANGE_AVAILABILITY_PATH, method = RequestMethod.GET)
    public String changeAvailabilityGet(Model model) {
        model.addAttribute("params", new ChangeAvailabilityParams());
        return "op20/ChangeAvailability";
    }

    @RequestMapping(value = CHANGE_AVAILABILITY_PATH, method = RequestMethod.POST)
    public String changeAvailabilityPost(@Valid @ModelAttribute("params") ChangeAvailabilityParams params,
                                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/ChangeAvailability";
        }

        ChangeAvailabilityTask task = new ChangeAvailabilityTask(
            params.getChargePointSelectList(),
            params.getOperationalStatus(),
            params.getEvseId(),
            params.getConnectorId()
        );
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + CHANGE_AVAILABILITY_PATH;
    }

    @RequestMapping(value = CLEAR_CACHE_PATH, method = RequestMethod.GET)
    public String clearCacheGet(Model model) {
        model.addAttribute("params", new ClearCacheParams());
        return "op20/ClearCache";
    }

    @RequestMapping(value = CLEAR_CACHE_PATH, method = RequestMethod.POST)
    public String clearCachePost(@Valid @ModelAttribute("params") ClearCacheParams params,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/ClearCache";
        }

        ClearCacheTask task = new ClearCacheTask(params.getChargePointSelectList());
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + CLEAR_CACHE_PATH;
    }

    @RequestMapping(value = DATA_TRANSFER_PATH, method = RequestMethod.GET)
    public String dataTransferGet(Model model) {
        model.addAttribute("params", new DataTransferParams());
        return "op20/DataTransfer";
    }

    @RequestMapping(value = DATA_TRANSFER_PATH, method = RequestMethod.POST)
    public String dataTransferPost(@Valid @ModelAttribute("params") DataTransferParams params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/DataTransfer";
        }

        DataTransferTask task = new DataTransferTask(
            params.getChargePointSelectList(),
            params.getVendorId(),
            params.getMessageId(),
            params.getData()
        );
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + DATA_TRANSFER_PATH;
    }

    // -------------------------------------------------------------------------
    // GetBaseReport
    // -------------------------------------------------------------------------

    private static final String GET_BASE_REPORT_PATH = "/GetBaseReport";

    @RequestMapping(value = GET_BASE_REPORT_PATH, method = RequestMethod.GET)
    public String getBaseReportGet(Model model) {
        model.addAttribute("params", new GetBaseReportParams());
        return "op20/GetBaseReport";
    }

    @RequestMapping(value = GET_BASE_REPORT_PATH, method = RequestMethod.POST)
    public String getBaseReportPost(@Valid @ModelAttribute("params") GetBaseReportParams params,
                                    BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/GetBaseReport";
        }

        ReportBaseEnum reportBase = ReportBaseEnum.fromValue(params.getReportBase());
        GetBaseReportTask task = new GetBaseReportTask(
            params.getChargePointSelectList(),
            params.getRequestId(),
            reportBase
        );
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_BASE_REPORT_PATH;
    }

    // -------------------------------------------------------------------------
    // GetReport
    // -------------------------------------------------------------------------

    private static final String GET_REPORT_PATH = "/GetReport";

    @RequestMapping(value = GET_REPORT_PATH, method = RequestMethod.GET)
    public String getReportGet(Model model) {
        model.addAttribute("params", new GetReportParams());
        return "op20/GetReport";
    }

    @RequestMapping(value = GET_REPORT_PATH, method = RequestMethod.POST)
    public String getReportPost(@Valid @ModelAttribute("params") GetReportParams params,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/GetReport";
        }

        List<ComponentVariable> componentVariables = null;
        if (params.getComponentName() != null && !params.getComponentName().isEmpty()) {
            Component component = new Component();
            component.setName(params.getComponentName());

            Variable variable = null;
            if (params.getVariableName() != null && !params.getVariableName().isEmpty()) {
                variable = new Variable();
                variable.setName(params.getVariableName());
            }

            ComponentVariable cv = new ComponentVariable();
            cv.setComponent(component);
            cv.setVariable(variable);
            componentVariables = Collections.singletonList(cv);
        }

        List<ComponentCriterionEnum> componentCriteria = null;
        if (params.getComponentCriteria() != null && !params.getComponentCriteria().isEmpty()) {
            componentCriteria = Collections.singletonList(
                ComponentCriterionEnum.fromValue(params.getComponentCriteria())
            );
        }

        GetReportTask task = new GetReportTask(
            params.getChargePointSelectList(),
            params.getRequestId(),
            componentVariables,
            componentCriteria
        );
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_REPORT_PATH;
    }

    // -------------------------------------------------------------------------
    // SetNetworkProfile
    // -------------------------------------------------------------------------

    private static final String SET_NETWORK_PROFILE_PATH = "/SetNetworkProfile";

    @RequestMapping(value = SET_NETWORK_PROFILE_PATH, method = RequestMethod.GET)
    public String setNetworkProfileGet(Model model) {
        model.addAttribute("params", new SetNetworkProfileParams());
        return "op20/SetNetworkProfile";
    }

    @RequestMapping(value = SET_NETWORK_PROFILE_PATH, method = RequestMethod.POST)
    public String setNetworkProfilePost(@Valid @ModelAttribute("params") SetNetworkProfileParams params,
                                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/SetNetworkProfile";
        }

        NetworkConnectionProfile connectionData = new NetworkConnectionProfile();
        connectionData.setOcppVersion(OCPPVersionEnum.fromValue(params.getOcppVersion()));
        connectionData.setOcppTransport(OCPPTransportEnum.fromValue(params.getOcppTransport()));
        connectionData.setOcppCsmsUrl(params.getOcppCsmsUrl());
        connectionData.setMessageTimeout(params.getMessageTimeout());
        connectionData.setSecurityProfile(Integer.parseInt(params.getSecurityProfile()));
        connectionData.setOcppInterface(OCPPInterfaceEnum.fromValue(params.getOcppInterface()));

        SetNetworkProfileTask task = new SetNetworkProfileTask(
            params.getChargePointSelectList(),
            params.getConfigurationSlot(),
            connectionData
        );
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + SET_NETWORK_PROFILE_PATH;
    }

    // -------------------------------------------------------------------------
    // SetChargingProfile
    // -------------------------------------------------------------------------

    private static final String SET_CHARGING_PROFILE_PATH = "/SetChargingProfile";
    private static final String GET_CHARGING_PROFILES_PATH = "/GetChargingProfiles";
    private static final String CLEAR_CHARGING_PROFILE_PATH = "/ClearChargingProfile";
    private static final String GET_COMPOSITE_SCHEDULE_PATH = "/GetCompositeSchedule";
    private static final String UPDATE_FIRMWARE_PATH = "/UpdateFirmware";
    private static final String GET_LOG_PATH = "/GetLog";
    private static final String CANCEL_RESERVATION_PATH = "/CancelReservation";
    private static final String RESERVE_NOW_PATH = "/ReserveNow";
    private static final String SEND_LOCAL_LIST_PATH = "/SendLocalList";

    @RequestMapping(value = SET_CHARGING_PROFILE_PATH, method = RequestMethod.GET)
    public String setChargingProfileGet(Model model) {
        model.addAttribute("params", new SetChargingProfileParams());
        return "op20/SetChargingProfile";
    }

    @RequestMapping(value = SET_CHARGING_PROFILE_PATH, method = RequestMethod.POST)
    public String setChargingProfilePost(@Valid @ModelAttribute("params") SetChargingProfileParams params,
                                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/SetChargingProfile";
        }

        // Build ChargingProfile from params
        ChargingProfile profile = new ChargingProfile();
        profile.setId(params.getProfileId());
        profile.setStackLevel(params.getStackLevel());
        profile.setChargingProfilePurpose(ChargingProfilePurposeEnum.fromValue(params.getProfilePurpose()));
        profile.setChargingProfileKind(ChargingProfileKindEnum.fromValue(params.getProfileKind()));

        if (params.getRecurrencyKind() != null) {
            profile.setRecurrencyKind(RecurrencyKindEnum.fromValue(params.getRecurrencyKind()));
        }

        // Create ChargingSchedule
        ChargingSchedule schedule = new ChargingSchedule();
        schedule.setId(0); // Schedule ID
        schedule.setDuration(params.getDuration());
        schedule.setChargingRateUnit(ChargingRateUnitEnum.fromValue(params.getChargingRateUnit()));

        if (params.getStartSchedule() != null && !params.getStartSchedule().isEmpty()) {
            // TODO: Parse datetime string to OffsetDateTime
        }

        if (params.getMinChargingRate() != null) {
            schedule.setMinChargingRate(params.getMinChargingRate().doubleValue());
        }

        // Create ChargingSchedulePeriod
        ChargingSchedulePeriod period = new ChargingSchedulePeriod();
        period.setStartPeriod(params.getStartPeriod());
        period.setLimit(params.getLimit());
        if (params.getNumberPhases() != null) {
            period.setNumberPhases(params.getNumberPhases());
        }

        schedule.getChargingSchedulePeriod().add(period);
        profile.getChargingSchedule().add(schedule);

        SetChargingProfileTask task = new SetChargingProfileTask(
            params.getChargePointSelectList(),
            params.getEvseId(),
            profile
        );
        taskExecutor.execute(task);

        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + SET_CHARGING_PROFILE_PATH;
    }

    // -------------------------------------------------------------------------
    // GetChargingProfiles
    // -------------------------------------------------------------------------

    @RequestMapping(value = GET_CHARGING_PROFILES_PATH, method = RequestMethod.GET)
    public String getChargingProfilesGet(Model model) {
        model.addAttribute("params", new GetChargingProfilesParams());
        return "op20/GetChargingProfiles";
    }

    @RequestMapping(value = GET_CHARGING_PROFILES_PATH, method = RequestMethod.POST)
    public String getChargingProfilesPost(@Valid @ModelAttribute("params") GetChargingProfilesParams params,
                                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/GetChargingProfiles";
        }

        GetChargingProfilesTask task = new GetChargingProfilesTask(
            params.getChargePointSelectList(),
            params.getRequestId(),
            params.getEvseId()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_CHARGING_PROFILES_PATH;
    }

    // -------------------------------------------------------------------------
    // ClearChargingProfile
    // -------------------------------------------------------------------------

    @RequestMapping(value = CLEAR_CHARGING_PROFILE_PATH, method = RequestMethod.GET)
    public String clearChargingProfileGet(Model model) {
        model.addAttribute("params", new ClearChargingProfileParams());
        return "op20/ClearChargingProfile";
    }

    @RequestMapping(value = CLEAR_CHARGING_PROFILE_PATH, method = RequestMethod.POST)
    public String clearChargingProfilePost(@Valid @ModelAttribute("params") ClearChargingProfileParams params,
                                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/ClearChargingProfile";
        }

        ClearChargingProfileTask task = new ClearChargingProfileTask(
            params.getChargePointSelectList(),
            params.getChargingProfileId()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + CLEAR_CHARGING_PROFILE_PATH;
    }

    // -------------------------------------------------------------------------
    // GetCompositeSchedule
    // -------------------------------------------------------------------------

    @RequestMapping(value = GET_COMPOSITE_SCHEDULE_PATH, method = RequestMethod.GET)
    public String getCompositeScheduleGet(Model model) {
        model.addAttribute("params", new GetCompositeScheduleParams());
        return "op20/GetCompositeSchedule";
    }

    @RequestMapping(value = GET_COMPOSITE_SCHEDULE_PATH, method = RequestMethod.POST)
    public String getCompositeSchedulePost(@Valid @ModelAttribute("params") GetCompositeScheduleParams params,
                                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/GetCompositeSchedule";
        }

        ChargingRateUnitEnum rateUnit = params.getChargingRateUnit() != null ?
            ChargingRateUnitEnum.fromValue(params.getChargingRateUnit()) : null;

        GetCompositeScheduleTask task = new GetCompositeScheduleTask(
            params.getChargePointSelectList(),
            params.getDuration(),
            params.getEvseId(),
            rateUnit
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_COMPOSITE_SCHEDULE_PATH;
    }

    // -------------------------------------------------------------------------
    // UpdateFirmware
    // -------------------------------------------------------------------------

    @RequestMapping(value = UPDATE_FIRMWARE_PATH, method = RequestMethod.GET)
    public String updateFirmwareGet(Model model) {
        model.addAttribute("params", new UpdateFirmwareParams());
        return "op20/UpdateFirmware";
    }

    @RequestMapping(value = UPDATE_FIRMWARE_PATH, method = RequestMethod.POST)
    public String updateFirmwarePost(@Valid @ModelAttribute("params") UpdateFirmwareParams params,
                                     BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/UpdateFirmware";
        }

        UpdateFirmwareTask task = new UpdateFirmwareTask(
            params.getChargePointSelectList(),
            params.getRequestId(),
            params.getLocation()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + UPDATE_FIRMWARE_PATH;
    }

    // -------------------------------------------------------------------------
    // GetLog
    // -------------------------------------------------------------------------

    @RequestMapping(value = GET_LOG_PATH, method = RequestMethod.GET)
    public String getLogGet(Model model) {
        model.addAttribute("params", new GetLogParams());
        return "op20/GetLog";
    }

    @RequestMapping(value = GET_LOG_PATH, method = RequestMethod.POST)
    public String getLogPost(@Valid @ModelAttribute("params") GetLogParams params,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/GetLog";
        }

        LogEnum logType = LogEnum.fromValue(params.getLogType());

        GetLogTask task = new GetLogTask(
            params.getChargePointSelectList(),
            params.getRequestId(),
            params.getRemoteLocation(),
            logType
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_LOG_PATH;
    }

    // -------------------------------------------------------------------------
    // CancelReservation
    // -------------------------------------------------------------------------

    @RequestMapping(value = CANCEL_RESERVATION_PATH, method = RequestMethod.GET)
    public String cancelReservationGet(Model model) {
        model.addAttribute("params", new CancelReservationParams());
        return "op20/CancelReservation";
    }

    @RequestMapping(value = CANCEL_RESERVATION_PATH, method = RequestMethod.POST)
    public String cancelReservationPost(@Valid @ModelAttribute("params") CancelReservationParams params,
                                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/CancelReservation";
        }

        CancelReservationTask task = new CancelReservationTask(
            params.getChargePointSelectList(),
            params.getReservationId()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + CANCEL_RESERVATION_PATH;
    }

    // -------------------------------------------------------------------------
    // ReserveNow
    // -------------------------------------------------------------------------

    @RequestMapping(value = RESERVE_NOW_PATH, method = RequestMethod.GET)
    public String reserveNowGet(Model model) {
        model.addAttribute("params", new ReserveNowParams());
        return "op20/ReserveNow";
    }

    @RequestMapping(value = RESERVE_NOW_PATH, method = RequestMethod.POST)
    public String reserveNowPost(@Valid @ModelAttribute("params") ReserveNowParams params,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/ReserveNow";
        }

        IdToken idToken = new IdToken();
        idToken.setIdToken(params.getIdToken());
        idToken.setType(IdTokenEnum.fromValue(params.getIdTokenType()));

        ReserveNowTask task = new ReserveNowTask(
            params.getChargePointSelectList(),
            params.getId(),
            new org.joda.time.DateTime(params.getExpiryDateTime()),
            idToken,
            params.getEvseId(),
            params.getGroupIdToken()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + RESERVE_NOW_PATH;
    }

    // -------------------------------------------------------------------------
    // SendLocalList
    // -------------------------------------------------------------------------

    @RequestMapping(value = SEND_LOCAL_LIST_PATH, method = RequestMethod.GET)
    public String sendLocalListGet(Model model) {
        model.addAttribute("params", new SendLocalListParams());
        return "op20/SendLocalList";
    }

    @RequestMapping(value = SEND_LOCAL_LIST_PATH, method = RequestMethod.POST)
    public String sendLocalListPost(@Valid @ModelAttribute("params") SendLocalListParams params,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/SendLocalList";
        }

        UpdateEnum updateType = UpdateEnum.fromValue(params.getUpdateType());
        List<AuthorizationData> authList = new ArrayList<>();

        SendLocalListTask task = new SendLocalListTask(
            params.getChargePointSelectList(),
            params.getVersionNumber(),
            updateType,
            authList
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + SEND_LOCAL_LIST_PATH;
    }
}