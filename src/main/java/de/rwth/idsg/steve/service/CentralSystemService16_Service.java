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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.config.SecurityProfileConfiguration;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.SecurityRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import de.rwth.idsg.steve.service.notification.OccpStationBooted;
import de.rwth.idsg.steve.service.notification.OcppStationStatusFailure;
import de.rwth.idsg.steve.service.notification.OcppTransactionEnded;
import de.rwth.idsg.steve.service.notification.OcppTransactionStarted;
import jooq.steve.db.enums.TransactionStopEventActor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp._2020._03.LogStatusNotificationRequest;
import ocpp._2020._03.LogStatusNotificationResponse;
import ocpp._2020._03.SecurityEventNotificationRequest;
import ocpp._2020._03.SecurityEventNotificationResponse;
import ocpp._2020._03.SignCertificateRequest;
import ocpp._2020._03.SignCertificateResponse;
import ocpp._2020._03.SignedFirmwareStatusNotificationRequest;
import ocpp._2020._03.SignedFirmwareStatusNotificationResponse;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.DataTransferRequest;
import ocpp.cs._2015._10.DataTransferResponse;
import ocpp.cs._2015._10.DataTransferStatus;
import ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2015._10.DiagnosticsStatusNotificationResponse;
import ocpp.cs._2015._10.FirmwareStatusNotificationRequest;
import ocpp.cs._2015._10.FirmwareStatusNotificationResponse;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.HeartbeatResponse;
import ocpp.cs._2015._10.IdTagInfo;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.MeterValuesResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StatusNotificationResponse;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.StopTransactionResponse;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.03.2018
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CentralSystemService16_Service {

    private final OcppServerRepository ocppServerRepository;
    private final SettingsRepository settingsRepository;
    private final OcppTagService ocppTagService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ChargePointRegistrationService chargePointRegistrationService;
    private final SecurityRepository securityRepository;
    private final CertificateSigningService certificateSigningService;
    private final SecurityProfileConfiguration securityConfig;

    public BootNotificationResponse bootNotification(BootNotificationRequest parameters, String chargeBoxIdentity,
                                                     OcppProtocol ocppProtocol) {

        Optional<RegistrationStatus> status = chargePointRegistrationService.getRegistrationStatus(chargeBoxIdentity);
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

            ocppServerRepository.updateChargebox(params);
        }

        return new BootNotificationResponse()
                .withStatus(status.orElse(RegistrationStatus.REJECTED))
                .withCurrentTime(now)
                .withInterval(settingsRepository.getHeartbeatIntervalInSeconds());
    }

    public FirmwareStatusNotificationResponse firmwareStatusNotification(
            FirmwareStatusNotificationRequest parameters, String chargeBoxIdentity) {
        String status = parameters.getStatus().value();
        ocppServerRepository.updateChargeboxFirmwareStatus(chargeBoxIdentity, status);
        return new FirmwareStatusNotificationResponse();
    }

    public StatusNotificationResponse statusNotification(
            StatusNotificationRequest parameters, String chargeBoxIdentity) {
        // Optional field
        DateTime timestamp = parameters.isSetTimestamp() ? parameters.getTimestamp() : DateTime.now();

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

        ocppServerRepository.insertConnectorStatus(params);

        if (parameters.getStatus() == ChargePointStatus.FAULTED) {
            applicationEventPublisher.publishEvent(new OcppStationStatusFailure(
                    chargeBoxIdentity, parameters.getConnectorId(), parameters.getErrorCode().value()));
        }

        return new StatusNotificationResponse();
    }

    public MeterValuesResponse meterValues(MeterValuesRequest parameters, String chargeBoxIdentity) {
        Integer transactionId = getTransactionId(parameters);

        ocppServerRepository.insertMeterValues(
                chargeBoxIdentity,
                parameters.getMeterValue(),
                parameters.getConnectorId(),
                transactionId
        );

        return new MeterValuesResponse();
    }

    public DiagnosticsStatusNotificationResponse diagnosticsStatusNotification(
            DiagnosticsStatusNotificationRequest parameters, String chargeBoxIdentity) {
        String status = parameters.getStatus().value();
        ocppServerRepository.updateChargeboxDiagnosticsStatus(chargeBoxIdentity, status);
        return new DiagnosticsStatusNotificationResponse();
    }

    public StartTransactionResponse startTransaction(StartTransactionRequest parameters, String chargeBoxIdentity) {
        // Get the authorization info of the user, before making tx changes (will affectAuthorizationStatus)
        IdTagInfo info = ocppTagService.getIdTagInfo(
                parameters.getIdTag(),
                true,
                chargeBoxIdentity,
                parameters.getConnectorId(),
                () -> new IdTagInfo().withStatus(AuthorizationStatus.INVALID) // IdTagInfo is required
        );

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

        int transactionId = ocppServerRepository.insertTransaction(params);

        applicationEventPublisher.publishEvent(new OcppTransactionStarted(transactionId, params));

        return new StartTransactionResponse()
                .withIdTagInfo(info)
                .withTransactionId(transactionId);
    }

    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargeBoxIdentity) {
        int transactionId = parameters.getTransactionId();
        String stopReason = parameters.isSetReason() ? parameters.getReason().value() : null;

        // Get the authorization info of the user, before making tx changes (will affectAuthorizationStatus)
        IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(
                parameters.getIdTag(),
                false,
                chargeBoxIdentity,
                null,
                () -> null
        );

        UpdateTransactionParams params =
                UpdateTransactionParams.builder()
                                       .chargeBoxId(chargeBoxIdentity)
                                       .transactionId(transactionId)
                                       .stopTimestamp(parameters.getTimestamp())
                                       .stopMeterValue(Integer.toString(parameters.getMeterStop()))
                                       .stopReason(stopReason)
                                       .eventTimestamp(DateTime.now())
                                       .eventActor(TransactionStopEventActor.station)
                                       .build();

        ocppServerRepository.updateTransaction(params);

        ocppServerRepository.insertMeterValues(chargeBoxIdentity, parameters.getTransactionData(), transactionId);

        applicationEventPublisher.publishEvent(new OcppTransactionEnded(params));

        return new StopTransactionResponse().withIdTagInfo(idTagInfo);
    }

    public HeartbeatResponse heartbeat(HeartbeatRequest parameters, String chargeBoxIdentity) {
        DateTime now = DateTime.now();
        ocppServerRepository.updateChargeboxHeartbeat(chargeBoxIdentity, now);

        return new HeartbeatResponse().withCurrentTime(now);
    }

    public AuthorizeResponse authorize(AuthorizeRequest parameters, String chargeBoxIdentity) {
        // Get the authorization info of the user
        IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(
                parameters.getIdTag(),
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

    public SignCertificateResponse signCertificate(SignCertificateRequest parameters, String chargeBoxIdentity) {
        log.info("Received SignCertificateRequest from '{}' with CSR length: {}", chargeBoxIdentity,
                parameters.getCsr() != null ? parameters.getCsr().length() : 0);

        var response = new SignCertificateResponse();

        try {
            var csr = parameters.getCsr();
            if (csr == null || csr.trim().isEmpty()) {
                log.error("Empty or null CSR received from '{}'", chargeBoxIdentity);
                response.setStatus(SignCertificateResponse.GenericStatusEnumType.REJECTED);

                securityRepository.insertSecurityEvent(
                    chargeBoxIdentity,
                    "SignCertificateRejected",
                    DateTime.now(),
                    "Empty CSR received",
                    "MEDIUM"
                );

                return response;
            }

            if (!certificateSigningService.isInitialized()) {
                log.error("Certificate signing service not initialized. Check TLS configuration.");
                response.setStatus(SignCertificateResponse.GenericStatusEnumType.REJECTED);

                securityRepository.insertSecurityEvent(
                    chargeBoxIdentity,
                    "SignCertificateUnavailable",
                    DateTime.now(),
                    "Certificate signing service not initialized",
                    "HIGH"
                );

                return response;
            }

            var signedCertificatePem = certificateSigningService.signCertificateRequest(csr, chargeBoxIdentity);
            var caCertificatePem = certificateSigningService.getCertificateChain();
            var certificateChain = signedCertificatePem + caCertificatePem;

            var certificateId = securityRepository.insertCertificate(
                chargeBoxIdentity,
                "ChargePointCertificate",
                signedCertificatePem,
                null,
                null,
                null,
                DateTime.now(),
                DateTime.now().plusYears(securityConfig.getCertificateValidityYears()),
                "SHA256WithRSA",
                2048
            );

            securityRepository.insertSecurityEvent(
                chargeBoxIdentity,
                "SignCertificateRequest",
                DateTime.now(),
                "CSR signed successfully, certificate ID: " + certificateId,
                "INFO"
            );

            response.setStatus(SignCertificateResponse.GenericStatusEnumType.ACCEPTED);
            log.info("SignCertificateRequest from '{}' processed successfully. Certificate stored with ID: {}. "
                      + "Send certificate to charge point using CertificateSignedTask with certificate chain: {}",
                    chargeBoxIdentity, certificateId, certificateChain.length());

        } catch (IllegalArgumentException e) {
            log.error("Invalid CSR from '{}': {}", chargeBoxIdentity, e.getMessage());
            response.setStatus(SignCertificateResponse.GenericStatusEnumType.REJECTED);

            securityRepository.insertSecurityEvent(
                chargeBoxIdentity,
                "SignCertificateRejected",
                DateTime.now(),
                "Invalid CSR: " + e.getMessage(),
                "HIGH"
            );

        } catch (Exception e) {
            log.error("Error signing certificate for '{}': {}", chargeBoxIdentity, e.getMessage(), e);
            response.setStatus(SignCertificateResponse.GenericStatusEnumType.REJECTED);

            securityRepository.insertSecurityEvent(
                chargeBoxIdentity,
                "SignCertificateError",
                DateTime.now(),
                "Error signing CSR: " + e.getMessage(),
                "HIGH"
            );
        }

        return response;
    }

    public SecurityEventNotificationResponse securityEventNotification(SecurityEventNotificationRequest parameters,
                                                                       String chargeBoxIdentity) {
        var eventType = parameters.getType();
        var eventTimestamp = parameters.getTimestamp();
        var techInfo = parameters.getTechInfo();

        log.info("SecurityEvent from '{}': type={}, timestamp={}", chargeBoxIdentity, eventType, eventTimestamp);

        try {
            var severity = determineSeverity(eventType);

            securityRepository.insertSecurityEvent(
                chargeBoxIdentity,
                eventType,
                eventTimestamp,
                techInfo != null ? techInfo : "",
                severity
            );

            if ("CRITICAL".equals(severity) || "HIGH".equals(severity)) {
                log.warn("High-severity security event from '{}': {}", chargeBoxIdentity, eventType);
            }

        } catch (Exception e) {
            log.error("Error storing security event from '{}': {}", chargeBoxIdentity, e.getMessage(), e);
        }

        return new SecurityEventNotificationResponse();
    }

    public SignedFirmwareStatusNotificationResponse signedFirmwareStatusNotification(
            SignedFirmwareStatusNotificationRequest parameters, String chargeBoxIdentity) {
        var status = parameters.getStatus() != null ? parameters.getStatus().toString() : "Unknown";
        var requestId = parameters.getRequestId();

        log.info("FirmwareStatus from '{}': status={}, requestId={}", chargeBoxIdentity, status, requestId);

        try {
            var firmwareUpdate = securityRepository.getCurrentFirmwareUpdate(chargeBoxIdentity);

            if (firmwareUpdate != null) {
                securityRepository.updateFirmwareUpdateStatus(firmwareUpdate.getFirmwareUpdateId(), status);
                log.info("Updated firmware status for chargeBox '{}' to '{}'", chargeBoxIdentity, status);
            } else {
                log.warn("No firmware update found for chargeBox '{}'", chargeBoxIdentity);
            }

            securityRepository.insertSecurityEvent(
                chargeBoxIdentity,
                "FirmwareStatusNotification",
                DateTime.now(),
                "Firmware status: " + status + (requestId != null ? ", requestId: " + requestId : ""),
                "INFO"
            );

        } catch (Exception e) {
            log.error("Error processing firmware status notification from '{}': {}",
                    chargeBoxIdentity, e.getMessage(), e);
        }

        return new SignedFirmwareStatusNotificationResponse();
    }

    public LogStatusNotificationResponse logStatusNotification(LogStatusNotificationRequest parameters, String chargeBoxIdentity) {
        var status = parameters.getStatus() != null ? parameters.getStatus().toString() : "Unknown";
        var requestId = parameters.getRequestId();

        log.info("LogStatus from '{}': status={}, requestId={}", chargeBoxIdentity, status, requestId);

        try {
            if (requestId != null) {
                var logFile = securityRepository.getLogFile(requestId);

                if (logFile != null) {
                    securityRepository.updateLogFileStatus(requestId, status, null);
                    log.info("Updated log file status for requestId {} to '{}'", requestId, status);
                } else {
                    log.warn("No log file found for requestId {}", requestId);
                }
            }

            securityRepository.insertSecurityEvent(
                chargeBoxIdentity,
                "LogStatusNotification",
                DateTime.now(),
                "Log upload status: " + status + (requestId != null ? ", requestId: " + requestId : ""),
                "INFO"
            );

        } catch (Exception e) {
            log.error("Error processing log status notification from '{}': {}", chargeBoxIdentity, e.getMessage(), e);
        }

        return new LogStatusNotificationResponse();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * https://github.com/steve-community/steve/issues/1415
     */
    private Integer getTransactionId(MeterValuesRequest parameters) {
        Integer transactionId = parameters.getTransactionId();
        if (transactionId == null) {
            return null;
        } else if (transactionId < 1) {
            log.warn("MeterValues transactionId is invalid ({}), ignoring it", transactionId);
            return null;
        }
        return transactionId;
    }

    private static String determineSeverity(String eventType) {
        if (eventType == null) {
            return "INFO";
        }

        var upperType = eventType.toUpperCase();

        if (upperType.contains("ATTACK") || upperType.contains("BREACH") || upperType.contains("TAMPER")) {
            return "CRITICAL";
        }

        if (upperType.contains("FAIL") || upperType.contains("ERROR") || upperType.contains("INVALID")
            || upperType.contains("UNAUTHORIZED") || upperType.contains("REJECT")) {
            return "HIGH";
        }

        if (upperType.contains("WARNING") || upperType.contains("EXPIR")) {
            return "MEDIUM";
        }

        return "INFO";
    }
}
