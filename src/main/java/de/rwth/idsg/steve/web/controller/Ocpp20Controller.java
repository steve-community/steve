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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    private static final String COST_UPDATED_PATH = "/CostUpdated";
    private static final String GET_MONITORING_REPORT_PATH = "/GetMonitoringReport";

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
    private static final String GET_LOCAL_LIST_VERSION_PATH = "/GetLocalListVersion";
    private static final String GET_TRANSACTION_STATUS_PATH = "/GetTransactionStatus";

    // Certificate Management Operations
    private static final String INSTALL_CERTIFICATE_PATH = "/InstallCertificate";
    private static final String GET_INSTALLED_CERTIFICATE_IDS_PATH = "/GetInstalledCertificateIds";
    private static final String DELETE_CERTIFICATE_PATH = "/DeleteCertificate";
    private static final String CERTIFICATE_SIGNED_PATH = "/CertificateSigned";
    private static final String GET_CERTIFICATE_STATUS_PATH = "/GetCertificateStatus";
    private static final String GET_15118_EV_CERTIFICATE_PATH = "/Get15118EVCertificate";

    // Customer and Display Operations
    private static final String CUSTOMER_INFORMATION_PATH = "/CustomerInformation";
    private static final String GET_DISPLAY_MESSAGES_PATH = "/GetDisplayMessages";
    private static final String SET_DISPLAY_MESSAGE_PATH = "/SetDisplayMessage";
    private static final String CLEAR_DISPLAY_MESSAGE_PATH = "/ClearDisplayMessage";

    // Monitoring Operations
    private static final String SET_MONITORING_BASE_PATH = "/SetMonitoringBase";
    private static final String SET_MONITORING_LEVEL_PATH = "/SetMonitoringLevel";
    private static final String SET_VARIABLE_MONITORING_PATH = "/SetVariableMonitoring";
    private static final String CLEAR_VARIABLE_MONITORING_PATH = "/ClearVariableMonitoring";

    // Firmware Operations
    private static final String PUBLISH_FIRMWARE_PATH = "/PublishFirmware";
    private static final String UNPUBLISH_FIRMWARE_PATH = "/UnpublishFirmware";

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

    // -------------------------------------------------------------------------
    // GetLocalListVersion
    // -------------------------------------------------------------------------

    @RequestMapping(value = GET_LOCAL_LIST_VERSION_PATH, method = RequestMethod.GET)
    public String getLocalListVersionGet(Model model) {
        model.addAttribute("params", new GetLocalListVersionParams());
        return "op20/GetLocalListVersion";
    }

    @RequestMapping(value = GET_LOCAL_LIST_VERSION_PATH, method = RequestMethod.POST)
    public String getLocalListVersionPost(@Valid @ModelAttribute("params") GetLocalListVersionParams params,
                                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/GetLocalListVersion";
        }

        GetLocalListVersionTask task = new GetLocalListVersionTask(
            params.getChargePointSelectList()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_LOCAL_LIST_VERSION_PATH;
    }

    // -------------------------------------------------------------------------
    // GetTransactionStatus
    // -------------------------------------------------------------------------

    @RequestMapping(value = GET_TRANSACTION_STATUS_PATH, method = RequestMethod.GET)
    public String getTransactionStatusGet(Model model) {
        model.addAttribute("params", new GetTransactionStatusParams());
        return "op20/GetTransactionStatus";
    }

    @RequestMapping(value = GET_TRANSACTION_STATUS_PATH, method = RequestMethod.POST)
    public String getTransactionStatusPost(@Valid @ModelAttribute("params") GetTransactionStatusParams params,
                                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/GetTransactionStatus";
        }

        GetTransactionStatusTask task = new GetTransactionStatusTask(
            params.getChargePointSelectList(),
            params.getTransactionId()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_TRANSACTION_STATUS_PATH;
    }

    // -------------------------------------------------------------------------
    // Certificate Management Methods
    // -------------------------------------------------------------------------

    @RequestMapping(value = INSTALL_CERTIFICATE_PATH, method = RequestMethod.GET)
    public String getInstallCertificate(Model model) {
        model.addAttribute("params", new InstallCertificateParams());
        return "op20/InstallCertificate";
    }

    @RequestMapping(value = INSTALL_CERTIFICATE_PATH, method = RequestMethod.POST)
    public String postInstallCertificate(@Valid @ModelAttribute("params") InstallCertificateParams params,
                                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/InstallCertificate";
        }

        InstallCertificateTask task = new InstallCertificateTask(
            params.getChargePointSelectList(),
            InstallCertificateUseEnum.fromValue(params.getCertificateType()),
            params.getCertificate()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + INSTALL_CERTIFICATE_PATH;
    }

    @RequestMapping(value = GET_INSTALLED_CERTIFICATE_IDS_PATH, method = RequestMethod.GET)
    public String getGetInstalledCertificateIds(Model model) {
        model.addAttribute("params", new GetInstalledCertificateIdsParams());
        return "op20/GetInstalledCertificateIds";
    }

    @RequestMapping(value = GET_INSTALLED_CERTIFICATE_IDS_PATH, method = RequestMethod.POST)
    public String postGetInstalledCertificateIds(@Valid @ModelAttribute("params") GetInstalledCertificateIdsParams params,
                                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/GetInstalledCertificateIds";
        }

        GetCertificateIdUseEnum certType = params.getCertificateType() != null && !params.getCertificateType().isEmpty()
            ? GetCertificateIdUseEnum.fromValue(params.getCertificateType())
            : null;

        GetInstalledCertificateIdsTask task = new GetInstalledCertificateIdsTask(
            params.getChargePointSelectList(),
            certType
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_INSTALLED_CERTIFICATE_IDS_PATH;
    }

    @RequestMapping(value = DELETE_CERTIFICATE_PATH, method = RequestMethod.GET)
    public String getDeleteCertificate(Model model) {
        model.addAttribute("params", new DeleteCertificateParams());
        
        return "op20/DeleteCertificate";
    }

    @RequestMapping(value = DELETE_CERTIFICATE_PATH, method = RequestMethod.POST)
    public String postDeleteCertificate(@Valid @ModelAttribute("params") DeleteCertificateParams params,
                                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            
            return "op20/DeleteCertificate";
        }

        DeleteCertificateTask task = new DeleteCertificateTask(
            params.getChargePointSelectList(),
            params.getCertificateHashData().getIssuerNameHash(),
            params.getCertificateHashData().getIssuerKeyHash(),
            params.getCertificateHashData().getSerialNumber()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + DELETE_CERTIFICATE_PATH;
    }

    @RequestMapping(value = CERTIFICATE_SIGNED_PATH, method = RequestMethod.GET)
    public String getCertificateSigned(Model model) {
        model.addAttribute("params", new CertificateSignedParams());
        
        return "op20/CertificateSigned";
    }

    @RequestMapping(value = CERTIFICATE_SIGNED_PATH, method = RequestMethod.POST)
    public String postCertificateSigned(@Valid @ModelAttribute("params") CertificateSignedParams params,
                                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            
            return "op20/CertificateSigned";
        }

        CertificateSignedTask task = new CertificateSignedTask(
            params.getChargePointSelectList(),
            params.getCertificateChain()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + CERTIFICATE_SIGNED_PATH;
    }

    // -------------------------------------------------------------------------
    // Customer and Display Management Methods
    // -------------------------------------------------------------------------

    @RequestMapping(value = CUSTOMER_INFORMATION_PATH, method = RequestMethod.GET)
    public String getCustomerInformation(Model model) {
        model.addAttribute("params", new CustomerInformationParams());
        
        return "op20/CustomerInformation";
    }

    @RequestMapping(value = CUSTOMER_INFORMATION_PATH, method = RequestMethod.POST)
    public String postCustomerInformation(@Valid @ModelAttribute("params") CustomerInformationParams params,
                                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            
            return "op20/CustomerInformation";
        }

        CustomerInformationTask task = new CustomerInformationTask(
            params.getChargePointSelectList(),
            params.getRequestId(),
            params.getReport(),
            params.getClear(),
            params.getCustomerIdentifier()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + CUSTOMER_INFORMATION_PATH;
    }

    @RequestMapping(value = GET_DISPLAY_MESSAGES_PATH, method = RequestMethod.GET)
    public String getGetDisplayMessages(Model model) {
        model.addAttribute("params", new GetDisplayMessagesParams());
        
        return "op20/GetDisplayMessages";
    }

    @RequestMapping(value = GET_DISPLAY_MESSAGES_PATH, method = RequestMethod.POST)
    public String postGetDisplayMessages(@Valid @ModelAttribute("params") GetDisplayMessagesParams params,
                                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            
            return "op20/GetDisplayMessages";
        }

        List<Integer> messageIds = null;
        if (params.getMessageIds() != null && !params.getMessageIds().isEmpty()) {
            messageIds = Arrays.stream(params.getMessageIds().split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        }

        MessagePriorityEnum priority = params.getPriority() != null && !params.getPriority().isEmpty()
            ? MessagePriorityEnum.fromValue(params.getPriority())
            : null;

        MessageStateEnum state = params.getState() != null && !params.getState().isEmpty()
            ? MessageStateEnum.fromValue(params.getState())
            : null;

        GetDisplayMessagesTask task = new GetDisplayMessagesTask(
            params.getChargePointSelectList(),
            params.getRequestId(),
            messageIds,
            priority,
            state
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_DISPLAY_MESSAGES_PATH;
    }

    // -------------------------------------------------------------------------
    // Monitoring Methods
    // -------------------------------------------------------------------------

    @RequestMapping(value = SET_MONITORING_BASE_PATH, method = RequestMethod.GET)
    public String getSetMonitoringBase(Model model) {
        model.addAttribute("params", new SetMonitoringBaseParams());
        
        return "op20/SetMonitoringBase";
    }

    @RequestMapping(value = SET_MONITORING_BASE_PATH, method = RequestMethod.POST)
    public String postSetMonitoringBase(@Valid @ModelAttribute("params") SetMonitoringBaseParams params,
                                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            
            return "op20/SetMonitoringBase";
        }

        SetMonitoringBaseTask task = new SetMonitoringBaseTask(
            params.getChargePointSelectList(),
            MonitoringBaseEnum.ALL // Default to ALL, could be made configurable
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + SET_MONITORING_BASE_PATH;
    }

    @RequestMapping(value = SET_MONITORING_LEVEL_PATH, method = RequestMethod.GET)
    public String getSetMonitoringLevel(Model model) {
        model.addAttribute("params", new SetMonitoringLevelParams());
        
        return "op20/SetMonitoringLevel";
    }

    @RequestMapping(value = SET_MONITORING_LEVEL_PATH, method = RequestMethod.POST)
    public String postSetMonitoringLevel(@Valid @ModelAttribute("params") SetMonitoringLevelParams params,
                                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            
            return "op20/SetMonitoringLevel";
        }

        SetMonitoringLevelTask task = new SetMonitoringLevelTask(
            params.getChargePointSelectList(),
            params.getSeverity()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + SET_MONITORING_LEVEL_PATH;
    }

    @RequestMapping(value = CLEAR_VARIABLE_MONITORING_PATH, method = RequestMethod.GET)
    public String getClearVariableMonitoring(Model model) {
        model.addAttribute("params", new ClearVariableMonitoringParams());
        
        return "op20/ClearVariableMonitoring";
    }

    @RequestMapping(value = CLEAR_VARIABLE_MONITORING_PATH, method = RequestMethod.POST)
    public String postClearVariableMonitoring(@Valid @ModelAttribute("params") ClearVariableMonitoringParams params,
                                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            
            return "op20/ClearVariableMonitoring";
        }

        List<Integer> monitoringIds = Arrays.stream(params.getMonitoringIds().split(","))
            .map(String::trim)
            .map(Integer::parseInt)
            .collect(Collectors.toList());

        ClearVariableMonitoringTask task = new ClearVariableMonitoringTask(
            params.getChargePointSelectList(),
            monitoringIds
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + CLEAR_VARIABLE_MONITORING_PATH;
    }

    // -------------------------------------------------------------------------
    // Firmware Methods
    // -------------------------------------------------------------------------

    @RequestMapping(value = PUBLISH_FIRMWARE_PATH, method = RequestMethod.GET)
    public String getPublishFirmware(Model model) {
        model.addAttribute("params", new PublishFirmwareParams());
        
        return "op20/PublishFirmware";
    }

    @RequestMapping(value = PUBLISH_FIRMWARE_PATH, method = RequestMethod.POST)
    public String postPublishFirmware(@Valid @ModelAttribute("params") PublishFirmwareParams params,
                                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            
            return "op20/PublishFirmware";
        }

        PublishFirmwareTask task = new PublishFirmwareTask(
            params.getChargePointSelectList(),
            params.getLocation(),
            params.getRetries(),
            params.getRetryInterval(),
            params.getChecksum(),
            params.getRequestId()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + PUBLISH_FIRMWARE_PATH;
    }

    @RequestMapping(value = UNPUBLISH_FIRMWARE_PATH, method = RequestMethod.GET)
    public String getUnpublishFirmware(Model model) {
        model.addAttribute("params", new UnpublishFirmwareParams());
        
        return "op20/UnpublishFirmware";
    }

    @RequestMapping(value = UNPUBLISH_FIRMWARE_PATH, method = RequestMethod.POST)
    public String postUnpublishFirmware(@Valid @ModelAttribute("params") UnpublishFirmwareParams params,
                                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            
            return "op20/UnpublishFirmware";
        }

        UnpublishFirmwareTask task = new UnpublishFirmwareTask(
            params.getChargePointSelectList(),
            params.getChecksum()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + UNPUBLISH_FIRMWARE_PATH;
    }

    // ClearDisplayMessage
    @RequestMapping(value = CLEAR_DISPLAY_MESSAGE_PATH, method = RequestMethod.GET)
    public String clearDisplayMessageGet(Model model) {
        model.addAttribute("params", new ClearDisplayMessageParams());
        return "op20/ClearDisplayMessage";
    }

    @RequestMapping(value = CLEAR_DISPLAY_MESSAGE_PATH, method = RequestMethod.POST)
    public String clearDisplayMessagePost(@Valid @ModelAttribute("params") ClearDisplayMessageParams params,
                                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/ClearDisplayMessage";
        }

        ClearDisplayMessageTask task = new ClearDisplayMessageTask(
            params.getChargePointSelectList(),
            params.getId()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + CLEAR_DISPLAY_MESSAGE_PATH;
    }

    // CostUpdated - TODO: Fix CostUpdatedTask implementation
    /*
    @RequestMapping(value = COST_UPDATED_PATH, method = RequestMethod.GET)
    public String costUpdatedGet(Model model) {
        model.addAttribute("params", new CostUpdatedParams());
        return "op20/CostUpdated";
    }

    @RequestMapping(value = COST_UPDATED_PATH, method = RequestMethod.POST)
    public String costUpdatedPost(@Valid @ModelAttribute("params") CostUpdatedParams params,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/CostUpdated";
        }

        // TODO: Fix CostUpdatedTask implementation
        // CostUpdatedTask task = new CostUpdatedTask(
        //     params.getChargePointSelectList(),
        //     params.getTotalCost(),
        //     params.getTransactionId()
        // );

        // taskExecutor.execute(task);
        // model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + COST_UPDATED_PATH;
    }
    */

    // Get15118EVCertificate
    @RequestMapping(value = GET_15118_EV_CERTIFICATE_PATH, method = RequestMethod.GET)
    public String get15118EVCertificateGet(Model model) {
        model.addAttribute("params", new Get15118EVCertificateParams());
        return "op20/Get15118EVCertificate";
    }

    @RequestMapping(value = GET_15118_EV_CERTIFICATE_PATH, method = RequestMethod.POST)
    public String get15118EVCertificatePost(@Valid @ModelAttribute("params") Get15118EVCertificateParams params,
                                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/Get15118EVCertificate";
        }

        Get15118EVCertificateTask task = new Get15118EVCertificateTask(
            params.getChargePointSelectList(),
            params.getIso15118SchemaVersion(),
            params.getExiRequest()
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_15118_EV_CERTIFICATE_PATH;
    }

    // GetCertificateStatus
    @RequestMapping(value = GET_CERTIFICATE_STATUS_PATH, method = RequestMethod.GET)
    public String getCertificateStatusGet(Model model) {
        model.addAttribute("params", new GetCertificateStatusParams());
        return "op20/GetCertificateStatus";
    }

    @RequestMapping(value = GET_CERTIFICATE_STATUS_PATH, method = RequestMethod.POST)
    public String getCertificateStatusPost(@Valid @ModelAttribute("params") GetCertificateStatusParams params,
                                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/GetCertificateStatus";
        }

        GetCertificateStatusTask task = new GetCertificateStatusTask(
            params.getChargePointSelectList(),
            null // TODO: Convert DTO to OCPP model
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_CERTIFICATE_STATUS_PATH;
    }

    // GetMonitoringReport - TODO: Fix type conversions
    /*
    @RequestMapping(value = GET_MONITORING_REPORT_PATH, method = RequestMethod.GET)
    public String getMonitoringReportGet(Model model) {
        model.addAttribute("params", new GetMonitoringReportParams());
        return "op20/GetMonitoringReport";
    }

    @RequestMapping(value = GET_MONITORING_REPORT_PATH, method = RequestMethod.POST)
    public String getMonitoringReportPost(@Valid @ModelAttribute("params") GetMonitoringReportParams params,
                                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/GetMonitoringReport";
        }

        // TODO: Fix GetMonitoringReportTask constructor - needs proper type conversion
        // GetMonitoringReportTask task = new GetMonitoringReportTask(
        //     params.getChargePointSelectList(),
        //     params.getRequestId(),
        //     params.getMonitoringCriteria(), // Convert monitoring criteria
        //     params.getComponentVariable()  // Convert component variables
        // );

        // taskExecutor.execute(task);
        // model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + GET_MONITORING_REPORT_PATH;
    }
    */

    // SetDisplayMessage
    @RequestMapping(value = SET_DISPLAY_MESSAGE_PATH, method = RequestMethod.GET)
    public String setDisplayMessageGet(Model model) {
        model.addAttribute("params", new SetDisplayMessageParams());
        return "op20/SetDisplayMessage";
    }

    @RequestMapping(value = SET_DISPLAY_MESSAGE_PATH, method = RequestMethod.POST)
    public String setDisplayMessagePost(@Valid @ModelAttribute("params") SetDisplayMessageParams params,
                                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/SetDisplayMessage";
        }

        SetDisplayMessageTask task = new SetDisplayMessageTask(
            params.getChargePointSelectList(),
            null // TODO: Convert message info
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + SET_DISPLAY_MESSAGE_PATH;
    }

    // SetVariableMonitoring
    @RequestMapping(value = SET_VARIABLE_MONITORING_PATH, method = RequestMethod.GET)
    public String setVariableMonitoringGet(Model model) {
        model.addAttribute("params", new SetVariableMonitoringParams());
        return "op20/SetVariableMonitoring";
    }

    @RequestMapping(value = SET_VARIABLE_MONITORING_PATH, method = RequestMethod.POST)
    public String setVariableMonitoringPost(@Valid @ModelAttribute("params") SetVariableMonitoringParams params,
                                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "op20/SetVariableMonitoring";
        }

        SetVariableMonitoringTask task = new SetVariableMonitoringTask(
            params.getChargePointSelectList(),
            null // TODO: Convert monitoring data
        );

        taskExecutor.execute(task);
        model.addAttribute("taskId", task.getTaskId());
        return "redirect:/manager/operations/v2.0" + SET_VARIABLE_MONITORING_PATH;
    }

}