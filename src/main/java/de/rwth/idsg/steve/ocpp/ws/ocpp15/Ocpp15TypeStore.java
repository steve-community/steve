package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ws.TypeStore;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;
import ocpp.cp._2012._06.CancelReservationResponse;
import ocpp.cp._2012._06.ChangeAvailabilityResponse;
import ocpp.cp._2012._06.ChangeConfigurationResponse;
import ocpp.cp._2012._06.ClearCacheResponse;
import ocpp.cp._2012._06.DataTransferResponse;
import ocpp.cp._2012._06.GetConfigurationResponse;
import ocpp.cp._2012._06.GetDiagnosticsResponse;
import ocpp.cp._2012._06.GetLocalListVersionResponse;
import ocpp.cp._2012._06.RemoteStartTransactionResponse;
import ocpp.cp._2012._06.RemoteStopTransactionResponse;
import ocpp.cp._2012._06.ReserveNowResponse;
import ocpp.cp._2012._06.ResetResponse;
import ocpp.cp._2012._06.SendLocalListResponse;
import ocpp.cp._2012._06.UnlockConnectorResponse;
import ocpp.cp._2012._06.UpdateFirmwareResponse;
import ocpp.cs._2012._06.AuthorizeRequest;
import ocpp.cs._2012._06.BootNotificationRequest;
import ocpp.cs._2012._06.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2012._06.FirmwareStatusNotificationRequest;
import ocpp.cs._2012._06.HeartbeatRequest;
import ocpp.cs._2012._06.MeterValuesRequest;
import ocpp.cs._2012._06.StartTransactionRequest;
import ocpp.cs._2012._06.StatusNotificationRequest;
import ocpp.cs._2012._06.StopTransactionRequest;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.03.2015
 */
public enum Ocpp15TypeStore implements TypeStore {
    INSTANCE;

    @Override
    public Class<? extends RequestType> findRequestClass(String action) {
        switch (action) {
            case "BootNotification":
                return BootNotificationRequest.class;
            case "FirmwareStatusNotification":
                return FirmwareStatusNotificationRequest.class;
            case "StatusNotification":
                return StatusNotificationRequest.class;
            case "MeterValues":
                return MeterValuesRequest.class;
            case "DiagnosticsStatusNotification":
                return DiagnosticsStatusNotificationRequest.class;
            case "StartTransaction":
                return StartTransactionRequest.class;
            case "StopTransaction":
                return StopTransactionRequest.class;
            case "Heartbeat":
                return HeartbeatRequest.class;
            case "Authorize":
                return AuthorizeRequest.class;
            case "DataTransfer":
                return ocpp.cs._2012._06.DataTransferRequest.class;
            default:
                return null;
        }
    }

    @Override
    public <T extends RequestType> ActionResponsePair findActionResponse(T requestPayload) {
        switch (requestPayload.getClass().getSimpleName()) {
            case "UnlockConnectorRequest":
                return new ActionResponsePair("UnlockConnector", UnlockConnectorResponse.class);
            case "ResetRequest":
                return new ActionResponsePair("Reset", ResetResponse.class);
            case "ChangeAvailabilityRequest":
                return new ActionResponsePair("ChangeAvailability", ChangeAvailabilityResponse.class);
            case "GetDiagnosticsRequest":
                return new ActionResponsePair("GetDiagnostics", GetDiagnosticsResponse.class);
            case "ClearCacheRequest":
                return new ActionResponsePair("ClearCache", ClearCacheResponse.class);
            case "UpdateFirmwareRequest":
                return new ActionResponsePair("UpdateFirmware", UpdateFirmwareResponse.class);
            case "ChangeConfigurationRequest":
                return new ActionResponsePair("ChangeConfiguration", ChangeConfigurationResponse.class);
            case "RemoteStartTransactionRequest":
                return new ActionResponsePair("RemoteStartTransaction", RemoteStartTransactionResponse.class);
            case "RemoteStopTransactionRequest":
                return new ActionResponsePair("RemoteStopTransaction", RemoteStopTransactionResponse.class);

            // new in ocpp 1.5
            case "CancelReservationRequest":
                return new ActionResponsePair("CancelReservation", CancelReservationResponse.class);
            case "DataTransferRequest":
                return new ActionResponsePair("DataTransfer", DataTransferResponse.class);
            case "GetConfigurationRequest":
                return new ActionResponsePair("GetConfiguration", GetConfigurationResponse.class);
            case "GetLocalListVersionRequest":
                return new ActionResponsePair("GetLocalListVersion", GetLocalListVersionResponse.class);
            case "ReserveNowRequest":
                return new ActionResponsePair("ReserveNow", ReserveNowResponse.class);
            case "SendLocalListRequest":
                return new ActionResponsePair("SendLocalList", SendLocalListResponse.class);
            default:
                return null;
        }
    }

}
