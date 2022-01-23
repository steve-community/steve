/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.ocpp.soap;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.converter.Convert;
import de.rwth.idsg.steve.ocpp.converter.Server15to16Impl;
import de.rwth.idsg.steve.service.CentralSystemService16_Service;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2012._06.AuthorizeRequest;
import ocpp.cs._2012._06.AuthorizeResponse;
import ocpp.cs._2012._06.BootNotificationRequest;
import ocpp.cs._2012._06.BootNotificationResponse;
import ocpp.cs._2012._06.CentralSystemService;
import ocpp.cs._2012._06.DataTransferRequest;
import ocpp.cs._2012._06.DataTransferResponse;
import ocpp.cs._2012._06.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2012._06.DiagnosticsStatusNotificationResponse;
import ocpp.cs._2012._06.FirmwareStatusNotificationRequest;
import ocpp.cs._2012._06.FirmwareStatusNotificationResponse;
import ocpp.cs._2012._06.HeartbeatRequest;
import ocpp.cs._2012._06.HeartbeatResponse;
import ocpp.cs._2012._06.MeterValuesRequest;
import ocpp.cs._2012._06.MeterValuesResponse;
import ocpp.cs._2012._06.StartTransactionRequest;
import ocpp.cs._2012._06.StartTransactionResponse;
import ocpp.cs._2012._06.StatusNotificationRequest;
import ocpp.cs._2012._06.StatusNotificationResponse;
import ocpp.cs._2012._06.StopTransactionRequest;
import ocpp.cs._2012._06.StopTransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingType;
import javax.xml.ws.Response;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.SOAPBinding;
import java.util.concurrent.Future;

/**
 * Service implementation of OCPP V1.5
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 */
@Slf4j
@Service
@Addressing(enabled = true, required = false)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@WebService(
        serviceName = "CentralSystemService",
        portName = "CentralSystemServiceSoap12",
        targetNamespace = "urn://Ocpp/Cs/2012/06/",
        endpointInterface = "ocpp.cs._2012._06.CentralSystemService")
public class CentralSystemService15_SoapServer implements CentralSystemService {

    @Autowired private CentralSystemService16_Service service;

    public BootNotificationResponse bootNotificationWithTransport(BootNotificationRequest parameters,
                                                                  String chargeBoxIdentity, OcppProtocol protocol) {
        if (protocol.getVersion() != OcppVersion.V_15) {
            throw new IllegalArgumentException("Unexpected OCPP version: " + protocol.getVersion());
        }
        return Convert.start(parameters, Server15to16Impl.SINGLETON::convertRequest)
                      .andThen(req -> service.bootNotification(req, chargeBoxIdentity, protocol))
                      .andThen(Server15to16Impl.SINGLETON::convertResponse)
                      .apply(parameters);
    }

    public BootNotificationResponse bootNotification(BootNotificationRequest parameters, String chargeBoxIdentity) {
        return this.bootNotificationWithTransport(parameters, chargeBoxIdentity, OcppProtocol.V_15_SOAP);
    }

    public FirmwareStatusNotificationResponse firmwareStatusNotification(
            FirmwareStatusNotificationRequest parameters, String chargeBoxIdentity) {
        return Convert.start(parameters, Server15to16Impl.SINGLETON::convertRequest)
                      .andThen(req -> service.firmwareStatusNotification(req, chargeBoxIdentity))
                      .andThen(Server15to16Impl.SINGLETON::convertResponse)
                      .apply(parameters);
    }

    public StatusNotificationResponse statusNotification(
            StatusNotificationRequest parameters, String chargeBoxIdentity) {
        return Convert.start(parameters, Server15to16Impl.SINGLETON::convertRequest)
                      .andThen(req -> service.statusNotification(req, chargeBoxIdentity))
                      .andThen(Server15to16Impl.SINGLETON::convertResponse)
                      .apply(parameters);
    }

    public MeterValuesResponse meterValues(MeterValuesRequest parameters, String chargeBoxIdentity) {
        return Convert.start(parameters, Server15to16Impl.SINGLETON::convertRequest)
                      .andThen(req -> service.meterValues(req, chargeBoxIdentity))
                      .andThen(Server15to16Impl.SINGLETON::convertResponse)
                      .apply(parameters);
    }

    public DiagnosticsStatusNotificationResponse diagnosticsStatusNotification(
            DiagnosticsStatusNotificationRequest parameters, String chargeBoxIdentity) {
        return Convert.start(parameters, Server15to16Impl.SINGLETON::convertRequest)
                      .andThen(req -> service.diagnosticsStatusNotification(req, chargeBoxIdentity))
                      .andThen(Server15to16Impl.SINGLETON::convertResponse)
                      .apply(parameters);
    }

    public StartTransactionResponse startTransaction(StartTransactionRequest parameters, String chargeBoxIdentity) {
        return Convert.start(parameters, Server15to16Impl.SINGLETON::convertRequest)
                      .andThen(req -> service.startTransaction(req, chargeBoxIdentity))
                      .andThen(Server15to16Impl.SINGLETON::convertResponse)
                      .apply(parameters);
    }

    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargeBoxIdentity) {
        return Convert.start(parameters, Server15to16Impl.SINGLETON::convertRequest)
                      .andThen(req -> service.stopTransaction(req, chargeBoxIdentity))
                      .andThen(Server15to16Impl.SINGLETON::convertResponse)
                      .apply(parameters);
    }

    public HeartbeatResponse heartbeat(HeartbeatRequest parameters, String chargeBoxIdentity) {
        return Convert.start(parameters, Server15to16Impl.SINGLETON::convertRequest)
                      .andThen(req -> service.heartbeat(req, chargeBoxIdentity))
                      .andThen(Server15to16Impl.SINGLETON::convertResponse)
                      .apply(parameters);
    }

    public AuthorizeResponse authorize(AuthorizeRequest parameters, String chargeBoxIdentity) {
        return Convert.start(parameters, Server15to16Impl.SINGLETON::convertRequest)
                      .andThen(req -> service.authorize(req, chargeBoxIdentity))
                      .andThen(Server15to16Impl.SINGLETON::convertResponse)
                      .apply(parameters);
    }

    public DataTransferResponse dataTransfer(DataTransferRequest parameters, String chargeBoxIdentity) {
        return Convert.start(parameters, Server15to16Impl.SINGLETON::convertRequest)
                      .andThen(req -> service.dataTransfer(req, chargeBoxIdentity))
                      .andThen(Server15to16Impl.SINGLETON::convertResponse)
                      .apply(parameters);
    }

    // -------------------------------------------------------------------------
    // No-op
    // -------------------------------------------------------------------------

    @Override
    public Response<HeartbeatResponse> heartbeatAsync(HeartbeatRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> heartbeatAsync(HeartbeatRequest parameters, String chargeBoxIdentity,
                                    AsyncHandler<HeartbeatResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<StartTransactionResponse> startTransactionAsync(StartTransactionRequest parameters,
                                                                    String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> startTransactionAsync(StartTransactionRequest parameters, String chargeBoxIdentity,
                                           AsyncHandler<StartTransactionResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<StopTransactionResponse> stopTransactionAsync(StopTransactionRequest parameters,
                                                                  String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> stopTransactionAsync(StopTransactionRequest parameters, String chargeBoxIdentity,
                                          AsyncHandler<StopTransactionResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<DiagnosticsStatusNotificationResponse> diagnosticsStatusNotificationAsync(
            DiagnosticsStatusNotificationRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> diagnosticsStatusNotificationAsync(
            DiagnosticsStatusNotificationRequest parameters, String chargeBoxIdentity,
            AsyncHandler<DiagnosticsStatusNotificationResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<AuthorizeResponse> authorizeAsync(AuthorizeRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> authorizeAsync(AuthorizeRequest parameters, String chargeBoxIdentity,
                                    AsyncHandler<AuthorizeResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<BootNotificationResponse> bootNotificationAsync(BootNotificationRequest parameters,
                                                                    String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> bootNotificationAsync(BootNotificationRequest parameters, String chargeBoxIdentity,
                                           AsyncHandler<BootNotificationResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<MeterValuesResponse> meterValuesAsync(MeterValuesRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> meterValuesAsync(MeterValuesRequest parameters, String chargeBoxIdentity,
                                      AsyncHandler<MeterValuesResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<FirmwareStatusNotificationResponse> firmwareStatusNotificationAsync(
            FirmwareStatusNotificationRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> firmwareStatusNotificationAsync(FirmwareStatusNotificationRequest parameters,
                                                     String chargeBoxIdentity,
                                                     AsyncHandler<FirmwareStatusNotificationResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<DataTransferResponse> dataTransferAsync(DataTransferRequest parameters, String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> dataTransferAsync(DataTransferRequest parameters, String chargeBoxIdentity,
                                       AsyncHandler<DataTransferResponse> asyncHandler) {
        return null;
    }

    @Override
    public Response<StatusNotificationResponse> statusNotificationAsync(StatusNotificationRequest parameters,
                                                                        String chargeBoxIdentity) {
        return null;
    }

    @Override
    public Future<?> statusNotificationAsync(StatusNotificationRequest parameters, String chargeBoxIdentity,
                                             AsyncHandler<StatusNotificationResponse> asyncHandler) {
        return null;
    }
}
