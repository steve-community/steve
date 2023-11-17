
package ocpp.cs._2012._06;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ocpp.cs._2012._06 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ChargeBoxIdentity_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "chargeBoxIdentity");
    private final static QName _AuthorizeRequest_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "authorizeRequest");
    private final static QName _AuthorizeResponse_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "authorizeResponse");
    private final static QName _StartTransactionRequest_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "startTransactionRequest");
    private final static QName _StartTransactionResponse_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "startTransactionResponse");
    private final static QName _StopTransactionRequest_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "stopTransactionRequest");
    private final static QName _StopTransactionResponse_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "stopTransactionResponse");
    private final static QName _HeartbeatRequest_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "heartbeatRequest");
    private final static QName _HeartbeatResponse_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "heartbeatResponse");
    private final static QName _MeterValuesRequest_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "meterValuesRequest");
    private final static QName _MeterValuesResponse_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "meterValuesResponse");
    private final static QName _BootNotificationRequest_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "bootNotificationRequest");
    private final static QName _BootNotificationResponse_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "bootNotificationResponse");
    private final static QName _StatusNotificationRequest_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "statusNotificationRequest");
    private final static QName _StatusNotificationResponse_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "statusNotificationResponse");
    private final static QName _FirmwareStatusNotificationRequest_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "firmwareStatusNotificationRequest");
    private final static QName _FirmwareStatusNotificationResponse_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "firmwareStatusNotificationResponse");
    private final static QName _DiagnosticsStatusNotificationRequest_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "diagnosticsStatusNotificationRequest");
    private final static QName _DiagnosticsStatusNotificationResponse_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "diagnosticsStatusNotificationResponse");
    private final static QName _DataTransferRequest_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "dataTransferRequest");
    private final static QName _DataTransferResponse_QNAME = new QName("urn://Ocpp/Cs/2012/06/", "dataTransferResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ocpp.cs._2012._06
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MeterValue }
     * 
     */
    public MeterValue createMeterValue() {
        return new MeterValue();
    }

    /**
     * Create an instance of {@link AuthorizeRequest }
     * 
     */
    public AuthorizeRequest createAuthorizeRequest() {
        return new AuthorizeRequest();
    }

    /**
     * Create an instance of {@link AuthorizeResponse }
     * 
     */
    public AuthorizeResponse createAuthorizeResponse() {
        return new AuthorizeResponse();
    }

    /**
     * Create an instance of {@link StartTransactionRequest }
     * 
     */
    public StartTransactionRequest createStartTransactionRequest() {
        return new StartTransactionRequest();
    }

    /**
     * Create an instance of {@link StartTransactionResponse }
     * 
     */
    public StartTransactionResponse createStartTransactionResponse() {
        return new StartTransactionResponse();
    }

    /**
     * Create an instance of {@link StopTransactionRequest }
     * 
     */
    public StopTransactionRequest createStopTransactionRequest() {
        return new StopTransactionRequest();
    }

    /**
     * Create an instance of {@link StopTransactionResponse }
     * 
     */
    public StopTransactionResponse createStopTransactionResponse() {
        return new StopTransactionResponse();
    }

    /**
     * Create an instance of {@link HeartbeatRequest }
     * 
     */
    public HeartbeatRequest createHeartbeatRequest() {
        return new HeartbeatRequest();
    }

    /**
     * Create an instance of {@link HeartbeatResponse }
     * 
     */
    public HeartbeatResponse createHeartbeatResponse() {
        return new HeartbeatResponse();
    }

    /**
     * Create an instance of {@link MeterValuesRequest }
     * 
     */
    public MeterValuesRequest createMeterValuesRequest() {
        return new MeterValuesRequest();
    }

    /**
     * Create an instance of {@link MeterValuesResponse }
     * 
     */
    public MeterValuesResponse createMeterValuesResponse() {
        return new MeterValuesResponse();
    }

    /**
     * Create an instance of {@link BootNotificationRequest }
     * 
     */
    public BootNotificationRequest createBootNotificationRequest() {
        return new BootNotificationRequest();
    }

    /**
     * Create an instance of {@link BootNotificationResponse }
     * 
     */
    public BootNotificationResponse createBootNotificationResponse() {
        return new BootNotificationResponse();
    }

    /**
     * Create an instance of {@link StatusNotificationRequest }
     * 
     */
    public StatusNotificationRequest createStatusNotificationRequest() {
        return new StatusNotificationRequest();
    }

    /**
     * Create an instance of {@link StatusNotificationResponse }
     * 
     */
    public StatusNotificationResponse createStatusNotificationResponse() {
        return new StatusNotificationResponse();
    }

    /**
     * Create an instance of {@link FirmwareStatusNotificationRequest }
     * 
     */
    public FirmwareStatusNotificationRequest createFirmwareStatusNotificationRequest() {
        return new FirmwareStatusNotificationRequest();
    }

    /**
     * Create an instance of {@link FirmwareStatusNotificationResponse }
     * 
     */
    public FirmwareStatusNotificationResponse createFirmwareStatusNotificationResponse() {
        return new FirmwareStatusNotificationResponse();
    }

    /**
     * Create an instance of {@link DiagnosticsStatusNotificationRequest }
     * 
     */
    public DiagnosticsStatusNotificationRequest createDiagnosticsStatusNotificationRequest() {
        return new DiagnosticsStatusNotificationRequest();
    }

    /**
     * Create an instance of {@link DiagnosticsStatusNotificationResponse }
     * 
     */
    public DiagnosticsStatusNotificationResponse createDiagnosticsStatusNotificationResponse() {
        return new DiagnosticsStatusNotificationResponse();
    }

    /**
     * Create an instance of {@link DataTransferRequest }
     * 
     */
    public DataTransferRequest createDataTransferRequest() {
        return new DataTransferRequest();
    }

    /**
     * Create an instance of {@link DataTransferResponse }
     * 
     */
    public DataTransferResponse createDataTransferResponse() {
        return new DataTransferResponse();
    }

    /**
     * Create an instance of {@link IdTagInfo }
     * 
     */
    public IdTagInfo createIdTagInfo() {
        return new IdTagInfo();
    }

    /**
     * Create an instance of {@link TransactionData }
     * 
     */
    public TransactionData createTransactionData() {
        return new TransactionData();
    }

    /**
     * Create an instance of {@link MeterValue.Value }
     * 
     */
    public MeterValue.Value createMeterValueValue() {
        return new MeterValue.Value();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "chargeBoxIdentity")
    public JAXBElement<String> createChargeBoxIdentity(String value) {
        return new JAXBElement<String>(_ChargeBoxIdentity_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthorizeRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "authorizeRequest")
    public JAXBElement<AuthorizeRequest> createAuthorizeRequest(AuthorizeRequest value) {
        return new JAXBElement<AuthorizeRequest>(_AuthorizeRequest_QNAME, AuthorizeRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthorizeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "authorizeResponse")
    public JAXBElement<AuthorizeResponse> createAuthorizeResponse(AuthorizeResponse value) {
        return new JAXBElement<AuthorizeResponse>(_AuthorizeResponse_QNAME, AuthorizeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartTransactionRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "startTransactionRequest")
    public JAXBElement<StartTransactionRequest> createStartTransactionRequest(StartTransactionRequest value) {
        return new JAXBElement<StartTransactionRequest>(_StartTransactionRequest_QNAME, StartTransactionRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartTransactionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "startTransactionResponse")
    public JAXBElement<StartTransactionResponse> createStartTransactionResponse(StartTransactionResponse value) {
        return new JAXBElement<StartTransactionResponse>(_StartTransactionResponse_QNAME, StartTransactionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StopTransactionRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "stopTransactionRequest")
    public JAXBElement<StopTransactionRequest> createStopTransactionRequest(StopTransactionRequest value) {
        return new JAXBElement<StopTransactionRequest>(_StopTransactionRequest_QNAME, StopTransactionRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StopTransactionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "stopTransactionResponse")
    public JAXBElement<StopTransactionResponse> createStopTransactionResponse(StopTransactionResponse value) {
        return new JAXBElement<StopTransactionResponse>(_StopTransactionResponse_QNAME, StopTransactionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HeartbeatRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "heartbeatRequest")
    public JAXBElement<HeartbeatRequest> createHeartbeatRequest(HeartbeatRequest value) {
        return new JAXBElement<HeartbeatRequest>(_HeartbeatRequest_QNAME, HeartbeatRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HeartbeatResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "heartbeatResponse")
    public JAXBElement<HeartbeatResponse> createHeartbeatResponse(HeartbeatResponse value) {
        return new JAXBElement<HeartbeatResponse>(_HeartbeatResponse_QNAME, HeartbeatResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeterValuesRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "meterValuesRequest")
    public JAXBElement<MeterValuesRequest> createMeterValuesRequest(MeterValuesRequest value) {
        return new JAXBElement<MeterValuesRequest>(_MeterValuesRequest_QNAME, MeterValuesRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeterValuesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "meterValuesResponse")
    public JAXBElement<MeterValuesResponse> createMeterValuesResponse(MeterValuesResponse value) {
        return new JAXBElement<MeterValuesResponse>(_MeterValuesResponse_QNAME, MeterValuesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BootNotificationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "bootNotificationRequest")
    public JAXBElement<BootNotificationRequest> createBootNotificationRequest(BootNotificationRequest value) {
        return new JAXBElement<BootNotificationRequest>(_BootNotificationRequest_QNAME, BootNotificationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BootNotificationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "bootNotificationResponse")
    public JAXBElement<BootNotificationResponse> createBootNotificationResponse(BootNotificationResponse value) {
        return new JAXBElement<BootNotificationResponse>(_BootNotificationResponse_QNAME, BootNotificationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatusNotificationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "statusNotificationRequest")
    public JAXBElement<StatusNotificationRequest> createStatusNotificationRequest(StatusNotificationRequest value) {
        return new JAXBElement<StatusNotificationRequest>(_StatusNotificationRequest_QNAME, StatusNotificationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatusNotificationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "statusNotificationResponse")
    public JAXBElement<StatusNotificationResponse> createStatusNotificationResponse(StatusNotificationResponse value) {
        return new JAXBElement<StatusNotificationResponse>(_StatusNotificationResponse_QNAME, StatusNotificationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FirmwareStatusNotificationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "firmwareStatusNotificationRequest")
    public JAXBElement<FirmwareStatusNotificationRequest> createFirmwareStatusNotificationRequest(FirmwareStatusNotificationRequest value) {
        return new JAXBElement<FirmwareStatusNotificationRequest>(_FirmwareStatusNotificationRequest_QNAME, FirmwareStatusNotificationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FirmwareStatusNotificationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "firmwareStatusNotificationResponse")
    public JAXBElement<FirmwareStatusNotificationResponse> createFirmwareStatusNotificationResponse(FirmwareStatusNotificationResponse value) {
        return new JAXBElement<FirmwareStatusNotificationResponse>(_FirmwareStatusNotificationResponse_QNAME, FirmwareStatusNotificationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DiagnosticsStatusNotificationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "diagnosticsStatusNotificationRequest")
    public JAXBElement<DiagnosticsStatusNotificationRequest> createDiagnosticsStatusNotificationRequest(DiagnosticsStatusNotificationRequest value) {
        return new JAXBElement<DiagnosticsStatusNotificationRequest>(_DiagnosticsStatusNotificationRequest_QNAME, DiagnosticsStatusNotificationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DiagnosticsStatusNotificationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "diagnosticsStatusNotificationResponse")
    public JAXBElement<DiagnosticsStatusNotificationResponse> createDiagnosticsStatusNotificationResponse(DiagnosticsStatusNotificationResponse value) {
        return new JAXBElement<DiagnosticsStatusNotificationResponse>(_DiagnosticsStatusNotificationResponse_QNAME, DiagnosticsStatusNotificationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataTransferRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "dataTransferRequest")
    public JAXBElement<DataTransferRequest> createDataTransferRequest(DataTransferRequest value) {
        return new JAXBElement<DataTransferRequest>(_DataTransferRequest_QNAME, DataTransferRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataTransferResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cs/2012/06/", name = "dataTransferResponse")
    public JAXBElement<DataTransferResponse> createDataTransferResponse(DataTransferResponse value) {
        return new JAXBElement<DataTransferResponse>(_DataTransferResponse_QNAME, DataTransferResponse.class, null, value);
    }

}
