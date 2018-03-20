package de.rwth.idsg.steve.ocpp.converter;

/**
 * @author Andreas Heuvels <andreas.heuvels@rwth-aachen.de>
 * @since 07.03.18
 */
public interface Server15to16 {

    // -------------------------------------------------------------------------
    // Requests
    // -------------------------------------------------------------------------

    ocpp.cs._2015._10.BootNotificationRequest convertRequest(ocpp.cs._2012._06.BootNotificationRequest request);

    ocpp.cs._2015._10.FirmwareStatusNotificationRequest convertRequest(ocpp.cs._2012._06.FirmwareStatusNotificationRequest request);

    ocpp.cs._2015._10.StatusNotificationRequest convertRequest(ocpp.cs._2012._06.StatusNotificationRequest request);

    ocpp.cs._2015._10.MeterValuesRequest convertRequest(ocpp.cs._2012._06.MeterValuesRequest request);

    ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest convertRequest(ocpp.cs._2012._06.DiagnosticsStatusNotificationRequest request);

    ocpp.cs._2015._10.StartTransactionRequest convertRequest(ocpp.cs._2012._06.StartTransactionRequest request);

    ocpp.cs._2015._10.StopTransactionRequest convertRequest(ocpp.cs._2012._06.StopTransactionRequest request);

    ocpp.cs._2015._10.HeartbeatRequest convertRequest(ocpp.cs._2012._06.HeartbeatRequest request);

    ocpp.cs._2015._10.AuthorizeRequest convertRequest(ocpp.cs._2012._06.AuthorizeRequest request);

    ocpp.cs._2015._10.DataTransferRequest convertRequest(ocpp.cs._2012._06.DataTransferRequest request);

    // -------------------------------------------------------------------------
    // Responses
    // -------------------------------------------------------------------------

    ocpp.cs._2012._06.BootNotificationResponse convertResponse(ocpp.cs._2015._10.BootNotificationResponse response);

    ocpp.cs._2012._06.FirmwareStatusNotificationResponse convertResponse(ocpp.cs._2015._10.FirmwareStatusNotificationResponse response);

    ocpp.cs._2012._06.StatusNotificationResponse convertResponse(ocpp.cs._2015._10.StatusNotificationResponse response);

    ocpp.cs._2012._06.MeterValuesResponse convertResponse(ocpp.cs._2015._10.MeterValuesResponse response);

    ocpp.cs._2012._06.DiagnosticsStatusNotificationResponse convertResponse(ocpp.cs._2015._10.DiagnosticsStatusNotificationResponse response);

    ocpp.cs._2012._06.StartTransactionResponse convertResponse(ocpp.cs._2015._10.StartTransactionResponse response);

    ocpp.cs._2012._06.StopTransactionResponse convertResponse(ocpp.cs._2015._10.StopTransactionResponse response);

    ocpp.cs._2012._06.HeartbeatResponse convertResponse(ocpp.cs._2015._10.HeartbeatResponse response);

    ocpp.cs._2012._06.AuthorizeResponse convertResponse(ocpp.cs._2015._10.AuthorizeResponse response);

    ocpp.cs._2012._06.DataTransferResponse convertResponse(ocpp.cs._2015._10.DataTransferResponse response);
}
