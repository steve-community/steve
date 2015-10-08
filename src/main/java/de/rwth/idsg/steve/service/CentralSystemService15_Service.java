package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.OcppConstants;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2012._06.AuthorizeRequest;
import ocpp.cs._2012._06.AuthorizeResponse;
import ocpp.cs._2012._06.BootNotificationRequest;
import ocpp.cs._2012._06.BootNotificationResponse;
import ocpp.cs._2012._06.DataTransferRequest;
import ocpp.cs._2012._06.DataTransferResponse;
import ocpp.cs._2012._06.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2012._06.DiagnosticsStatusNotificationResponse;
import ocpp.cs._2012._06.FirmwareStatusNotificationRequest;
import ocpp.cs._2012._06.FirmwareStatusNotificationResponse;
import ocpp.cs._2012._06.HeartbeatRequest;
import ocpp.cs._2012._06.HeartbeatResponse;
import ocpp.cs._2012._06.IdTagInfo;
import ocpp.cs._2012._06.MeterValue;
import ocpp.cs._2012._06.MeterValuesRequest;
import ocpp.cs._2012._06.MeterValuesResponse;
import ocpp.cs._2012._06.RegistrationStatus;
import ocpp.cs._2012._06.StartTransactionRequest;
import ocpp.cs._2012._06.StartTransactionResponse;
import ocpp.cs._2012._06.StatusNotificationRequest;
import ocpp.cs._2012._06.StatusNotificationResponse;
import ocpp.cs._2012._06.StopTransactionRequest;
import ocpp.cs._2012._06.StopTransactionResponse;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Transport-level agnostic OCPP 1.5 server service which contains the actual business logic.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2015
 */
@Slf4j
@Service
public class CentralSystemService15_Service {

    @Autowired private OcppServerRepository ocppServerRepository;
    @Autowired private UserService userService;
    @Autowired private OcppConstants ocppConstants;

    public BootNotificationResponse bootNotification(BootNotificationRequest parameters, String chargeBoxIdentity,
                                                     OcppProtocol ocppProtocol) {
        log.debug("Executing bootNotification for {}", chargeBoxIdentity);

        DateTime now = DateTime.now();

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
                                                                    now);

        RegistrationStatus status = isRegistered ? RegistrationStatus.ACCEPTED : RegistrationStatus.REJECTED;

        return new BootNotificationResponse()
                .withStatus(status)
                .withCurrentTime(now)
                .withHeartbeatInterval(ocppConstants.getHeartbeatIntervalInSeconds());
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

        // Mandatory fields
        int connectorId = parameters.getConnectorId();
        String status = parameters.getStatus().value();
        String errorCode = parameters.getErrorCode().value();

        // Optional fields
        String errorInfo = parameters.getInfo();
        DateTime timestamp = parameters.isSetTimestamp() ? parameters.getTimestamp() : DateTime.now();
        String vendorId = parameters.getVendorId();
        String vendorErrorCode = parameters.getVendorErrorCode();

        ocppServerRepository.insertConnectorStatus15(chargeBoxIdentity, connectorId, status, timestamp,
                errorCode, errorInfo, vendorId, vendorErrorCode);

        return new StatusNotificationResponse();
    }

    public MeterValuesResponse meterValues(MeterValuesRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing meterValues for {}", chargeBoxIdentity);

        int connectorId = parameters.getConnectorId();
        Integer transactionId = parameters.getTransactionId();
        if (parameters.isSetValues()) {
            ocppServerRepository.insertMeterValues15(chargeBoxIdentity, connectorId,
                                                     parameters.getValues(), transactionId);
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

        String idTag = parameters.getIdTag();
        int connectorId = parameters.getConnectorId();
        Integer reservationId = parameters.getReservationId();
        DateTime startTimestamp = parameters.getTimestamp();
        String startMeterValue = Integer.toString(parameters.getMeterStart());

        Integer transactionId = ocppServerRepository.insertTransaction15(chargeBoxIdentity, connectorId, idTag,
                                                                         startTimestamp, startMeterValue,
                                                                         reservationId);

        return new StartTransactionResponse()
                .withIdTagInfo(userService.getIdTagInfoV15(idTag))
                .withTransactionId(transactionId);
    }

    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing stopTransaction for {}", chargeBoxIdentity);

        // Get parameters and update transaction in DB
        int transactionId = parameters.getTransactionId();
        DateTime stopTimestamp = parameters.getTimestamp();
        String stopMeterValue = Integer.toString(parameters.getMeterStop());
        ocppServerRepository.updateTransaction(transactionId, stopTimestamp, stopMeterValue);

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
            List<MeterValue> combinedList = parameters.getTransactionData()
                                                      .stream()
                                                      .flatMap(data -> data.getValues().stream())
                                                      .collect(Collectors.toList());

            ocppServerRepository.insertMeterValuesOfTransaction(chargeBoxIdentity, transactionId, combinedList);
        }

        // Get the authorization info of the user
        if (parameters.isSetIdTag()) {
            IdTagInfo idTagInfo = userService.getIdTagInfoV15(parameters.getIdTag());
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
        IdTagInfo idTagInfo = userService.getIdTagInfoV15(idTag);

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

        return new DataTransferResponse();
    }
}
