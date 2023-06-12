package ocpp.cs._2015._10;

import java.util.concurrent.Future;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.Action;
import jakarta.xml.ws.AsyncHandler;
import jakarta.xml.ws.Response;

/**
 * This class was generated by Apache CXF 3.2.4
 * 2023-02-25T02:57:51.610Z
 * Generated source version: 3.2.4
 *
 */
@WebService(targetNamespace = "urn://Ocpp/Cs/2015/10/", name = "CentralSystemService")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface CentralSystemService {

    @WebMethod(operationName = "StopTransaction")
    public Response<StopTransactionResponse> stopTransactionAsync(
        @WebParam(partName = "parameters", name = "stopTransactionRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        StopTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "StopTransaction")
    public Future<?> stopTransactionAsync(
        @WebParam(partName = "parameters", name = "stopTransactionRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        StopTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<StopTransactionResponse> asyncHandler
    );

    @WebMethod(operationName = "StopTransaction", action = "/StopTransaction")
    @Action(input = "/StopTransaction", output = "/StopTransactionResponse")
    @WebResult(name = "stopTransactionResponse", targetNamespace = "urn://Ocpp/Cs/2015/10/", partName = "parameters")
    public StopTransactionResponse stopTransaction(
        @WebParam(partName = "parameters", name = "stopTransactionRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        StopTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "StatusNotification")
    public Response<StatusNotificationResponse> statusNotificationAsync(
        @WebParam(partName = "parameters", name = "statusNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        StatusNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "StatusNotification")
    public Future<?> statusNotificationAsync(
        @WebParam(partName = "parameters", name = "statusNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        StatusNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<StatusNotificationResponse> asyncHandler
    );

    @WebMethod(operationName = "StatusNotification", action = "/StatusNotification")
    @Action(input = "/StatusNotification", output = "/StatusNotificationResponse")
    @WebResult(name = "statusNotificationResponse", targetNamespace = "urn://Ocpp/Cs/2015/10/", partName = "parameters")
    public StatusNotificationResponse statusNotification(
        @WebParam(partName = "parameters", name = "statusNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        StatusNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "Authorize")
    public Response<AuthorizeResponse> authorizeAsync(
        @WebParam(partName = "parameters", name = "authorizeRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        AuthorizeRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "Authorize")
    public Future<?> authorizeAsync(
        @WebParam(partName = "parameters", name = "authorizeRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        AuthorizeRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<AuthorizeResponse> asyncHandler
    );

    @WebMethod(operationName = "Authorize", action = "/Authorize")
    @Action(input = "/Authorize", output = "/AuthorizeResponse")
    @WebResult(name = "authorizeResponse", targetNamespace = "urn://Ocpp/Cs/2015/10/", partName = "parameters")
    public AuthorizeResponse authorize(
        @WebParam(partName = "parameters", name = "authorizeRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        AuthorizeRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "StartTransaction")
    public Response<StartTransactionResponse> startTransactionAsync(
        @WebParam(partName = "parameters", name = "startTransactionRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        StartTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "StartTransaction")
    public Future<?> startTransactionAsync(
        @WebParam(partName = "parameters", name = "startTransactionRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        StartTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<StartTransactionResponse> asyncHandler
    );

    @WebMethod(operationName = "StartTransaction", action = "/StartTransaction")
    @Action(input = "/StartTransaction", output = "/StartTransactionResponse")
    @WebResult(name = "startTransactionResponse", targetNamespace = "urn://Ocpp/Cs/2015/10/", partName = "parameters")
    public StartTransactionResponse startTransaction(
        @WebParam(partName = "parameters", name = "startTransactionRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        StartTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "FirmwareStatusNotification")
    public Response<FirmwareStatusNotificationResponse> firmwareStatusNotificationAsync(
        @WebParam(partName = "parameters", name = "firmwareStatusNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        FirmwareStatusNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "FirmwareStatusNotification")
    public Future<?> firmwareStatusNotificationAsync(
        @WebParam(partName = "parameters", name = "firmwareStatusNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        FirmwareStatusNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<FirmwareStatusNotificationResponse> asyncHandler
    );

    @WebMethod(operationName = "FirmwareStatusNotification", action = "/FirmwareStatusNotification")
    @Action(input = "/FirmwareStatusNotification", output = "/FirmwareStatusNotificationResponse")
    @WebResult(name = "firmwareStatusNotificationResponse", targetNamespace = "urn://Ocpp/Cs/2015/10/", partName = "parameters")
    public FirmwareStatusNotificationResponse firmwareStatusNotification(
        @WebParam(partName = "parameters", name = "firmwareStatusNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        FirmwareStatusNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "BootNotification")
    public Response<BootNotificationResponse> bootNotificationAsync(
        @WebParam(partName = "parameters", name = "bootNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        BootNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "BootNotification")
    public Future<?> bootNotificationAsync(
        @WebParam(partName = "parameters", name = "bootNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        BootNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<BootNotificationResponse> asyncHandler
    );

    @WebMethod(operationName = "BootNotification", action = "/BootNotification")
    @Action(input = "/BootNotification", output = "/BootNotificationResponse")
    @WebResult(name = "bootNotificationResponse", targetNamespace = "urn://Ocpp/Cs/2015/10/", partName = "parameters")
    public BootNotificationResponse bootNotification(
        @WebParam(partName = "parameters", name = "bootNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        BootNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "Heartbeat")
    public Response<HeartbeatResponse> heartbeatAsync(
        @WebParam(partName = "parameters", name = "heartbeatRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        HeartbeatRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "Heartbeat")
    public Future<?> heartbeatAsync(
        @WebParam(partName = "parameters", name = "heartbeatRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        HeartbeatRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<HeartbeatResponse> asyncHandler
    );

    @WebMethod(operationName = "Heartbeat", action = "/Heartbeat")
    @Action(input = "/Heartbeat", output = "/HeartbeatResponse")
    @WebResult(name = "heartbeatResponse", targetNamespace = "urn://Ocpp/Cs/2015/10/", partName = "parameters")
    public HeartbeatResponse heartbeat(
        @WebParam(partName = "parameters", name = "heartbeatRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        HeartbeatRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "MeterValues")
    public Response<MeterValuesResponse> meterValuesAsync(
        @WebParam(partName = "parameters", name = "meterValuesRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        MeterValuesRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "MeterValues")
    public Future<?> meterValuesAsync(
        @WebParam(partName = "parameters", name = "meterValuesRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        MeterValuesRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<MeterValuesResponse> asyncHandler
    );

    @WebMethod(operationName = "MeterValues", action = "/MeterValues")
    @Action(input = "/MeterValues", output = "/MeterValuesResponse")
    @WebResult(name = "meterValuesResponse", targetNamespace = "urn://Ocpp/Cs/2015/10/", partName = "parameters")
    public MeterValuesResponse meterValues(
        @WebParam(partName = "parameters", name = "meterValuesRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        MeterValuesRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "DataTransfer")
    public Response<DataTransferResponse> dataTransferAsync(
        @WebParam(partName = "parameters", name = "dataTransferRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        DataTransferRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "DataTransfer")
    public Future<?> dataTransferAsync(
        @WebParam(partName = "parameters", name = "dataTransferRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        DataTransferRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<DataTransferResponse> asyncHandler
    );

    @WebMethod(operationName = "DataTransfer", action = "/DataTransfer")
    @Action(input = "/DataTransfer", output = "/DataTransferResponse")
    @WebResult(name = "dataTransferResponse", targetNamespace = "urn://Ocpp/Cs/2015/10/", partName = "parameters")
    public DataTransferResponse dataTransfer(
        @WebParam(partName = "parameters", name = "dataTransferRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        DataTransferRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "DiagnosticsStatusNotification")
    public Response<DiagnosticsStatusNotificationResponse> diagnosticsStatusNotificationAsync(
        @WebParam(partName = "parameters", name = "diagnosticsStatusNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        DiagnosticsStatusNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );

    @WebMethod(operationName = "DiagnosticsStatusNotification")
    public Future<?> diagnosticsStatusNotificationAsync(
        @WebParam(partName = "parameters", name = "diagnosticsStatusNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        DiagnosticsStatusNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<DiagnosticsStatusNotificationResponse> asyncHandler
    );

    @WebMethod(operationName = "DiagnosticsStatusNotification", action = "/DiagnosticsStatusNotification")
    @Action(input = "/DiagnosticsStatusNotification", output = "/DiagnosticsStatusNotificationResponse")
    @WebResult(name = "diagnosticsStatusNotificationResponse", targetNamespace = "urn://Ocpp/Cs/2015/10/", partName = "parameters")
    public DiagnosticsStatusNotificationResponse diagnosticsStatusNotification(
        @WebParam(partName = "parameters", name = "diagnosticsStatusNotificationRequest", targetNamespace = "urn://Ocpp/Cs/2015/10/")
        DiagnosticsStatusNotificationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cs/2015/10/", header = true)
        String chargeBoxIdentity
    );
}
