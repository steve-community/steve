package de.rwth.idsg.steve.ocpp.converter;

import ocpp.cs._2012._06.AuthorizeResponse;
import ocpp.cs._2012._06.BootNotificationResponse;
import ocpp.cs._2012._06.DataTransferResponse;
import ocpp.cs._2012._06.DiagnosticsStatusNotificationResponse;
import ocpp.cs._2012._06.FirmwareStatusNotificationResponse;
import ocpp.cs._2012._06.HeartbeatResponse;
import ocpp.cs._2012._06.MeterValuesResponse;
import ocpp.cs._2012._06.StartTransactionResponse;
import ocpp.cs._2012._06.StatusNotificationResponse;
import ocpp.cs._2012._06.StopTransactionResponse;
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
public enum Server15to16Impl implements Server15to16 {
    SINGLETON;

    // -------------------------------------------------------------------------
    // Requests
    // -------------------------------------------------------------------------

    @Override
    public BootNotificationRequest convertRequest(ocpp.cs._2012._06.BootNotificationRequest request) {
        return null;
    }

    @Override
    public FirmwareStatusNotificationRequest convertRequest(
            ocpp.cs._2012._06.FirmwareStatusNotificationRequest request) {
        return null;
    }

    @Override
    public StatusNotificationRequest convertRequest(ocpp.cs._2012._06.StatusNotificationRequest request) {
        return null;
    }

    @Override
    public MeterValuesRequest convertRequest(ocpp.cs._2012._06.MeterValuesRequest request) {
        return null;
    }

    @Override
    public DiagnosticsStatusNotificationRequest convertRequest(
            ocpp.cs._2012._06.DiagnosticsStatusNotificationRequest request) {
        return null;
    }

    @Override
    public StartTransactionRequest convertRequest(ocpp.cs._2012._06.StartTransactionRequest request) {
        return null;
    }

    @Override
    public StopTransactionRequest convertRequest(ocpp.cs._2012._06.StopTransactionRequest request) {
        return null;
    }

    @Override
    public HeartbeatRequest convertRequest(ocpp.cs._2012._06.HeartbeatRequest request) {
        return null;
    }

    @Override
    public AuthorizeRequest convertRequest(ocpp.cs._2012._06.AuthorizeRequest request) {
        return null;
    }

    @Override
    public DataTransferRequest convertRequest(ocpp.cs._2012._06.DataTransferRequest request) {
        return null;
    }

    // -------------------------------------------------------------------------
    // Responses
    // -------------------------------------------------------------------------

    @Override
    public BootNotificationResponse convertResponse(ocpp.cs._2015._10.BootNotificationResponse response) {
        return null;
    }

    @Override
    public FirmwareStatusNotificationResponse convertResponse(
            ocpp.cs._2015._10.FirmwareStatusNotificationResponse response) {
        return null;
    }

    @Override
    public StatusNotificationResponse convertResponse(ocpp.cs._2015._10.StatusNotificationResponse response) {
        return null;
    }

    @Override
    public MeterValuesResponse convertResponse(ocpp.cs._2015._10.MeterValuesResponse response) {
        return null;
    }

    @Override
    public DiagnosticsStatusNotificationResponse convertResponse(
            ocpp.cs._2015._10.DiagnosticsStatusNotificationResponse response) {
        return null;
    }

    @Override
    public StartTransactionResponse convertResponse(ocpp.cs._2015._10.StartTransactionResponse response) {
        return null;
    }

    @Override
    public StopTransactionResponse convertResponse(ocpp.cs._2015._10.StopTransactionResponse response) {
        return null;
    }

    @Override
    public HeartbeatResponse convertResponse(ocpp.cs._2015._10.HeartbeatResponse response) {
        return null;
    }

    @Override
    public AuthorizeResponse convertResponse(ocpp.cs._2015._10.AuthorizeResponse response) {
        return null;
    }

    @Override
    public DataTransferResponse convertResponse(ocpp.cs._2015._10.DataTransferResponse response) {
        return null;
    }
}
