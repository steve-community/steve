package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2010._08.AuthorizeRequest;
import ocpp.cs._2010._08.AuthorizeResponse;
import ocpp.cs._2010._08.BootNotificationRequest;
import ocpp.cs._2010._08.BootNotificationResponse;
import ocpp.cs._2010._08.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2010._08.DiagnosticsStatusNotificationResponse;
import ocpp.cs._2010._08.FirmwareStatusNotificationRequest;
import ocpp.cs._2010._08.FirmwareStatusNotificationResponse;
import ocpp.cs._2010._08.HeartbeatRequest;
import ocpp.cs._2010._08.HeartbeatResponse;
import ocpp.cs._2010._08.IdTagInfo;
import ocpp.cs._2010._08.MeterValuesRequest;
import ocpp.cs._2010._08.MeterValuesResponse;
import ocpp.cs._2010._08.RegistrationStatus;
import ocpp.cs._2010._08.StartTransactionRequest;
import ocpp.cs._2010._08.StartTransactionResponse;
import ocpp.cs._2010._08.StatusNotificationRequest;
import ocpp.cs._2010._08.StatusNotificationResponse;
import ocpp.cs._2010._08.StopTransactionRequest;
import ocpp.cs._2010._08.StopTransactionResponse;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Transport-level agnostic OCPP 1.2 server service which contains the actual business logic.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
@Slf4j
@Service
public class CentralSystemService12_Service {

    @Autowired private OcppServerRepository ocppServerRepository;
    @Autowired private UserService userService;
    @Autowired private SettingsRepository settingsRepository;

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

        if (isRegistered) {
            return new BootNotificationResponse()
                    .withStatus(RegistrationStatus.ACCEPTED)
                    .withCurrentTime(now)
                    .withHeartbeatInterval(settingsRepository.getHeartbeatIntervalInSeconds());

        } else {
            return new BootNotificationResponse()
                    .withStatus(RegistrationStatus.REJECTED);
        }
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

        InsertConnectorStatusParams params =
                InsertConnectorStatusParams.builder()
                                           .chargeBoxId(chargeBoxIdentity)
                                           .connectorId(parameters.getConnectorId())
                                           .status(parameters.getStatus().value())
                                           .errorCode(parameters.getErrorCode().value())
                                           .timestamp(DateTime.now())
                                           .build();

        ocppServerRepository.insertConnectorStatus(params);
        return new StatusNotificationResponse();
    }

    public MeterValuesResponse meterValues(MeterValuesRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing meterValues for {}", chargeBoxIdentity);

        int connectorId = parameters.getConnectorId();
        if (parameters.isSetValues()) {
            ocppServerRepository.insertMeterValues12(chargeBoxIdentity, connectorId, parameters.getValues());
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
                                       .build();

        Integer transactionId = ocppServerRepository.insertTransaction(params);

        return new StartTransactionResponse()
                .withIdTagInfo(userService.getIdTagInfoV12(parameters.getIdTag()))
                .withTransactionId(transactionId);
    }

    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing stopTransaction for {}", chargeBoxIdentity);

        int transactionId = parameters.getTransactionId();
        DateTime stopTimestamp = parameters.getTimestamp();
        String stopMeterValue = Integer.toString(parameters.getMeterStop());
        ocppServerRepository.updateTransaction(transactionId, stopTimestamp, stopMeterValue);

        // Get the authorization info of the user
        if (parameters.isSetIdTag()) {
            IdTagInfo idTagInfo = userService.getIdTagInfoV12(parameters.getIdTag());
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
        IdTagInfo idTagInfo = userService.getIdTagInfoV12(idTag);

        return new AuthorizeResponse().withIdTagInfo(idTagInfo);
    }
}
