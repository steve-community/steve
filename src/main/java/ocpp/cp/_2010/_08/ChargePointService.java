package ocpp.cp._2010._08;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.Action;
import jakarta.xml.ws.AsyncHandler;
import jakarta.xml.ws.Response;
import java.util.concurrent.Future;

/**
 * This class was generated by Apache CXF 4.0.5
 * 2024-09-28T11:14:44.921+02:00
 * Generated source version: 4.0.5
 *
 */
@WebService(targetNamespace = "urn://Ocpp/Cp/2010/08/", name = "ChargePointService")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ChargePointService {

    @WebMethod(operationName = "GetDiagnostics")
    public Response<ocpp.cp._2010._08.GetDiagnosticsResponse> getDiagnosticsAsync(

        @WebParam(partName = "parameters", name = "getDiagnosticsRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        GetDiagnosticsRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "GetDiagnostics")
    public Future<?> getDiagnosticsAsync(

        @WebParam(partName = "parameters", name = "getDiagnosticsRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        GetDiagnosticsRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<ocpp.cp._2010._08.GetDiagnosticsResponse> asyncHandler
    );

    @WebMethod(operationName = "GetDiagnostics", action = "/GetDiagnostics")
    @Action(input = "/GetDiagnostics")
    @WebResult(name = "getDiagnosticsResponse", targetNamespace = "urn://Ocpp/Cp/2010/08/", partName = "parameters")
    public GetDiagnosticsResponse getDiagnostics(

        @WebParam(partName = "parameters", name = "getDiagnosticsRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        GetDiagnosticsRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "ChangeAvailability")
    public Response<ocpp.cp._2010._08.ChangeAvailabilityResponse> changeAvailabilityAsync(

        @WebParam(partName = "parameters", name = "changeAvailabilityRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ChangeAvailabilityRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "ChangeAvailability")
    public Future<?> changeAvailabilityAsync(

        @WebParam(partName = "parameters", name = "changeAvailabilityRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ChangeAvailabilityRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<ocpp.cp._2010._08.ChangeAvailabilityResponse> asyncHandler
    );

    @WebMethod(operationName = "ChangeAvailability", action = "/ChangeAvailability")
    @Action(input = "/ChangeAvailability")
    @WebResult(name = "changeAvailabilityResponse", targetNamespace = "urn://Ocpp/Cp/2010/08/", partName = "parameters")
    public ChangeAvailabilityResponse changeAvailability(

        @WebParam(partName = "parameters", name = "changeAvailabilityRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ChangeAvailabilityRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "Reset")
    public Response<ocpp.cp._2010._08.ResetResponse> resetAsync(

        @WebParam(partName = "parameters", name = "resetRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ResetRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "Reset")
    public Future<?> resetAsync(

        @WebParam(partName = "parameters", name = "resetRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ResetRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<ocpp.cp._2010._08.ResetResponse> asyncHandler
    );

    @WebMethod(operationName = "Reset", action = "/Reset")
    @Action(input = "/Reset")
    @WebResult(name = "resetResponse", targetNamespace = "urn://Ocpp/Cp/2010/08/", partName = "parameters")
    public ResetResponse reset(

        @WebParam(partName = "parameters", name = "resetRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ResetRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "UpdateFirmware")
    public Response<ocpp.cp._2010._08.UpdateFirmwareResponse> updateFirmwareAsync(

        @WebParam(partName = "parameters", name = "updateFirmwareRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        UpdateFirmwareRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "UpdateFirmware")
    public Future<?> updateFirmwareAsync(

        @WebParam(partName = "parameters", name = "updateFirmwareRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        UpdateFirmwareRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<ocpp.cp._2010._08.UpdateFirmwareResponse> asyncHandler
    );

    @WebMethod(operationName = "UpdateFirmware", action = "/UpdateFirmware")
    @Action(input = "/UpdateFirmware")
    @WebResult(name = "updateFirmwareResponse", targetNamespace = "urn://Ocpp/Cp/2010/08/", partName = "parameters")
    public UpdateFirmwareResponse updateFirmware(

        @WebParam(partName = "parameters", name = "updateFirmwareRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        UpdateFirmwareRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "RemoteStopTransaction")
    public Response<ocpp.cp._2010._08.RemoteStopTransactionResponse> remoteStopTransactionAsync(

        @WebParam(partName = "parameters", name = "remoteStopTransactionRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        RemoteStopTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "RemoteStopTransaction")
    public Future<?> remoteStopTransactionAsync(

        @WebParam(partName = "parameters", name = "remoteStopTransactionRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        RemoteStopTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<ocpp.cp._2010._08.RemoteStopTransactionResponse> asyncHandler
    );

    @WebMethod(operationName = "RemoteStopTransaction", action = "/RemoteStopTransaction")
    @Action(input = "/RemoteStopTransaction")
    @WebResult(name = "remoteStopTransactionResponse", targetNamespace = "urn://Ocpp/Cp/2010/08/", partName = "parameters")
    public RemoteStopTransactionResponse remoteStopTransaction(

        @WebParam(partName = "parameters", name = "remoteStopTransactionRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        RemoteStopTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "RemoteStartTransaction")
    public Response<ocpp.cp._2010._08.RemoteStartTransactionResponse> remoteStartTransactionAsync(

        @WebParam(partName = "parameters", name = "remoteStartTransactionRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        RemoteStartTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "RemoteStartTransaction")
    public Future<?> remoteStartTransactionAsync(

        @WebParam(partName = "parameters", name = "remoteStartTransactionRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        RemoteStartTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<ocpp.cp._2010._08.RemoteStartTransactionResponse> asyncHandler
    );

    @WebMethod(operationName = "RemoteStartTransaction", action = "/RemoteStartTransaction")
    @Action(input = "/RemoteStartTransaction")
    @WebResult(name = "remoteStartTransactionResponse", targetNamespace = "urn://Ocpp/Cp/2010/08/", partName = "parameters")
    public RemoteStartTransactionResponse remoteStartTransaction(

        @WebParam(partName = "parameters", name = "remoteStartTransactionRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        RemoteStartTransactionRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "UnlockConnector")
    public Response<ocpp.cp._2010._08.UnlockConnectorResponse> unlockConnectorAsync(

        @WebParam(partName = "parameters", name = "unlockConnectorRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        UnlockConnectorRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "UnlockConnector")
    public Future<?> unlockConnectorAsync(

        @WebParam(partName = "parameters", name = "unlockConnectorRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        UnlockConnectorRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<ocpp.cp._2010._08.UnlockConnectorResponse> asyncHandler
    );

    @WebMethod(operationName = "UnlockConnector", action = "/UnlockConnector")
    @Action(input = "/UnlockConnector")
    @WebResult(name = "unlockConnectorResponse", targetNamespace = "urn://Ocpp/Cp/2010/08/", partName = "parameters")
    public UnlockConnectorResponse unlockConnector(

        @WebParam(partName = "parameters", name = "unlockConnectorRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        UnlockConnectorRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "ClearCache")
    public Response<ocpp.cp._2010._08.ClearCacheResponse> clearCacheAsync(

        @WebParam(partName = "parameters", name = "clearCacheRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ClearCacheRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "ClearCache")
    public Future<?> clearCacheAsync(

        @WebParam(partName = "parameters", name = "clearCacheRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ClearCacheRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<ocpp.cp._2010._08.ClearCacheResponse> asyncHandler
    );

    @WebMethod(operationName = "ClearCache", action = "/ClearCache")
    @Action(input = "/ClearCache")
    @WebResult(name = "clearCacheResponse", targetNamespace = "urn://Ocpp/Cp/2010/08/", partName = "parameters")
    public ClearCacheResponse clearCache(

        @WebParam(partName = "parameters", name = "clearCacheRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ClearCacheRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "ChangeConfiguration")
    public Response<ocpp.cp._2010._08.ChangeConfigurationResponse> changeConfigurationAsync(

        @WebParam(partName = "parameters", name = "changeConfigurationRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ChangeConfigurationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );

    @WebMethod(operationName = "ChangeConfiguration")
    public Future<?> changeConfigurationAsync(

        @WebParam(partName = "parameters", name = "changeConfigurationRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ChangeConfigurationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<ocpp.cp._2010._08.ChangeConfigurationResponse> asyncHandler
    );

    @WebMethod(operationName = "ChangeConfiguration", action = "/ChangeConfiguration")
    @Action(input = "/ChangeConfiguration")
    @WebResult(name = "changeConfigurationResponse", targetNamespace = "urn://Ocpp/Cp/2010/08/", partName = "parameters")
    public ChangeConfigurationResponse changeConfiguration(

        @WebParam(partName = "parameters", name = "changeConfigurationRequest", targetNamespace = "urn://Ocpp/Cp/2010/08/")
        ChangeConfigurationRequest parameters,
        @WebParam(partName = "ChargeBoxIdentity", name = "chargeBoxIdentity", targetNamespace = "urn://Ocpp/Cp/2010/08/", header = true)
        java.lang.String chargeBoxIdentity
    );
}
