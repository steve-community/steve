package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import de.rwth.idsg.steve.ocpp.ws.AbstractTypeStore;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;
import ocpp.cp._2012._06.CancelReservationRequest;
import ocpp.cp._2012._06.CancelReservationResponse;
import ocpp.cp._2012._06.ChangeAvailabilityRequest;
import ocpp.cp._2012._06.ChangeAvailabilityResponse;
import ocpp.cp._2012._06.ChangeConfigurationRequest;
import ocpp.cp._2012._06.ChangeConfigurationResponse;
import ocpp.cp._2012._06.ClearCacheRequest;
import ocpp.cp._2012._06.ClearCacheResponse;
import ocpp.cp._2012._06.DataTransferRequest;
import ocpp.cp._2012._06.DataTransferResponse;
import ocpp.cp._2012._06.GetConfigurationRequest;
import ocpp.cp._2012._06.GetConfigurationResponse;
import ocpp.cp._2012._06.GetDiagnosticsRequest;
import ocpp.cp._2012._06.GetDiagnosticsResponse;
import ocpp.cp._2012._06.GetLocalListVersionRequest;
import ocpp.cp._2012._06.GetLocalListVersionResponse;
import ocpp.cp._2012._06.RemoteStartTransactionRequest;
import ocpp.cp._2012._06.RemoteStartTransactionResponse;
import ocpp.cp._2012._06.RemoteStopTransactionRequest;
import ocpp.cp._2012._06.RemoteStopTransactionResponse;
import ocpp.cp._2012._06.ReserveNowRequest;
import ocpp.cp._2012._06.ReserveNowResponse;
import ocpp.cp._2012._06.ResetRequest;
import ocpp.cp._2012._06.ResetResponse;
import ocpp.cp._2012._06.SendLocalListRequest;
import ocpp.cp._2012._06.SendLocalListResponse;
import ocpp.cp._2012._06.UnlockConnectorRequest;
import ocpp.cp._2012._06.UnlockConnectorResponse;
import ocpp.cp._2012._06.UpdateFirmwareRequest;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.03.2015
 */
@Component
public class Ocpp15TypeStore extends AbstractTypeStore {

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
        requestMap.put("DataTransfer", ocpp.cs._2012._06.DataTransferRequest.class);

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
    }
}
