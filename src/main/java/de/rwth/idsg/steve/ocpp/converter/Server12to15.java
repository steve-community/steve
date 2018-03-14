package de.rwth.idsg.steve.ocpp.converter;

/**
 * @author Andreas Heuvels <andreas.heuvels@rwth-aachen.de>
 * @since 07.03.18
 */
public interface Server12to15 {

    // -------------------------------------------------------------------------
    // Requests
    // -------------------------------------------------------------------------

    ocpp.cs._2012._06.BootNotificationRequest convertRequest(ocpp.cs._2010._08.BootNotificationRequest request);

    ocpp.cs._2012._06.FirmwareStatusNotificationRequest convertRequest(ocpp.cs._2010._08.FirmwareStatusNotificationRequest request);

    ocpp.cs._2012._06.StatusNotificationRequest convertRequest(ocpp.cs._2010._08.StatusNotificationRequest request);

    ocpp.cs._2012._06.MeterValuesRequest convertRequest(ocpp.cs._2010._08.MeterValuesRequest request);

    ocpp.cs._2012._06.DiagnosticsStatusNotificationRequest convertRequest(ocpp.cs._2010._08.DiagnosticsStatusNotificationRequest request);

    ocpp.cs._2012._06.StartTransactionRequest convertRequest(ocpp.cs._2010._08.StartTransactionRequest request);

    ocpp.cs._2012._06.StopTransactionRequest convertRequest(ocpp.cs._2010._08.StopTransactionRequest request);

    ocpp.cs._2012._06.HeartbeatRequest convertRequest(ocpp.cs._2010._08.HeartbeatRequest request);

    ocpp.cs._2012._06.AuthorizeRequest convertRequest(ocpp.cs._2010._08.AuthorizeRequest request);

    // -------------------------------------------------------------------------
    // Responses
    // -------------------------------------------------------------------------

    ocpp.cs._2010._08.BootNotificationResponse convertResponse(ocpp.cs._2012._06.BootNotificationResponse response);

    ocpp.cs._2010._08.FirmwareStatusNotificationResponse convertResponse(ocpp.cs._2012._06.FirmwareStatusNotificationResponse response);

    ocpp.cs._2010._08.StatusNotificationResponse convertResponse(ocpp.cs._2012._06.StatusNotificationResponse response);

    ocpp.cs._2010._08.MeterValuesResponse convertResponse(ocpp.cs._2012._06.MeterValuesResponse response);

    ocpp.cs._2010._08.DiagnosticsStatusNotificationResponse convertResponse(ocpp.cs._2012._06.DiagnosticsStatusNotificationResponse response);

    ocpp.cs._2010._08.StartTransactionResponse convertResponse(ocpp.cs._2012._06.StartTransactionResponse response);

    ocpp.cs._2010._08.StopTransactionResponse convertResponse(ocpp.cs._2012._06.StopTransactionResponse response);

    ocpp.cs._2010._08.HeartbeatResponse convertResponse(ocpp.cs._2012._06.HeartbeatResponse response);

    ocpp.cs._2010._08.AuthorizeResponse convertResponse(ocpp.cs._2012._06.AuthorizeResponse response);
}
