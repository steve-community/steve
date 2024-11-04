/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
 * All Rights Reserved.
 *
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import de.rwth.idsg.steve.service.notification.OccpStationBooted;
import de.rwth.idsg.steve.service.notification.OcppStationStatusFailure;
import de.rwth.idsg.steve.service.notification.OcppTransactionEnded;
import de.rwth.idsg.steve.service.notification.OcppTransactionStarted;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.module.esp.model.ESPRfidChargingStartRequest;
import net.parkl.ocpp.service.ChargingProcessService;
import net.parkl.ocpp.service.ESPNotificationService;
import net.parkl.ocpp.service.HeartBeatService;
import net.parkl.ocpp.service.RemoteStartService;
import net.parkl.ocpp.service.cs.*;
import net.parkl.ocpp.service.middleware.OcppNotificationMiddleware;
import ocpp.cs._2015._10.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static net.parkl.ocpp.entities.TransactionStopEventActor.station;
import static ocpp.cs._2015._10.AuthorizationStatus.INVALID;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.03.2018
 */
@Slf4j
@Service
public class CentralSystemService16_Service {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ChargePointService chargePointService;
    @Autowired
    private ConnectorService connectorService;
    @Autowired
    private ConnectorMeterValueService connectorMeterValueService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private OcppTagService ocppTagService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ChargePointHelperService chargePointHelperService;
    @Autowired
    private ChargingProcessService chargingProcessService;
    @Autowired
    private OcppNotificationMiddleware notificationMiddleware;
    @Autowired
    private HeartBeatService heartBeatService;

    @Autowired
    private RemoteStartService remoteStartService;

    @Autowired
    private ESPNotificationService espNotificationService;

    public BootNotificationResponse bootNotification(BootNotificationRequest parameters, String chargeBoxIdentity,
                                                     OcppProtocol ocppProtocol) {
        log.info("Boot notification: {}", chargeBoxIdentity);
        Optional<RegistrationStatus> status = chargePointHelperService.getRegistrationStatus(chargeBoxIdentity);
        applicationEventPublisher.publishEvent(new OccpStationBooted(chargeBoxIdentity, status));
        DateTime now = DateTime.now();

        if (status.isEmpty()) {
            // Applies only to stations not in db (regardless of the registration_status field from db)
            log.error("The chargebox '{}' is NOT in database.", chargeBoxIdentity);
        } else {
            // Applies to all stations in db (even with registration_status Rejected)
            log.info("The boot of the chargebox '{}' with registration status '{}' is acknowledged.", chargeBoxIdentity, status);
            UpdateChargeboxParams params =
                    UpdateChargeboxParams.builder()
                            .ocppProtocol(ocppProtocol)
                            .vendor(parameters.getChargePointVendor())
                            .model(parameters.getChargePointModel())
                            .pointSerial(parameters.getChargePointSerialNumber())
                            .boxSerial(parameters.getChargeBoxSerialNumber())
                            .fwVersion(parameters.getFirmwareVersion())
                            .iccid(parameters.getIccid())
                            .imsi(parameters.getImsi())
                            .meterType(parameters.getMeterType())
                            .meterSerial(parameters.getMeterSerialNumber())
                            .chargeBoxId(chargeBoxIdentity)
                            .heartbeatTimestamp(now)
                            .build();

            chargePointService.updateChargebox(params);

            try {
                heartBeatService.changeConfig(ocppProtocol, chargeBoxIdentity);
            } catch (Exception ex) {
                log.error("Heart beat change configuration error: ", ex);
            }
        }

        return new BootNotificationResponse()
                .withStatus(status.orElse(RegistrationStatus.REJECTED))
                .withCurrentTime(now)
                .withInterval(settingsService.getHeartbeatIntervalInSeconds());
    }

    public FirmwareStatusNotificationResponse firmwareStatusNotification(
            FirmwareStatusNotificationRequest parameters, String chargeBoxIdentity) {
        String status = parameters.getStatus().value();
        log.info("Firmware status notification on {}: {}", chargeBoxIdentity, status);

        chargePointService.updateChargeboxFirmwareStatus(chargeBoxIdentity, status);
        return new FirmwareStatusNotificationResponse();
    }

    public StatusNotificationResponse statusNotification(
            StatusNotificationRequest parameters, String chargeBoxIdentity) {
        log.info("Status notification on {}: {}", chargeBoxIdentity, parameters.getStatus().value());
        // Optional field
        DateTime timestamp = parameters.getTimestamp()!=null ? parameters.getTimestamp() : DateTime.now();

        InsertConnectorStatusParams params =
                InsertConnectorStatusParams.builder()
                        .chargeBoxId(chargeBoxIdentity)
                        .connectorId(parameters.getConnectorId())
                        .status(parameters.getStatus().value())
                        .errorCode(parameters.getErrorCode().value())
                        .timestamp(timestamp)
                        .errorInfo(parameters.getInfo())
                        .vendorId(parameters.getVendorId())
                        .vendorErrorCode(parameters.getVendorErrorCode())
                        .build();

        connectorService.insertConnectorStatus(params);

        if (parameters.getStatus() == ChargePointStatus.FAULTED) {
            applicationEventPublisher.publishEvent(new OcppStationStatusFailure(
                    chargeBoxIdentity, parameters.getConnectorId(), parameters.getErrorCode().value()));
        }

        return new StatusNotificationResponse();
    }

    public MeterValuesResponse meterValues(MeterValuesRequest parameters, String chargeBoxIdentity) {
        Integer transactionId = getTransactionId(parameters);
        log.info("Meter values request for transaction: {} [chargeBox={}]", transactionId, chargeBoxIdentity);

        if (parameters.isSetMeterValue() && transactionId != null && transactionId > 0) {
            TransactionStart transactionStart =
                    transactionService.findTransactionStart(transactionId).orElseThrow(() ->
                            new IllegalArgumentException("Invalid transaction id: " + transactionId));

            connectorMeterValueService.insertMeterValues(parameters.getMeterValue(), transactionStart);
            espNotificationService.notifyMeterValues(transactionStart, parameters.getMeterValue());

        }
        return new MeterValuesResponse();
    }

    public DiagnosticsStatusNotificationResponse diagnosticsStatusNotification(
            DiagnosticsStatusNotificationRequest parameters, String chargeBoxIdentity) {
        String status = parameters.getStatus().value();
        log.info("Diagnostics status on {}: {}...", chargeBoxIdentity, status);
        chargePointService.updateChargeboxDiagnosticsStatus(chargeBoxIdentity, status);
        return new DiagnosticsStatusNotificationResponse();
    }

    public StartTransactionResponse startTransaction(StartTransactionRequest parameters, String chargeBoxIdentity) {
        boolean remoteStart = remoteStartService
                .hasOpenRemoteStart(chargeBoxIdentity, parameters.getConnectorId(), parameters.getIdTag());

        // Get the authorization info of the user, before making tx changes (will affectAuthorizationStatus)
        IdTagInfo info;
        if (!remoteStart) {
            info = ocppTagService.getIdTagInfo(
                    parameters.getIdTag(),
                    true,
                    chargeBoxIdentity,
                    parameters.getConnectorId(),
                    () -> new IdTagInfo().withStatus(AuthorizationStatus.INVALID) // IdTagInfo is required
            );
        } else {
            info = new IdTagInfo()
                    .withStatus(AuthorizationStatus.ACCEPTED);
        }

        log.info("Starting transaction on {}...", chargeBoxIdentity);
        InsertTransactionParams params =
                InsertTransactionParams.builder()
                        .chargeBoxId(chargeBoxIdentity)
                        .connectorId(parameters.getConnectorId())
                        .idTag(parameters.getIdTag())
                        .startTimestamp(parameters.getTimestamp())
                        .startMeterValue(Integer.toString(parameters.getMeterStart()))
                        .reservationId(parameters.getReservationId())
                        .eventTimestamp(DateTime.now())
                        .build();

        Integer transactionId;

         if (!remoteStart) {
            log.info("No remote start for RFID tag: {} on charger: {}/{}, interpreting as RFID start...",
                    parameters.getIdTag(), chargeBoxIdentity, parameters.getConnectorId());

            chargingProcessService.createChargingProcess(chargeBoxIdentity,
                    parameters.getConnectorId(),
                    parameters.getIdTag(),
                    null,
                    null,
                    null);

            transactionId = transactionService.insertTransaction(params);

            ESPRfidChargingStartRequest startRequest = new ESPRfidChargingStartRequest();
            startRequest.setRfidTag(parameters.getIdTag());
            startRequest.setConnectorId(parameters.getConnectorId());
            startRequest.setChargeBoxId(chargeBoxIdentity);
            OcppChargingProcess ocppChargingProcess = chargingProcessService.findByTransactionId(transactionId);
            if (ocppChargingProcess != null) {
                startRequest.setExternalChargingProcessId(ocppChargingProcess.getOcppChargingProcessId());
            }
            notificationMiddleware.notifyAboutRfidStart(startRequest);
        } else {
            log.info("Remote start found for RFID tag: {} on charger: {}/{}, " +
                            "interpreting as remote start...",
                    parameters.getIdTag(), chargeBoxIdentity, parameters.getConnectorId());
            transactionId = transactionService.insertTransaction(params);
        }

        remoteStartService.remoteStartClosed(chargeBoxIdentity, parameters.getConnectorId(),
                parameters.getIdTag());

        applicationEventPublisher.publishEvent(new OcppTransactionStarted(transactionId, params));

        return new StartTransactionResponse()
                .withIdTagInfo(info)
                .withTransactionId(transactionId);
    }

    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargeBoxIdentity) {
        int transactionId = parameters.getTransactionId();
        log.info("Stopping transaction {} on charge box: {}", transactionId, chargeBoxIdentity);

        String stopReason = parameters.isSetReason() ? parameters.getReason().value() : null;

        // Get the authorization info of the user, before making tx changes (will affectAuthorizationStatus)
        IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(
                parameters.getIdTag(),
                false,
                chargeBoxIdentity,
                null,
                () -> null
        );

        if (transactionId == 0) {
            log.warn("StopRequest without transaction received");
            return new StopTransactionResponse().withIdTagInfo(idTagInfo);
        }

        UpdateTransactionParams params =
                UpdateTransactionParams.builder()
                        .chargeBoxId(chargeBoxIdentity)
                        .transactionId(transactionId)
                        .stopTimestamp(parameters.getTimestamp())
                        .stopMeterValue(Integer.toString(parameters.getMeterStop()))
                        .stopReason(stopReason)
                        .eventTimestamp(DateTime.now())
                        .eventActor(station)
                        .build();

        transactionService.updateTransaction(params);

        TransactionStart transactionStart =
                transactionService.findTransactionStart(transactionId).orElseThrow(() ->
                        new IllegalArgumentException("Invalid transaction id: " + transactionId));
        connectorMeterValueService.insertMeterValues(parameters.getTransactionData(), transactionStart);

        applicationEventPublisher.publishEvent(new OcppTransactionEnded(params));

        return new StopTransactionResponse().withIdTagInfo(idTagInfo);
    }

    public HeartbeatResponse heartbeat(HeartbeatRequest parameters, String chargeBoxIdentity) {
        log.debug("Heartbeat from charge box: {}", chargeBoxIdentity);
        DateTime now = DateTime.now();
        chargePointService.updateChargeboxHeartbeat(chargeBoxIdentity, now);
        return new HeartbeatResponse().withCurrentTime(now);
    }

    public AuthorizeResponse authorize(AuthorizeRequest parameters, String chargeBoxIdentity) {
        log.info("Authorizing ID tag {} on charge box: {}...", parameters.getIdTag(), chargeBoxIdentity);
        // Get the authorization info of the user
        IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(parameters.getIdTag(),
                false,
                chargeBoxIdentity,
                null,
                () -> new IdTagInfo().withStatus(AuthorizationStatus.INVALID)
        );

        return new AuthorizeResponse().withIdTagInfo(idTagInfo);
    }

    /**
     * Dummy implementation. This is new in OCPP 1.5. It must be vendor-specific.
     */
    public DataTransferResponse dataTransfer(DataTransferRequest parameters, String chargeBoxIdentity) {
        log.info("[Data Transfer] Charge point: {}, Vendor Id: {}", chargeBoxIdentity, parameters.getVendorId());
        if (parameters.isSetMessageId()) {
            log.info("[Data Transfer] Message Id: {}", parameters.getMessageId());
        }
        if (parameters.isSetData()) {
            log.info("[Data Transfer] Data: {}", parameters.getData());
        }

        // OCPP requires a status to be set. Since this is a dummy impl, set it to "Accepted".
        // https://github.com/steve-community/steve/pull/36
        return new DataTransferResponse().withStatus(DataTransferStatus.ACCEPTED);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * https://github.com/steve-community/steve/issues/1415
     */
    private Integer getTransactionId(MeterValuesRequest parameters) {
        Integer transactionId = parameters.getTransactionId();
        if (transactionId == null || transactionId == 0) {
            return null;
        }
        return transactionId;
    }
}
