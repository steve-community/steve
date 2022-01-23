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

import ocpp.cs._2012._06.AuthorizationStatus;
import ocpp.cs._2012._06.AuthorizeResponse;
import ocpp.cs._2012._06.BootNotificationResponse;
import ocpp.cs._2012._06.DataTransferResponse;
import ocpp.cs._2012._06.DataTransferStatus;
import ocpp.cs._2012._06.DiagnosticsStatusNotificationResponse;
import ocpp.cs._2012._06.FirmwareStatusNotificationResponse;
import ocpp.cs._2012._06.HeartbeatResponse;
import ocpp.cs._2012._06.IdTagInfo;
import ocpp.cs._2012._06.MeterValuesResponse;
import ocpp.cs._2012._06.RegistrationStatus;
import ocpp.cs._2012._06.StartTransactionResponse;
import ocpp.cs._2012._06.StatusNotificationResponse;
import ocpp.cs._2012._06.StopTransactionResponse;
import ocpp.cs._2012._06.TransactionData;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.DataTransferRequest;
import ocpp.cs._2015._10.DiagnosticsStatus;
import ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2015._10.FirmwareStatus;
import ocpp.cs._2015._10.FirmwareStatusNotificationRequest;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.Location;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.ReadingContext;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.UnitOfMeasure;
import ocpp.cs._2015._10.ValueFormat;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 13.03.2018
 */
public enum Server15to16Impl implements Server15to16 {
    SINGLETON;

    // -------------------------------------------------------------------------
    // Requests
    // -------------------------------------------------------------------------

    @Override
    public BootNotificationRequest convertRequest(ocpp.cs._2012._06.BootNotificationRequest request) {
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
    public FirmwareStatusNotificationRequest convertRequest(
            ocpp.cs._2012._06.FirmwareStatusNotificationRequest request) {
        return new FirmwareStatusNotificationRequest()
                .withStatus(FirmwareStatus.fromValue(request.getStatus().value()));
    }

    /**
     * New logic: Connector with Id 0 can only be AVAILABLE, UNAVAILABLE and FAULTED. But we do not handle this, since
     * this semantic change does not require a custom mapping.
     */
    @Override
    public StatusNotificationRequest convertRequest(ocpp.cs._2012._06.StatusNotificationRequest request) {
        return new StatusNotificationRequest()
                .withConnectorId(request.getConnectorId())
                .withStatus(customMapStatus(request.getStatus()))
                .withErrorCode(customMapErrorCode(request.getErrorCode()))
                .withInfo(request.getInfo())
                .withTimestamp(request.getTimestamp())
                .withVendorErrorCode(request.getVendorErrorCode())
                .withVendorId(request.getVendorId());
    }

    @Override
    public MeterValuesRequest convertRequest(ocpp.cs._2012._06.MeterValuesRequest request) {
        List<MeterValue> values16 = request.getValues()
                                           .stream()
                                           .map(Server15to16Impl::toOcpp16MeterValue)
                                           .collect(Collectors.toList());

        return new MeterValuesRequest()
                .withTransactionId(request.getTransactionId())
                .withConnectorId(request.getConnectorId())
                .withMeterValue(values16);
    }

    @Override
    public DiagnosticsStatusNotificationRequest convertRequest(
            ocpp.cs._2012._06.DiagnosticsStatusNotificationRequest request) {
        return new DiagnosticsStatusNotificationRequest()
                .withStatus(DiagnosticsStatus.fromValue(request.getStatus().value()));
    }

    @Override
    public StartTransactionRequest convertRequest(ocpp.cs._2012._06.StartTransactionRequest request) {
        return new StartTransactionRequest()
                .withConnectorId(request.getConnectorId())
                .withIdTag(request.getIdTag())
                .withMeterStart(request.getMeterStart())
                .withTimestamp(request.getTimestamp())
                .withReservationId(request.getReservationId());
    }

    /**
     * Reason was introduced with 1.6 and is optional (no mapping needed)
     */
    @Override
    public StopTransactionRequest convertRequest(ocpp.cs._2012._06.StopTransactionRequest request) {
        return new StopTransactionRequest()
                .withIdTag(request.getIdTag())
                .withMeterStop(request.getMeterStop())
                .withTimestamp(request.getTimestamp())
                .withTransactionId(request.getTransactionId())
                .withTransactionData(toOcpp16TransactionData(request.getTransactionData()));
    }

    @Override
    public HeartbeatRequest convertRequest(ocpp.cs._2012._06.HeartbeatRequest request) {
        return new HeartbeatRequest();
    }

    @Override
    public AuthorizeRequest convertRequest(ocpp.cs._2012._06.AuthorizeRequest request) {
        return new AuthorizeRequest()
                .withIdTag(request.getIdTag());
    }

    @Override
    public DataTransferRequest convertRequest(ocpp.cs._2012._06.DataTransferRequest request) {
        return new DataTransferRequest()
                .withVendorId(request.getVendorId())
                .withMessageId(request.getMessageId())
                .withData(request.getData());
    }

    // -------------------------------------------------------------------------
    // Responses
    // -------------------------------------------------------------------------

    @Override
    public BootNotificationResponse convertResponse(ocpp.cs._2015._10.BootNotificationResponse response) {
        return new BootNotificationResponse()
                .withCurrentTime(response.getCurrentTime())
                .withHeartbeatInterval(response.getInterval())
                .withStatus(RegistrationStatus.fromValue(response.getStatus().value()));
    }

    @Override
    public FirmwareStatusNotificationResponse convertResponse(
            ocpp.cs._2015._10.FirmwareStatusNotificationResponse response) {
        return new FirmwareStatusNotificationResponse();
    }

    @Override
    public StatusNotificationResponse convertResponse(ocpp.cs._2015._10.StatusNotificationResponse response) {
        return new StatusNotificationResponse();
    }

    @Override
    public MeterValuesResponse convertResponse(ocpp.cs._2015._10.MeterValuesResponse response) {
        return new MeterValuesResponse();
    }

    @Override
    public DiagnosticsStatusNotificationResponse convertResponse(
            ocpp.cs._2015._10.DiagnosticsStatusNotificationResponse response) {
        return new DiagnosticsStatusNotificationResponse();
    }

    @Override
    public StartTransactionResponse convertResponse(ocpp.cs._2015._10.StartTransactionResponse response) {
        return new StartTransactionResponse()
                .withIdTagInfo(toOcpp15IdTagInfo(response.getIdTagInfo()))
                .withTransactionId(response.getTransactionId());
    }

    @Override
    public StopTransactionResponse convertResponse(ocpp.cs._2015._10.StopTransactionResponse response) {
        return new StopTransactionResponse()
                .withIdTagInfo(toOcpp15IdTagInfo(response.getIdTagInfo()));
    }

    @Override
    public HeartbeatResponse convertResponse(ocpp.cs._2015._10.HeartbeatResponse response) {
        return new HeartbeatResponse()
                .withCurrentTime(response.getCurrentTime());
    }

    @Override
    public AuthorizeResponse convertResponse(ocpp.cs._2015._10.AuthorizeResponse response) {
        return new AuthorizeResponse()
                .withIdTagInfo(toOcpp15IdTagInfo(response.getIdTagInfo()));
    }

    @Override
    public DataTransferResponse convertResponse(ocpp.cs._2015._10.DataTransferResponse response) {
        return new DataTransferResponse()
                .withStatus(DataTransferStatus.fromValue(response.getStatus().value()))
                .withData(response.getData());
    }

    // -------------------------------------------------------------------------
    // Custom mapping, for situations where a unique mapping does not exists
    // -------------------------------------------------------------------------

    /**
     * OCCUPIED was replaced with several more specific values. For now it will be replaced with "CHARGING",
     * but something else might make more sense at this place
     */
    private static ChargePointStatus customMapStatus(ocpp.cs._2012._06.ChargePointStatus status) {
        if (status == ocpp.cs._2012._06.ChargePointStatus.OCCUPIED) {
            return ChargePointStatus.CHARGING;
        }
        return ChargePointStatus.fromValue(status.value());
    }

    /**
     * Mapping required: Enum values in both directions don't necessarily match.
     * Update: According to the 1.6 specification, MODE_3_ERROR was simply renamed to EV_COMMUNICATION_ERROR
     */
    private static ChargePointErrorCode customMapErrorCode(ocpp.cs._2012._06.ChargePointErrorCode errorCode15) {
        if (errorCode15 == ocpp.cs._2012._06.ChargePointErrorCode.MODE_3_ERROR) {
            return ChargePointErrorCode.EV_COMMUNICATION_ERROR;
        }
        return ChargePointErrorCode.fromValue(errorCode15.value());
    }

    /**
     * AMP and VOLT are shortened to A and V, respectively.
     *
     * https://github.com/RWTH-i5-IDSG/steve/issues/59
     */
    private static UnitOfMeasure convertUnit(ocpp.cs._2012._06.UnitOfMeasure unit) {
        switch (unit) {
            case AMP:
                return UnitOfMeasure.A;
            case VOLT:
                return UnitOfMeasure.V;
            default:
                return UnitOfMeasure.fromValue(unit.value());
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static IdTagInfo toOcpp15IdTagInfo(ocpp.cs._2015._10.IdTagInfo info16) {
        if (info16 == null) {
            return null;
        }
        return new IdTagInfo()
                .withExpiryDate(info16.getExpiryDate())
                .withParentIdTag(info16.getParentIdTag())
                .withStatus(AuthorizationStatus.fromValue(info16.getStatus().value()));
    }

    private static SampledValue toOcpp16SampledValue(ocpp.cs._2012._06.MeterValue.Value f) {
        return new SampledValue()
                .withContext(f.isSetContext() ? ReadingContext.fromValue(f.getContext().value()) : null)
                .withFormat(f.isSetFormat() ? ValueFormat.fromValue(f.getFormat().value()) : null)
                .withLocation(f.isSetLocation() ? Location.fromValue(f.getLocation().value()) : null)
                .withMeasurand(f.isSetMeasurand() ? Measurand.fromValue(f.getMeasurand().value()) : null)
                .withUnit(f.isSetUnit() ? convertUnit(f.getUnit()) : null)
                .withValue(f.getValue());
    }

    private static List<SampledValue> toOcpp16SampledValueList(List<ocpp.cs._2012._06.MeterValue.Value> vals) {
        return vals.stream()
                   .map(Server15to16Impl::toOcpp16SampledValue)
                   .collect(Collectors.toList());
    }

    private static MeterValue toOcpp16MeterValue(ocpp.cs._2012._06.MeterValue e) {
        return new MeterValue().withTimestamp(e.getTimestamp())
                               .withSampledValue(toOcpp16SampledValueList(e.getValue()));
    }

    private static List<MeterValue> toOcpp16TransactionData(List<TransactionData> transactionData) {
        return transactionData.stream()
                              .flatMap(data -> data.getValues().stream())
                              .map(Server15to16Impl::toOcpp16MeterValue)
                              .collect(Collectors.toList());
    }
}
