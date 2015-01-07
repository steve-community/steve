package de.rwth.idsg.steve.service;

import com.google.common.base.Optional;
import de.rwth.idsg.steve.OcppConstants;
import de.rwth.idsg.steve.OcppVersion;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2010._08.*;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.JAXWSAConstants;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingType;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.SOAPBinding;
import java.sql.Timestamp;
import java.util.concurrent.Future;

/**
 * Service implementation of OCPP V1.2
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *  
 */
@Slf4j
@Service
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@WebService(
        serviceName = "CentralSystemService",
        portName = "CentralSystemServiceSoap12",
        targetNamespace = "urn://Ocpp/Cs/2010/08/",
        endpointInterface = "ocpp.cs._2010._08.CentralSystemService")
public class CentralSystemService12_Server implements CentralSystemService {

    @Resource private WebServiceContext webServiceContext;
    @Autowired private OcppServerRepository ocppServerRepository;
    @Autowired private UserService userService;
    @Autowired private OcppConstants ocppConstants;

    public BootNotificationResponse bootNotification(BootNotificationRequest parameters, String chargeBoxIdentity) {
        log.debug("Executing bootNotification for {}", chargeBoxIdentity);

        // Get the Address value from WS-A Header
        MessageContext messageContext = webServiceContext.getMessageContext();
        AddressingProperties addressProp = (AddressingProperties) messageContext.get(JAXWSAConstants.ADDRESSING_PROPERTIES_INBOUND);
        String endpointAddress = addressProp.getFrom().getAddress().getValue();

        DateTime now = new DateTime();

        boolean isRegistered = ocppServerRepository.updateChargebox(endpointAddress,
                                                                     OcppVersion.V_12,
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
                                                      DateTimeUtils.getCurrentDateTime(), errorCode);
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

            Optional<Integer> transactionId = ocppServerRepository.insertTransaction12(chargeBoxIdentity,
                                                                                        connectorId,
                                                                                        idTag,
                                                                                        startTimestamp,
                                                                                        startMeterValue);

            if (transactionId.isPresent()) {
                response.setTransactionId(transactionId.get());
            }
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

    // -------------------------------------------------------------------------
    // No-op
    // -------------------------------------------------------------------------

    @Override
    public Response<BootNotificationResponse> bootNotificationAsync(BootNotificationRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> bootNotificationAsync(BootNotificationRequest parameters, String chargeBoxIdentity, AsyncHandler<BootNotificationResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<FirmwareStatusNotificationResponse> firmwareStatusNotificationAsync(FirmwareStatusNotificationRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> firmwareStatusNotificationAsync(FirmwareStatusNotificationRequest parameters, String chargeBoxIdentity, AsyncHandler<FirmwareStatusNotificationResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<MeterValuesResponse> meterValuesAsync(MeterValuesRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> meterValuesAsync(MeterValuesRequest parameters, String chargeBoxIdentity, AsyncHandler<MeterValuesResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<DiagnosticsStatusNotificationResponse> diagnosticsStatusNotificationAsync(DiagnosticsStatusNotificationRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> diagnosticsStatusNotificationAsync(DiagnosticsStatusNotificationRequest parameters, String chargeBoxIdentity, AsyncHandler<DiagnosticsStatusNotificationResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<StatusNotificationResponse> statusNotificationAsync(StatusNotificationRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> statusNotificationAsync(StatusNotificationRequest parameters, String chargeBoxIdentity, AsyncHandler<StatusNotificationResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<StopTransactionResponse> stopTransactionAsync(StopTransactionRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> stopTransactionAsync(StopTransactionRequest parameters, String chargeBoxIdentity, AsyncHandler<StopTransactionResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<AuthorizeResponse> authorizeAsync(AuthorizeRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> authorizeAsync(AuthorizeRequest parameters, String chargeBoxIdentity, AsyncHandler<AuthorizeResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<HeartbeatResponse> heartbeatAsync(HeartbeatRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> heartbeatAsync(HeartbeatRequest parameters, String chargeBoxIdentity, AsyncHandler<HeartbeatResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<StartTransactionResponse> startTransactionAsync(StartTransactionRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> startTransactionAsync(StartTransactionRequest parameters, String chargeBoxIdentity, AsyncHandler<StartTransactionResponse> asyncHandler) {
        return null;
    }
}