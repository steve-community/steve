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
package de.rwth.idsg.steve.ocpp.converter;

import ocpp.cs._2010._08.AuthorizationStatus;
import ocpp.cs._2010._08.AuthorizeResponse;
import ocpp.cs._2010._08.BootNotificationResponse;
import ocpp.cs._2010._08.DiagnosticsStatusNotificationResponse;
import ocpp.cs._2010._08.FirmwareStatusNotificationResponse;
import ocpp.cs._2010._08.HeartbeatResponse;
import ocpp.cs._2010._08.IdTagInfo;
import ocpp.cs._2010._08.MeterValuesResponse;
import ocpp.cs._2010._08.RegistrationStatus;
import ocpp.cs._2010._08.StartTransactionResponse;
import ocpp.cs._2010._08.StatusNotificationResponse;
import ocpp.cs._2010._08.StopTransactionResponse;
import ocpp.cs._2012._06.AuthorizeRequest;
import ocpp.cs._2012._06.BootNotificationRequest;
import ocpp.cs._2012._06.ChargePointErrorCode;
import ocpp.cs._2012._06.ChargePointStatus;
import ocpp.cs._2012._06.DiagnosticsStatus;
import ocpp.cs._2012._06.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2012._06.FirmwareStatus;
import ocpp.cs._2012._06.FirmwareStatusNotificationRequest;
import ocpp.cs._2012._06.HeartbeatRequest;
import ocpp.cs._2012._06.MeterValue;
import ocpp.cs._2012._06.MeterValuesRequest;
import ocpp.cs._2012._06.StartTransactionRequest;
import ocpp.cs._2012._06.StatusNotificationRequest;
import ocpp.cs._2012._06.StopTransactionRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Andreas Heuvels <andreas.heuvels@rwth-aachen.de>
 * @since 07.03.18
 */
public enum Server12to15Impl implements Server12to15 {
    SINGLETON;

    // -------------------------------------------------------------------------
    // Requests
    // -------------------------------------------------------------------------

    @Override
    public BootNotificationRequest convertRequest(ocpp.cs._2010._08.BootNotificationRequest request) {
        return new BootNotificationRequest()
                .withChargePointVendor(request.getChargePointVendor())
                .withChargePointModel(request.getChargePointModel())
                .withChargePointSerialNumber(request.getChargePointSerialNumber())
                .withChargeBoxSerialNumber(request.getChargeBoxSerialNumber())
                .withFirmwareVersion(request.getFirmwareVersion())
                .withIccid(request.getIccid())
                .withImsi(request.getImsi())
                .withMeterType(request.getMeterType())
                .withMeterSerialNumber(request.getMeterSerialNumber());
    }

    @Override
    public FirmwareStatusNotificationRequest convertRequest(ocpp.cs._2010._08.FirmwareStatusNotificationRequest request) {
        return new FirmwareStatusNotificationRequest()
                .withStatus(FirmwareStatus.fromValue(request.getStatus().value()));
    }

    @Override
    public StatusNotificationRequest convertRequest(ocpp.cs._2010._08.StatusNotificationRequest request) {
        return new StatusNotificationRequest()
                .withConnectorId(request.getConnectorId())
                .withStatus(ChargePointStatus.fromValue(request.getStatus().value()))
                .withErrorCode(ChargePointErrorCode.fromValue(request.getErrorCode().value()));
    }

    @Override
    public MeterValuesRequest convertRequest(ocpp.cs._2010._08.MeterValuesRequest request) {
        List<MeterValue> values15 =
                request.getValues()
                       .stream()
                       .map(e -> new MeterValue().withTimestamp(e.getTimestamp())
                                                 .withValue(new MeterValue.Value().withValue(Integer.toString(e.getValue()))))
                       .collect(Collectors.toList());

        return new MeterValuesRequest()
                .withConnectorId(request.getConnectorId())
                .withValues(values15);
    }

    @Override
    public DiagnosticsStatusNotificationRequest convertRequest(ocpp.cs._2010._08.DiagnosticsStatusNotificationRequest request) {
        return new DiagnosticsStatusNotificationRequest()
                .withStatus(DiagnosticsStatus.fromValue(request.getStatus().value()));
    }

    @Override
    public StartTransactionRequest convertRequest(ocpp.cs._2010._08.StartTransactionRequest request) {
        return new StartTransactionRequest()
                .withConnectorId(request.getConnectorId())
                .withIdTag(request.getIdTag())
                .withMeterStart(request.getMeterStart())
                .withTimestamp(request.getTimestamp());
    }

    @Override
    public StopTransactionRequest convertRequest(ocpp.cs._2010._08.StopTransactionRequest request) {
        return new StopTransactionRequest()
                .withIdTag(request.getIdTag())
                .withMeterStop(request.getMeterStop())
                .withTimestamp(request.getTimestamp())
                .withTransactionId(request.getTransactionId());
    }

    @Override
    public HeartbeatRequest convertRequest(ocpp.cs._2010._08.HeartbeatRequest request) {
        return new HeartbeatRequest();
    }

    @Override
    public AuthorizeRequest convertRequest(ocpp.cs._2010._08.AuthorizeRequest request) {
        return new AuthorizeRequest()
                .withIdTag(request.getIdTag());
    }

    // -------------------------------------------------------------------------
    // Responses
    // -------------------------------------------------------------------------

    @Override
    public BootNotificationResponse convertResponse(ocpp.cs._2012._06.BootNotificationResponse response) {
        return new BootNotificationResponse()
                .withCurrentTime(response.getCurrentTime())
                .withHeartbeatInterval(response.getHeartbeatInterval())
                .withStatus(RegistrationStatus.fromValue(response.getStatus().value()));
    }

    @Override
    public FirmwareStatusNotificationResponse convertResponse(ocpp.cs._2012._06.FirmwareStatusNotificationResponse response) {
        return new FirmwareStatusNotificationResponse();
    }

    @Override
    public StatusNotificationResponse convertResponse(ocpp.cs._2012._06.StatusNotificationResponse response) {
        return new StatusNotificationResponse();
    }

    @Override
    public MeterValuesResponse convertResponse(ocpp.cs._2012._06.MeterValuesResponse response) {
        return new MeterValuesResponse();
    }

    @Override
    public DiagnosticsStatusNotificationResponse convertResponse(ocpp.cs._2012._06.DiagnosticsStatusNotificationResponse response) {
        return new DiagnosticsStatusNotificationResponse();
    }

    @Override
    public StartTransactionResponse convertResponse(ocpp.cs._2012._06.StartTransactionResponse response) {
        return new StartTransactionResponse()
                .withIdTagInfo(toOcpp12TagInfo(response.getIdTagInfo()))
                .withTransactionId(response.getTransactionId());
    }

    @Override
    public StopTransactionResponse convertResponse(ocpp.cs._2012._06.StopTransactionResponse response) {
        return new StopTransactionResponse()
                .withIdTagInfo(toOcpp12TagInfo(response.getIdTagInfo()));
    }

    @Override
    public HeartbeatResponse convertResponse(ocpp.cs._2012._06.HeartbeatResponse response) {
        return new HeartbeatResponse()
                .withCurrentTime(response.getCurrentTime());
    }

    @Override
    public AuthorizeResponse convertResponse(ocpp.cs._2012._06.AuthorizeResponse response) {
        return new AuthorizeResponse()
                .withIdTagInfo(toOcpp12TagInfo(response.getIdTagInfo()));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static IdTagInfo toOcpp12TagInfo(ocpp.cs._2012._06.IdTagInfo info15) {
        if (info15 == null) {
            return null;
        }
        return new IdTagInfo()
                .withExpiryDate(info15.getExpiryDate())
                .withParentIdTag(info15.getParentIdTag())
                .withStatus(AuthorizationStatus.fromValue(info15.getStatus().value()));
    }
}
