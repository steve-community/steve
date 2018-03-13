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
    public FirmwareStatusNotificationRequest convertRequest(ocpp.cs._2012._06.FirmwareStatusNotificationRequest request) {
        return new FirmwareStatusNotificationRequest()
                .withStatus(FirmwareStatus.fromValue(request.getStatus().value()));
    }

    @Override
    public StatusNotificationRequest convertRequest(ocpp.cs._2012._06.StatusNotificationRequest request) {
        ChargePointStatus status = ChargePointStatus.fromValue(request.getStatus().value());

        ChargePointErrorCode errorCode16;
        ocpp.cs._2012._06.ChargePointErrorCode errorCode15 = request.getErrorCode();


        // Mapping required: Enum values in both directions don't necessarily match.
        // TODO: Make sure that no information is lost (by e.g. creating InsertConnectorStatusParams earlier in the pipeline)
        if (errorCode15.equals(ocpp.cs._2012._06.ChargePointErrorCode.MODE_3_ERROR)) {
            errorCode16 = ChargePointErrorCode.OTHER_ERROR;
        } else {
            errorCode16 = ChargePointErrorCode.fromValue(request.getErrorCode().value());
        }

        return new StatusNotificationRequest()
                .withConnectorId(request.getConnectorId())
                .withStatus(status)
                .withErrorCode(errorCode16);
    }

    @Override
    public MeterValuesRequest convertRequest(ocpp.cs._2012._06.MeterValuesRequest request) {
        //nested lists, therefore not that readable
        List<MeterValue> values16 = request.getValues()
                                           .stream()
                                           .map(e -> new MeterValue()
                                                   .withTimestamp(e.getTimestamp())
                                                   .withSampledValue(e.getValue().stream()
                                                                      .map(f -> new SampledValue()
                                                                                      .withContext(ReadingContext.fromValue(f.getContext().value()))
                                                                                      .withFormat(ValueFormat.fromValue(f.getFormat().value()))
                                                                                      .withLocation(Location.fromValue(f.getLocation().value()))
                                                                                      .withMeasurand(Measurand.fromValue(f.getMeasurand().value()))
                                                                                      .withUnit(UnitOfMeasure.fromValue(f.getUnit().value()))
                                                                                      .withValue(f.getValue())
                                                                      )
                                                                      .collect(Collectors.toList())
                                                   )
                                           )
                                           .collect(Collectors.toList());

        return new MeterValuesRequest()
                .withTransactionId(request.getTransactionId())
                .withConnectorId(request.getConnectorId())
                .withMeterValue(values16);
    }

    @Override
    public DiagnosticsStatusNotificationRequest convertRequest(ocpp.cs._2012._06.DiagnosticsStatusNotificationRequest request) {
        DiagnosticsStatus status = DiagnosticsStatus.fromValue(request.getStatus().value());
        return new DiagnosticsStatusNotificationRequest()
                .withStatus(status);
    }

    @Override
    public StartTransactionRequest convertRequest(ocpp.cs._2012._06.StartTransactionRequest request) {
        return new StartTransactionRequest()
                .withConnectorId(request.getConnectorId())
                .withIdTag(request.getIdTag())
                .withMeterStart(request.getMeterStart())
                .withTimestamp(request.getTimestamp());
    }

    @Override
    public StopTransactionRequest convertRequest(ocpp.cs._2012._06.StopTransactionRequest request) {
        return new StopTransactionRequest()
                .withIdTag(request.getIdTag())
//                .withReason(Reason.OTHER)     // Reason was introduced with 1.6 and is optional (no mapping needed)
                .withMeterStop(request.getMeterStop())
                .withTimestamp(request.getTimestamp())
                .withTransactionId(request.getTransactionId());
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
    public DiagnosticsStatusNotificationResponse convertResponse(ocpp.cs._2015._10.DiagnosticsStatusNotificationResponse response) {
        return new DiagnosticsStatusNotificationResponse();
    }

    @Override
    public StartTransactionResponse convertResponse(ocpp.cs._2015._10.StartTransactionResponse response) {
        return new StartTransactionResponse()
                .withIdTagInfo(convertIdTagInfo16to15(response.getIdTagInfo()))
                .withTransactionId(response.getTransactionId());
    }

    @Override
    public StopTransactionResponse convertResponse(ocpp.cs._2015._10.StopTransactionResponse response) {
        return new StopTransactionResponse()
                .withIdTagInfo(convertIdTagInfo16to15(response.getIdTagInfo()));
    }

    @Override
    public HeartbeatResponse convertResponse(ocpp.cs._2015._10.HeartbeatResponse response) {
        return new HeartbeatResponse()
                .withCurrentTime(response.getCurrentTime());
    }

    @Override
    public AuthorizeResponse convertResponse(ocpp.cs._2015._10.AuthorizeResponse response) {
        return new AuthorizeResponse()
                .withIdTagInfo(convertIdTagInfo16to15(response.getIdTagInfo()));
    }

    @Override
    public DataTransferResponse convertResponse(ocpp.cs._2015._10.DataTransferResponse response) {
        return new DataTransferResponse()
                .withStatus(DataTransferStatus.fromValue(response.getStatus().value()))
                .withData(response.getData());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static IdTagInfo convertIdTagInfo16to15(ocpp.cs._2015._10.IdTagInfo info16) {
        return new IdTagInfo()
                .withExpiryDate(info16.getExpiryDate())
                .withParentIdTag(info16.getParentIdTag())
                .withStatus(AuthorizationStatus.fromValue(info16.getStatus().value()));
    }

}
