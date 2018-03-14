package de.rwth.idsg.steve.ocpp.ws.ocpp12;

import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ws.TypeStore;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;
import ocpp.cp._2010._08.ChangeAvailabilityResponse;
import ocpp.cp._2010._08.ChangeConfigurationResponse;
import ocpp.cp._2010._08.ClearCacheResponse;
import ocpp.cp._2010._08.GetDiagnosticsResponse;
import ocpp.cp._2010._08.RemoteStartTransactionResponse;
import ocpp.cp._2010._08.RemoteStopTransactionResponse;
import ocpp.cp._2010._08.ResetResponse;
import ocpp.cp._2010._08.UnlockConnectorResponse;
import ocpp.cp._2010._08.UpdateFirmwareResponse;
import ocpp.cs._2010._08.AuthorizeRequest;
import ocpp.cs._2010._08.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2010._08.FirmwareStatusNotificationRequest;
import ocpp.cs._2010._08.HeartbeatRequest;
import ocpp.cs._2010._08.MeterValuesRequest;
import ocpp.cs._2010._08.StartTransactionRequest;
import ocpp.cs._2010._08.StatusNotificationRequest;
import ocpp.cs._2010._08.StopTransactionRequest;
import org.springframework.stereotype.Component;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
public enum Ocpp12TypeStore implements TypeStore {
    INSTANCE;

    @Override
    public Class<? extends RequestType> findRequestClass(String action) {
        switch (action) {
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
            default:
                return null;
        }
    }
}
