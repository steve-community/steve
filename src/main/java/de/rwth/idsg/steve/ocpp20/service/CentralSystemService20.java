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
package de.rwth.idsg.steve.ocpp20.service;

import de.rwth.idsg.steve.ocpp20.model.*;
import de.rwth.idsg.steve.ocpp20.repository.Ocpp20Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CentralSystemService20 {

    private final Ocpp20Repository ocpp20Repository;

    public BootNotificationResponse handleBootNotification(BootNotificationRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 BootNotification from '{}': vendor={}, model={}",
                chargeBoxId,
                request.getChargingStation().getModel(),
                request.getChargingStation().getVendorName());

        BootNotificationResponse response = new BootNotificationResponse();
        response.setStatus(RegistrationStatusEnum.ACCEPTED);
        response.setCurrentTime(OffsetDateTime.now());
        response.setInterval(300);

        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setReasonCode("OCPP20Experimental");
        statusInfo.setAdditionalInfo("OCPP 2.0.1 support is experimental");
        response.setStatusInfo(statusInfo);

        ocpp20Repository.insertBootNotification(chargeBoxId, request, response);

        return response;
    }

    public AuthorizeResponse handleAuthorize(AuthorizeRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 Authorize from '{}': idToken={}",
                chargeBoxId,
                request.getIdToken().getIdToken());

        IdTokenInfo idTokenInfo = new IdTokenInfo();
        idTokenInfo.setStatus(AuthorizationStatusEnum.ACCEPTED);

        AuthorizeResponse response = new AuthorizeResponse();
        response.setIdTokenInfo(idTokenInfo);

        ocpp20Repository.insertAuthorization(chargeBoxId, request, response);

        return response;
    }

    public TransactionEventResponse handleTransactionEvent(TransactionEventRequest request, String chargeBoxId) {
        String transactionId = request.getTransactionInfo() != null ? request.getTransactionInfo().getTransactionId() : null;

        log.info("OCPP 2.0 TransactionEvent from '{}': eventType={}, transactionId={}, seqNo={}",
                chargeBoxId,
                request.getEventType(),
                transactionId,
                request.getSeqNo());

        if (transactionId != null) {
            if (request.getEventType() == TransactionEventEnum.STARTED) {
                ocpp20Repository.insertTransaction(chargeBoxId, transactionId, request);
            } else if (request.getEventType() == TransactionEventEnum.ENDED) {
                ocpp20Repository.updateTransaction(transactionId, request);
            }

            ocpp20Repository.insertTransactionEvent(chargeBoxId, transactionId, request);
        }

        TransactionEventResponse response = new TransactionEventResponse();

        IdTokenInfo idTokenInfo = new IdTokenInfo();
        idTokenInfo.setStatus(AuthorizationStatusEnum.ACCEPTED);
        response.setIdTokenInfo(idTokenInfo);

        return response;
    }

    public StatusNotificationResponse handleStatusNotification(StatusNotificationRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 StatusNotification from '{}': connectorId={}, status={}",
                chargeBoxId,
                request.getConnectorId(),
                request.getConnectorStatus());

        return new StatusNotificationResponse();
    }

    public HeartbeatResponse handleHeartbeat(HeartbeatRequest request, String chargeBoxId) {
        log.debug("OCPP 2.0 Heartbeat from '{}'", chargeBoxId);

        HeartbeatResponse response = new HeartbeatResponse();
        response.setCurrentTime(OffsetDateTime.now());
        return response;
    }

    public MeterValuesResponse handleMeterValues(MeterValuesRequest request, String chargeBoxId) {
        log.debug("OCPP 2.0 MeterValues from '{}': evseId={}", chargeBoxId, request.getEvseId());
        return new MeterValuesResponse();
    }

    public SignCertificateResponse handleSignCertificate(SignCertificateRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 SignCertificate from '{}': type={}", chargeBoxId, request.getCertificateType());

        SignCertificateResponse response = new SignCertificateResponse();
        response.setStatus(GenericStatusEnum.ACCEPTED);
        return response;
    }

    public SecurityEventNotificationResponse handleSecurityEventNotification(SecurityEventNotificationRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 SecurityEventNotification from '{}': type={}", chargeBoxId, request.getType());
        return new SecurityEventNotificationResponse();
    }

    public NotifyReportResponse handleNotifyReport(NotifyReportRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 NotifyReport from '{}': requestId={}, seqNo={}",
                chargeBoxId, request.getRequestId(), request.getSeqNo());

        if (request.getReportData() != null) {
            for (ReportData reportData : request.getReportData()) {
                String componentName = reportData.getComponent() != null ? reportData.getComponent().getName() : "Unknown";
                String variableName = reportData.getVariable() != null ? reportData.getVariable().getName() : "Unknown";

                if (reportData.getVariableAttribute() != null && !reportData.getVariableAttribute().isEmpty()) {
                    for (VariableAttribute attr : reportData.getVariableAttribute()) {
                        String value = attr.getValue();
                        if (value != null) {
                            ocpp20Repository.upsertVariable(chargeBoxId, componentName, variableName, value);
                        }
                    }
                }
            }
        }

        return new NotifyReportResponse();
    }

    public NotifyEventResponse handleNotifyEvent(NotifyEventRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 NotifyEvent from '{}': seqNo={}, events={}",
                chargeBoxId, request.getSeqNo(), request.getEventData().size());
        return new NotifyEventResponse();
    }

    public FirmwareStatusNotificationResponse handleFirmwareStatusNotification(FirmwareStatusNotificationRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 FirmwareStatusNotification from '{}': status={}",
                chargeBoxId, request.getStatus());
        return new FirmwareStatusNotificationResponse();
    }

    public LogStatusNotificationResponse handleLogStatusNotification(LogStatusNotificationRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 LogStatusNotification from '{}': status={}",
                chargeBoxId, request.getStatus());
        return new LogStatusNotificationResponse();
    }

    public NotifyEVChargingNeedsResponse handleNotifyEVChargingNeeds(NotifyEVChargingNeedsRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 NotifyEVChargingNeeds from '{}': evseId={}",
                chargeBoxId, request.getEvseId());

        NotifyEVChargingNeedsResponse response = new NotifyEVChargingNeedsResponse();
        response.setStatus(NotifyEVChargingNeedsStatusEnum.ACCEPTED);
        return response;
    }

    public ReportChargingProfilesResponse handleReportChargingProfiles(ReportChargingProfilesRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 ReportChargingProfiles from '{}': requestId={}, evseId={}",
                chargeBoxId, request.getRequestId(), request.getEvseId());
        return new ReportChargingProfilesResponse();
    }

    public ReservationStatusUpdateResponse handleReservationStatusUpdate(ReservationStatusUpdateRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 ReservationStatusUpdate from '{}': reservationId={}, status={}",
                chargeBoxId, request.getReservationId(), request.getReservationUpdateStatus());
        return new ReservationStatusUpdateResponse();
    }

    public ClearedChargingLimitResponse handleClearedChargingLimit(ClearedChargingLimitRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 ClearedChargingLimit from '{}': source={}",
                chargeBoxId, request.getChargingLimitSource());
        return new ClearedChargingLimitResponse();
    }

    public NotifyChargingLimitResponse handleNotifyChargingLimit(NotifyChargingLimitRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 NotifyChargingLimit from '{}': evseId={}",
                chargeBoxId, request.getEvseId());
        return new NotifyChargingLimitResponse();
    }

    public NotifyCustomerInformationResponse handleNotifyCustomerInformation(NotifyCustomerInformationRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 NotifyCustomerInformation from '{}': requestId={}",
                chargeBoxId, request.getRequestId());
        return new NotifyCustomerInformationResponse();
    }

    public NotifyDisplayMessagesResponse handleNotifyDisplayMessages(NotifyDisplayMessagesRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 NotifyDisplayMessages from '{}': requestId={}",
                chargeBoxId, request.getRequestId());
        return new NotifyDisplayMessagesResponse();
    }

    public NotifyEVChargingScheduleResponse handleNotifyEVChargingSchedule(NotifyEVChargingScheduleRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 NotifyEVChargingSchedule from '{}': evseId={}",
                chargeBoxId, request.getEvseId());
        return new NotifyEVChargingScheduleResponse();
    }

    public NotifyMonitoringReportResponse handleNotifyMonitoringReport(NotifyMonitoringReportRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 NotifyMonitoringReport from '{}': requestId={}",
                chargeBoxId, request.getRequestId());
        return new NotifyMonitoringReportResponse();
    }

    public PublishFirmwareStatusNotificationResponse handlePublishFirmwareStatusNotification(PublishFirmwareStatusNotificationRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 PublishFirmwareStatusNotification from '{}': status={}",
                chargeBoxId, request.getStatus());
        return new PublishFirmwareStatusNotificationResponse();
    }

    public DataTransferResponse handleDataTransfer(DataTransferRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 DataTransfer from '{}': vendorId={}, messageId={}",
                chargeBoxId, request.getVendorId(), request.getMessageId());

        DataTransferResponse response = new DataTransferResponse();
        response.setStatus(DataTransferStatusEnum.ACCEPTED);

        if (request.getData() != null) {
            response.setData(request.getData());
        }

        return response;
    }

    public RequestStartTransactionResponse handleRequestStartTransaction(RequestStartTransactionRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 RequestStartTransaction from '{}': remoteStartId={}, evseId={}",
                chargeBoxId, request.getRemoteStartId(), request.getEvseId());

        RequestStartTransactionResponse response = new RequestStartTransactionResponse();
        response.setStatus(RequestStartStopStatusEnum.ACCEPTED);

        String transactionId = "TRX-" + System.currentTimeMillis();
        response.setTransactionId(transactionId);

        return response;
    }

    public RequestStopTransactionResponse handleRequestStopTransaction(RequestStopTransactionRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 RequestStopTransaction from '{}': transactionId={}",
                chargeBoxId, request.getTransactionId());

        RequestStopTransactionResponse response = new RequestStopTransactionResponse();
        response.setStatus(RequestStartStopStatusEnum.ACCEPTED);

        return response;
    }

    public Get15118EVCertificateResponse handleGet15118EVCertificate(Get15118EVCertificateRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 Get15118EVCertificate from '{}': iso15118SchemaVersion={}",
                chargeBoxId, request.getIso15118SchemaVersion());

        Get15118EVCertificateResponse response = new Get15118EVCertificateResponse();
        response.setStatus(Iso15118EVCertificateStatusEnum.ACCEPTED);
        response.setExiResponse("BASE64_ENCODED_EXI_RESPONSE");

        return response;
    }

    public GetCertificateStatusResponse handleGetCertificateStatus(GetCertificateStatusRequest request, String chargeBoxId) {
        log.info("OCPP 2.0 GetCertificateStatus from '{}'",
                chargeBoxId);

        GetCertificateStatusResponse response = new GetCertificateStatusResponse();
        response.setStatus(GetCertificateStatusEnum.ACCEPTED);

        if (request.getOcspRequestData() != null) {
            response.setOcspResult("BASE64_ENCODED_OCSP_RESPONSE");
        }

        return response;
    }
}