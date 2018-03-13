/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.ocpp.ws.ocpp16;

import de.rwth.idsg.steve.ocpp.ws.AbstractTypeStore;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;
import ocpp.cp._2015._10.*;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2015._10.FirmwareStatusNotificationRequest;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StopTransactionRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author david
 */

@Component
public class Ocpp16TypeStore extends AbstractTypeStore
{
    @PostConstruct
    public void init() {
        // For incoming requests
        requestMap.put("BootNotification", BootNotificationRequest.class);
        requestMap.put("FirmwareStatusNotification", FirmwareStatusNotificationRequest.class);
        requestMap.put("StatusNotification", StatusNotificationRequest.class);
        requestMap.put("MeterValues", MeterValuesRequest.class);
        requestMap.put("DiagnosticsStatusNotification", DiagnosticsStatusNotificationRequest.class);
        requestMap.put("StartTransaction", StartTransactionRequest.class);
        requestMap.put("StopTransaction", StopTransactionRequest.class);
        requestMap.put("Heartbeat", HeartbeatRequest.class);
        requestMap.put("Authorize", AuthorizeRequest.class);
        requestMap.put("DataTransfer", ocpp.cs._2015._10.DataTransferRequest.class);

        // For outgoing requests
        actionResponseMap.put(UnlockConnectorRequest.class,
                new ActionResponsePair("UnlockConnector", UnlockConnectorResponse.class));
        actionResponseMap.put(ResetRequest.class,
                new ActionResponsePair("Reset", ResetResponse.class));
        actionResponseMap.put(ChangeAvailabilityRequest.class,
                new ActionResponsePair("ChangeAvailability", ChangeAvailabilityResponse.class));
        actionResponseMap.put(GetDiagnosticsRequest.class,
                new ActionResponsePair("GetDiagnostics", GetDiagnosticsResponse.class));
        actionResponseMap.put(ClearCacheRequest.class,
                new ActionResponsePair("ClearCache", ClearCacheResponse.class));
        actionResponseMap.put(UpdateFirmwareRequest.class,
                new ActionResponsePair("UpdateFirmware", UpdateFirmwareResponse.class));
        actionResponseMap.put(ChangeConfigurationRequest.class,
                new ActionResponsePair("ChangeConfiguration", ChangeConfigurationResponse.class));
        actionResponseMap.put(RemoteStartTransactionRequest.class,
                new ActionResponsePair("RemoteStartTransaction", RemoteStartTransactionResponse.class));
        actionResponseMap.put(RemoteStopTransactionRequest.class,
                new ActionResponsePair("RemoteStopTransaction", RemoteStopTransactionResponse.class));
        actionResponseMap.put(CancelReservationRequest.class,
                new ActionResponsePair("CancelReservation", CancelReservationResponse.class));
        actionResponseMap.put(DataTransferRequest.class,
                new ActionResponsePair("DataTransfer", DataTransferResponse.class));
        actionResponseMap.put(GetConfigurationRequest.class,
                new ActionResponsePair("GetConfiguration", GetConfigurationResponse.class));
        actionResponseMap.put(GetLocalListVersionRequest.class,
                new ActionResponsePair("GetLocalListVersion", GetLocalListVersionResponse.class));
        actionResponseMap.put(ReserveNowRequest.class,
                new ActionResponsePair("ReserveNow", ReserveNowResponse.class));
        actionResponseMap.put(SendLocalListRequest.class,
                new ActionResponsePair("SendLocalList", SendLocalListResponse.class));
        actionResponseMap.put(TriggerMessageRequest.class,
                new ActionResponsePair("TriggerMessage", TriggerMessageResponse.class));
        actionResponseMap.put(SetChargingProfileRequest.class,
                new ActionResponsePair("SetChargingProfile", SetChargingProfileResponse.class));
        actionResponseMap.put(GetCompositeScheduleRequest.class,
                new ActionResponsePair("GetCompositeSchedule", GetCompositeScheduleResponse.class));
        actionResponseMap.put(ClearChargingProfileRequest.class,
                new ActionResponsePair("ClearChargingProfile", ClearChargingProfileResponse.class));
    }
}