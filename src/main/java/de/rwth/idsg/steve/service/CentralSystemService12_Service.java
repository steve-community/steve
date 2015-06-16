package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.OcppConstants;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2010._08.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

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
    @Autowired private OcppConstants ocppConstants;

    public BootNotificationResponse bootNotification(BootNotificationRequest parameters, String chargeBoxIdentity,
                                                     OcppProtocol ocppProtocol) {
        log.debug("Executing bootNotification for {}", chargeBoxIdentity);

        DateTime now = new DateTime();

        boolean isRegistered = ocppServerRepository.updateChargebox(ocppProtocol,
                                                                    parameters.getChargePointVendor(),
                                                                    parameters.getChargePointModel(),
                                                                    parameters.getChargePointSerialNumber(),
                                                                    parameters.getChargeBoxSerialNumber(),
                                                                    parameters.getFirmwareVersion(),
                                                                    parameters.getIccid(),
                                                                    parameters.getImsi(),
                                                                    parameters.getMeterType(),
                                                                    parameters.getMeterSerialNumber(),
                                                                    chargeBoxIdentity,
                                                                    new Timestamp(now.getMillis()));

        if (isRegistered) {
            return new BootNotificationResponse()
                    .withStatus(RegistrationStatus.ACCEPTED)
                    .withCurrentTime(now)
                    .withHeartbeatInterval(ocppConstants.getHeartbeatIntervalInSeconds());

        } else {
            return new BootNotificationResponse()
                    .withStatus(RegistrationStatus.REJECTED);
        }
    }

    public FirmwareStatusNotificationResponse firmwareStatusNotification(FirmwareStatusNotificationRequest parameters,
                                                                         String chargeBoxIdentity) {
        log.debug("Executing firmwareStatusNotification for {}", chargeBoxIdentity);

        String status = parameters.getStatus().value();
        ocppServerRepository.updateChargeboxFirmwareStatus(chargeBoxIdentity, status);
        return new FirmwareStatusNotificationResponse();
    }

    public StatusNotificationResponse statusNotification(StatusNotificationRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing statusNotification for {}", chargeBoxIdentity);

        int connectorId = parameters.getConnectorId();
        String status = parameters.getStatus().value();
        String errorCode = parameters.getErrorCode().value();
        ocppServerRepository.insertConnectorStatus12(chargeBoxIdentity, connectorId, status,
                                                     DateTimeUtils.getCurrentTimestamp(), errorCode);
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

    public DiagnosticsStatusNotificationResponse diagnosticsStatusNotification(DiagnosticsStatusNotificationRequest parameters,
                                                                               String chargeBoxIdentity) {
        log.debug("Executing diagnosticsStatusNotification for {}", chargeBoxIdentity);

        String status = parameters.getStatus().value();
        ocppServerRepository.updateChargeboxDiagnosticsStatus(chargeBoxIdentity, status);
        return new DiagnosticsStatusNotificationResponse();
    }

    public StartTransactionResponse startTransaction(StartTransactionRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing startTransaction for {}", chargeBoxIdentity);

        // Get the authorization info of the user
        String idTag = parameters.getIdTag();
        IdTagInfo idTagInfo = userService.getIdTagInfoV12(idTag);

        StartTransactionResponse response = new StartTransactionResponse().withIdTagInfo(idTagInfo);

        if (AuthorizationStatus.ACCEPTED.equals(idTagInfo.getStatus())) {
            int connectorId = parameters.getConnectorId();
            Timestamp startTimestamp = new Timestamp(parameters.getTimestamp().getMillis());
            String startMeterValue = Integer.toString(parameters.getMeterStart());

            Integer transactionId = ocppServerRepository.insertTransaction12(chargeBoxIdentity,connectorId, idTag,
                                                                             startTimestamp, startMeterValue);

            response.setTransactionId(transactionId);
        }
        return response;
    }

    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing stopTransaction for {}", chargeBoxIdentity);

        int transactionId = parameters.getTransactionId();
        Timestamp stopTimestamp = new Timestamp(parameters.getTimestamp().getMillis());
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

        DateTime now = new DateTime();
        ocppServerRepository.updateChargeboxHeartbeat(chargeBoxIdentity, new Timestamp(now.getMillis()));

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
