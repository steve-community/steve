package de.rwth.idsg.steve.ocpp.ws.ocpp16;

import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ws.TypeStore;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;
import ocpp.cp._2015._10.CancelReservationResponse;
import ocpp.cp._2015._10.ChangeAvailabilityResponse;
import ocpp.cp._2015._10.ChangeConfigurationResponse;
import ocpp.cp._2015._10.ClearCacheResponse;
import ocpp.cp._2015._10.ClearChargingProfileResponse;
import ocpp.cp._2015._10.DataTransferResponse;
import ocpp.cp._2015._10.GetCompositeScheduleResponse;
import ocpp.cp._2015._10.GetConfigurationResponse;
import ocpp.cp._2015._10.GetDiagnosticsResponse;
import ocpp.cp._2015._10.GetLocalListVersionResponse;
import ocpp.cp._2015._10.RemoteStartTransactionResponse;
import ocpp.cp._2015._10.RemoteStopTransactionResponse;
import ocpp.cp._2015._10.ReserveNowResponse;
import ocpp.cp._2015._10.ResetResponse;
import ocpp.cp._2015._10.SendLocalListResponse;
import ocpp.cp._2015._10.SetChargingProfileResponse;
import ocpp.cp._2015._10.TriggerMessageResponse;
import ocpp.cp._2015._10.UnlockConnectorResponse;
import ocpp.cp._2015._10.UpdateFirmwareResponse;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.DataTransferRequest;
import ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2015._10.FirmwareStatusNotificationRequest;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StopTransactionRequest;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
public enum Ocpp16TypeStore implements TypeStore {
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
                return DataTransferRequest.class;
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

            // new in ocpp 1.6
            case "ClearChargingProfileRequest":
                return new ActionResponsePair("ClearChargingProfile", ClearChargingProfileResponse.class);
            case "GetCompositeScheduleRequest":
                return new ActionResponsePair("GetCompositeSchedule", GetCompositeScheduleResponse.class);
            case "SetChargingProfileRequest":
                return new ActionResponsePair("SetChargingProfile", SetChargingProfileResponse.class);
            case "TriggerMessageRequest":
                return new ActionResponsePair("TriggerMessage", TriggerMessageResponse.class);
            default:
                return null;
        }
    }
}
