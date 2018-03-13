/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.service;


import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author david
 */

@Slf4j
@Service
public class CentralSystemService16_Service 
{
    @Autowired private OcppServerRepository ocppServerRepository;
    @Autowired private OcppTagService ocppTagService;
    @Autowired private SettingsRepository settingsRepository;
    @Autowired private NotificationService notificationService;

    public BootNotificationResponse bootNotification(BootNotificationRequest parameters, String chargeBoxIdentity,
                                                     OcppProtocol ocppProtocol) {
        log.debug("Executing bootNotification for {}", chargeBoxIdentity);

        DateTime now = DateTime.now();

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

        boolean isRegistered = ocppServerRepository.updateChargebox(params);
        notificationService.ocppStationBooted(chargeBoxIdentity, isRegistered);

        RegistrationStatus status = isRegistered ? RegistrationStatus.ACCEPTED : RegistrationStatus.REJECTED;

        return new BootNotificationResponse()
                .withStatus(status)
                .withCurrentTime(now)
                .withInterval(settingsRepository.getHeartbeatIntervalInSeconds());
    }

    public FirmwareStatusNotificationResponse firmwareStatusNotification(
            FirmwareStatusNotificationRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing firmwareStatusNotification for {}", chargeBoxIdentity);

        String status = parameters.getStatus().value();
        ocppServerRepository.updateChargeboxFirmwareStatus(chargeBoxIdentity, status);
        return new FirmwareStatusNotificationResponse();
    }

    public StatusNotificationResponse statusNotification(
            StatusNotificationRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing statusNotification for {}", chargeBoxIdentity);

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
            notificationService.ocppStationStatusFailure(
                    chargeBoxIdentity, parameters.getConnectorId(), parameters.getErrorCode().value());
        }

        return new StatusNotificationResponse();
    }

    public MeterValuesResponse meterValues(MeterValuesRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing meterValues for {}", chargeBoxIdentity);

        int connectorId = parameters.getConnectorId();
        Integer transactionId = parameters.getTransactionId();
        if (parameters.isSetMeterValue()) {
            ocppServerRepository.insertMeterValues16(chargeBoxIdentity, connectorId,
                                                     parameters.getMeterValue(), transactionId);
        }
        return new MeterValuesResponse();
    }

    public DiagnosticsStatusNotificationResponse diagnosticsStatusNotification(
            DiagnosticsStatusNotificationRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing diagnosticsStatusNotification for {}", chargeBoxIdentity);

        String status = parameters.getStatus().value();
        ocppServerRepository.updateChargeboxDiagnosticsStatus(chargeBoxIdentity, status);
        return new DiagnosticsStatusNotificationResponse();
    }

    public StartTransactionResponse startTransaction(StartTransactionRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing startTransaction for {}", chargeBoxIdentity);

        InsertTransactionParams params =
                InsertTransactionParams.builder()
                                       .chargeBoxId(chargeBoxIdentity)
                                       .connectorId(parameters.getConnectorId())
                                       .idTag(parameters.getIdTag())
                                       .startTimestamp(parameters.getTimestamp())
                                       .startMeterValue(Integer.toString(parameters.getMeterStart()))
                                       .reservationId(parameters.getReservationId())
                                       .build();

        IdTagInfo info = ocppTagService.getIdTagInfoV16(parameters.getIdTag());
        Integer transactionId = ocppServerRepository.insertTransaction(params);

        return new StartTransactionResponse()
                .withIdTagInfo(info)
                .withTransactionId(transactionId);
    }

    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing stopTransaction for {}", chargeBoxIdentity);

        int transactionId = parameters.getTransactionId();

        UpdateTransactionParams params =
                UpdateTransactionParams.builder()
                                       .chargeBoxId(chargeBoxIdentity)
                                       .transactionId(transactionId)
                                       .stopTimestamp(parameters.getTimestamp())
                                       .stopMeterValue(Integer.toString(parameters.getMeterStop()))
                                       .build();

        ocppServerRepository.updateTransaction(params);

        /**
         * If TransactionData is included:
         *
         * Aggregate MeterValues from multiple TransactionData in one big, happy list. TransactionData is just
         * a container for MeterValues without any additional data/semantics anyway. This enables us to write
         * into DB in one repository call (query), rather than multiple calls as it was before
         * (for each TransactionData)
         *
         * Saved the world again with this micro-optimization.
         */
        if (parameters.isSetTransactionData()) {
            List<MeterValue> combinedList = parameters.getTransactionData();
            combinedList.stream()
                    .flatMap(data2 -> data2.getSampledValue().stream())
                    .collect(Collectors.toList());
            
            ocppServerRepository.insertMeterValuesOfTransaction16(chargeBoxIdentity, transactionId, combinedList);
        }

        // Get the authorization info of the user
        if (parameters.isSetIdTag()) {
            IdTagInfo idTagInfo = ocppTagService.getIdTagInfoV16(parameters.getIdTag());
            return new StopTransactionResponse().withIdTagInfo(idTagInfo);
        } else {
            return new StopTransactionResponse();
        }
    }

    public HeartbeatResponse heartbeat(HeartbeatRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing heartbeat for {}", chargeBoxIdentity);

        DateTime now = DateTime.now();
        ocppServerRepository.updateChargeboxHeartbeat(chargeBoxIdentity, now);

        return new HeartbeatResponse().withCurrentTime(now);
    }

    public AuthorizeResponse authorize(AuthorizeRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing authorize for {}", chargeBoxIdentity);

        // Get the authorization info of the user
        String idTag = parameters.getIdTag();
        IdTagInfo idTagInfo = ocppTagService.getIdTagInfoV16(idTag);

        return new AuthorizeResponse().withIdTagInfo(idTagInfo);
    }

    // Dummy implementation. This is new in OCPP 1.5. It must be vendor-specific.
    public DataTransferResponse dataTransfer(DataTransferRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing dataTransfer for {}", chargeBoxIdentity);

        log.info("[Data Transfer] Charge point: {}, Vendor Id: {}", chargeBoxIdentity, parameters.getVendorId());
        if (parameters.isSetMessageId()) {
            log.info("[Data Transfer] Message Id: {}", parameters.getMessageId());
        }
        if (parameters.isSetData()) {
            log.info("[Data Transfer] Data: {}", parameters.getData());
        }
        //Temporary fix for OccurenceConstraintViolation will always return ACCEPTED
        return new DataTransferResponse().withStatus(DataTransferStatus.ACCEPTED);
    }
}
