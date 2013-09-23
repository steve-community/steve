
package ocpp.cp._2012._06;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ocpp.cp._2012._06 package. 
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

    private final static QName _GetConfigurationRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "getConfigurationRequest");
    private final static QName _CancelReservationResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "cancelReservationResponse");
    private final static QName _RemoteStopTransactionResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "remoteStopTransactionResponse");
    private final static QName _UnlockConnectorResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "unlockConnectorResponse");
    private final static QName _ChangeAvailabilityRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "changeAvailabilityRequest");
    private final static QName _ReserveNowResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "reserveNowResponse");
    private final static QName _GetLocalListVersionResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "getLocalListVersionResponse");
    private final static QName _ClearCacheResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "clearCacheResponse");
    private final static QName _GetConfigurationResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "getConfigurationResponse");
    private final static QName _CancelReservationRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "cancelReservationRequest");
    private final static QName _RemoteStopTransactionRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "remoteStopTransactionRequest");
    private final static QName _RemoteStartTransactionRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "remoteStartTransactionRequest");
    private final static QName _GetDiagnosticsRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "getDiagnosticsRequest");
    private final static QName _ResetRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "resetRequest");
    private final static QName _UnlockConnectorRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "unlockConnectorRequest");
    private final static QName _ChangeConfigurationResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "changeConfigurationResponse");
    private final static QName _ReserveNowRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "reserveNowRequest");
    private final static QName _DataTransferRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "dataTransferRequest");
    private final static QName _RemoteStartTransactionResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "remoteStartTransactionResponse");
    private final static QName _ChangeConfigurationRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "changeConfigurationRequest");
    private final static QName _ChangeAvailabilityResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "changeAvailabilityResponse");
    private final static QName _GetDiagnosticsResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "getDiagnosticsResponse");
    private final static QName _SendLocalListResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "sendLocalListResponse");
    private final static QName _UpdateFirmwareRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "updateFirmwareRequest");
    private final static QName _GetLocalListVersionRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "getLocalListVersionRequest");
    private final static QName _SendLocalListRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "sendLocalListRequest");
    private final static QName _ChargeBoxIdentity_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "chargeBoxIdentity");
    private final static QName _DataTransferResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "dataTransferResponse");
    private final static QName _ClearCacheRequest_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "clearCacheRequest");
    private final static QName _UpdateFirmwareResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "updateFirmwareResponse");
    private final static QName _ResetResponse_QNAME = new QName("urn://Ocpp/Cp/2012/06/", "resetResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ocpp.cp._2012._06
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetLocalListVersionResponse }
     * 
     */
    public GetLocalListVersionResponse createGetLocalListVersionResponse() {
        return new GetLocalListVersionResponse();
    }

    /**
     * Create an instance of {@link SendLocalListRequest }
     * 
     */
    public SendLocalListRequest createSendLocalListRequest() {
        return new SendLocalListRequest();
    }

    /**
     * Create an instance of {@link KeyValue }
     * 
     */
    public KeyValue createKeyValue() {
        return new KeyValue();
    }

    /**
     * Create an instance of {@link IdTagInfo }
     * 
     */
    public IdTagInfo createIdTagInfo() {
        return new IdTagInfo();
    }

    /**
     * Create an instance of {@link ChangeAvailabilityRequest }
     * 
     */
    public ChangeAvailabilityRequest createChangeAvailabilityRequest() {
        return new ChangeAvailabilityRequest();
    }

    /**
     * Create an instance of {@link ChangeConfigurationRequest }
     * 
     */
    public ChangeConfigurationRequest createChangeConfigurationRequest() {
        return new ChangeConfigurationRequest();
    }

    /**
     * Create an instance of {@link SendLocalListResponse }
     * 
     */
    public SendLocalListResponse createSendLocalListResponse() {
        return new SendLocalListResponse();
    }

    /**
     * Create an instance of {@link UpdateFirmwareResponse }
     * 
     */
    public UpdateFirmwareResponse createUpdateFirmwareResponse() {
        return new UpdateFirmwareResponse();
    }

    /**
     * Create an instance of {@link DataTransferResponse }
     * 
     */
    public DataTransferResponse createDataTransferResponse() {
        return new DataTransferResponse();
    }

    /**
     * Create an instance of {@link RemoteStopTransactionRequest }
     * 
     */
    public RemoteStopTransactionRequest createRemoteStopTransactionRequest() {
        return new RemoteStopTransactionRequest();
    }

    /**
     * Create an instance of {@link RemoteStartTransactionRequest }
     * 
     */
    public RemoteStartTransactionRequest createRemoteStartTransactionRequest() {
        return new RemoteStartTransactionRequest();
    }

    /**
     * Create an instance of {@link ResetResponse }
     * 
     */
    public ResetResponse createResetResponse() {
        return new ResetResponse();
    }

    /**
     * Create an instance of {@link ReserveNowRequest }
     * 
     */
    public ReserveNowRequest createReserveNowRequest() {
        return new ReserveNowRequest();
    }

    /**
     * Create an instance of {@link GetLocalListVersionRequest }
     * 
     */
    public GetLocalListVersionRequest createGetLocalListVersionRequest() {
        return new GetLocalListVersionRequest();
    }

    /**
     * Create an instance of {@link ReserveNowResponse }
     * 
     */
    public ReserveNowResponse createReserveNowResponse() {
        return new ReserveNowResponse();
    }

    /**
     * Create an instance of {@link GetConfigurationResponse }
     * 
     */
    public GetConfigurationResponse createGetConfigurationResponse() {
        return new GetConfigurationResponse();
    }

    /**
     * Create an instance of {@link CancelReservationRequest }
     * 
     */
    public CancelReservationRequest createCancelReservationRequest() {
        return new CancelReservationRequest();
    }

    /**
     * Create an instance of {@link UpdateFirmwareRequest }
     * 
     */
    public UpdateFirmwareRequest createUpdateFirmwareRequest() {
        return new UpdateFirmwareRequest();
    }

    /**
     * Create an instance of {@link DataTransferRequest }
     * 
     */
    public DataTransferRequest createDataTransferRequest() {
        return new DataTransferRequest();
    }

    /**
     * Create an instance of {@link UnlockConnectorResponse }
     * 
     */
    public UnlockConnectorResponse createUnlockConnectorResponse() {
        return new UnlockConnectorResponse();
    }

    /**
     * Create an instance of {@link AuthorisationData }
     * 
     */
    public AuthorisationData createAuthorisationData() {
        return new AuthorisationData();
    }

    /**
     * Create an instance of {@link ResetRequest }
     * 
     */
    public ResetRequest createResetRequest() {
        return new ResetRequest();
    }

    /**
     * Create an instance of {@link GetDiagnosticsRequest }
     * 
     */
    public GetDiagnosticsRequest createGetDiagnosticsRequest() {
        return new GetDiagnosticsRequest();
    }

    /**
     * Create an instance of {@link ClearCacheResponse }
     * 
     */
    public ClearCacheResponse createClearCacheResponse() {
        return new ClearCacheResponse();
    }

    /**
     * Create an instance of {@link RemoteStopTransactionResponse }
     * 
     */
    public RemoteStopTransactionResponse createRemoteStopTransactionResponse() {
        return new RemoteStopTransactionResponse();
    }

    /**
     * Create an instance of {@link UnlockConnectorRequest }
     * 
     */
    public UnlockConnectorRequest createUnlockConnectorRequest() {
        return new UnlockConnectorRequest();
    }

    /**
     * Create an instance of {@link GetConfigurationRequest }
     * 
     */
    public GetConfigurationRequest createGetConfigurationRequest() {
        return new GetConfigurationRequest();
    }

    /**
     * Create an instance of {@link ChangeConfigurationResponse }
     * 
     */
    public ChangeConfigurationResponse createChangeConfigurationResponse() {
        return new ChangeConfigurationResponse();
    }

    /**
     * Create an instance of {@link RemoteStartTransactionResponse }
     * 
     */
    public RemoteStartTransactionResponse createRemoteStartTransactionResponse() {
        return new RemoteStartTransactionResponse();
    }

    /**
     * Create an instance of {@link ClearCacheRequest }
     * 
     */
    public ClearCacheRequest createClearCacheRequest() {
        return new ClearCacheRequest();
    }

    /**
     * Create an instance of {@link CancelReservationResponse }
     * 
     */
    public CancelReservationResponse createCancelReservationResponse() {
        return new CancelReservationResponse();
    }

    /**
     * Create an instance of {@link GetDiagnosticsResponse }
     * 
     */
    public GetDiagnosticsResponse createGetDiagnosticsResponse() {
        return new GetDiagnosticsResponse();
    }

    /**
     * Create an instance of {@link ChangeAvailabilityResponse }
     * 
     */
    public ChangeAvailabilityResponse createChangeAvailabilityResponse() {
        return new ChangeAvailabilityResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConfigurationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "getConfigurationRequest")
    public JAXBElement<GetConfigurationRequest> createGetConfigurationRequest(GetConfigurationRequest value) {
        return new JAXBElement<GetConfigurationRequest>(_GetConfigurationRequest_QNAME, GetConfigurationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelReservationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "cancelReservationResponse")
    public JAXBElement<CancelReservationResponse> createCancelReservationResponse(CancelReservationResponse value) {
        return new JAXBElement<CancelReservationResponse>(_CancelReservationResponse_QNAME, CancelReservationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoteStopTransactionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "remoteStopTransactionResponse")
    public JAXBElement<RemoteStopTransactionResponse> createRemoteStopTransactionResponse(RemoteStopTransactionResponse value) {
        return new JAXBElement<RemoteStopTransactionResponse>(_RemoteStopTransactionResponse_QNAME, RemoteStopTransactionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnlockConnectorResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "unlockConnectorResponse")
    public JAXBElement<UnlockConnectorResponse> createUnlockConnectorResponse(UnlockConnectorResponse value) {
        return new JAXBElement<UnlockConnectorResponse>(_UnlockConnectorResponse_QNAME, UnlockConnectorResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeAvailabilityRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "changeAvailabilityRequest")
    public JAXBElement<ChangeAvailabilityRequest> createChangeAvailabilityRequest(ChangeAvailabilityRequest value) {
        return new JAXBElement<ChangeAvailabilityRequest>(_ChangeAvailabilityRequest_QNAME, ChangeAvailabilityRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReserveNowResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "reserveNowResponse")
    public JAXBElement<ReserveNowResponse> createReserveNowResponse(ReserveNowResponse value) {
        return new JAXBElement<ReserveNowResponse>(_ReserveNowResponse_QNAME, ReserveNowResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLocalListVersionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "getLocalListVersionResponse")
    public JAXBElement<GetLocalListVersionResponse> createGetLocalListVersionResponse(GetLocalListVersionResponse value) {
        return new JAXBElement<GetLocalListVersionResponse>(_GetLocalListVersionResponse_QNAME, GetLocalListVersionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClearCacheResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "clearCacheResponse")
    public JAXBElement<ClearCacheResponse> createClearCacheResponse(ClearCacheResponse value) {
        return new JAXBElement<ClearCacheResponse>(_ClearCacheResponse_QNAME, ClearCacheResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConfigurationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "getConfigurationResponse")
    public JAXBElement<GetConfigurationResponse> createGetConfigurationResponse(GetConfigurationResponse value) {
        return new JAXBElement<GetConfigurationResponse>(_GetConfigurationResponse_QNAME, GetConfigurationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelReservationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "cancelReservationRequest")
    public JAXBElement<CancelReservationRequest> createCancelReservationRequest(CancelReservationRequest value) {
        return new JAXBElement<CancelReservationRequest>(_CancelReservationRequest_QNAME, CancelReservationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoteStopTransactionRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "remoteStopTransactionRequest")
    public JAXBElement<RemoteStopTransactionRequest> createRemoteStopTransactionRequest(RemoteStopTransactionRequest value) {
        return new JAXBElement<RemoteStopTransactionRequest>(_RemoteStopTransactionRequest_QNAME, RemoteStopTransactionRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoteStartTransactionRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "remoteStartTransactionRequest")
    public JAXBElement<RemoteStartTransactionRequest> createRemoteStartTransactionRequest(RemoteStartTransactionRequest value) {
        return new JAXBElement<RemoteStartTransactionRequest>(_RemoteStartTransactionRequest_QNAME, RemoteStartTransactionRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDiagnosticsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "getDiagnosticsRequest")
    public JAXBElement<GetDiagnosticsRequest> createGetDiagnosticsRequest(GetDiagnosticsRequest value) {
        return new JAXBElement<GetDiagnosticsRequest>(_GetDiagnosticsRequest_QNAME, GetDiagnosticsRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResetRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "resetRequest")
    public JAXBElement<ResetRequest> createResetRequest(ResetRequest value) {
        return new JAXBElement<ResetRequest>(_ResetRequest_QNAME, ResetRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnlockConnectorRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "unlockConnectorRequest")
    public JAXBElement<UnlockConnectorRequest> createUnlockConnectorRequest(UnlockConnectorRequest value) {
        return new JAXBElement<UnlockConnectorRequest>(_UnlockConnectorRequest_QNAME, UnlockConnectorRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeConfigurationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "changeConfigurationResponse")
    public JAXBElement<ChangeConfigurationResponse> createChangeConfigurationResponse(ChangeConfigurationResponse value) {
        return new JAXBElement<ChangeConfigurationResponse>(_ChangeConfigurationResponse_QNAME, ChangeConfigurationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReserveNowRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "reserveNowRequest")
    public JAXBElement<ReserveNowRequest> createReserveNowRequest(ReserveNowRequest value) {
        return new JAXBElement<ReserveNowRequest>(_ReserveNowRequest_QNAME, ReserveNowRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataTransferRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "dataTransferRequest")
    public JAXBElement<DataTransferRequest> createDataTransferRequest(DataTransferRequest value) {
        return new JAXBElement<DataTransferRequest>(_DataTransferRequest_QNAME, DataTransferRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoteStartTransactionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "remoteStartTransactionResponse")
    public JAXBElement<RemoteStartTransactionResponse> createRemoteStartTransactionResponse(RemoteStartTransactionResponse value) {
        return new JAXBElement<RemoteStartTransactionResponse>(_RemoteStartTransactionResponse_QNAME, RemoteStartTransactionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeConfigurationRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "changeConfigurationRequest")
    public JAXBElement<ChangeConfigurationRequest> createChangeConfigurationRequest(ChangeConfigurationRequest value) {
        return new JAXBElement<ChangeConfigurationRequest>(_ChangeConfigurationRequest_QNAME, ChangeConfigurationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeAvailabilityResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "changeAvailabilityResponse")
    public JAXBElement<ChangeAvailabilityResponse> createChangeAvailabilityResponse(ChangeAvailabilityResponse value) {
        return new JAXBElement<ChangeAvailabilityResponse>(_ChangeAvailabilityResponse_QNAME, ChangeAvailabilityResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDiagnosticsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "getDiagnosticsResponse")
    public JAXBElement<GetDiagnosticsResponse> createGetDiagnosticsResponse(GetDiagnosticsResponse value) {
        return new JAXBElement<GetDiagnosticsResponse>(_GetDiagnosticsResponse_QNAME, GetDiagnosticsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendLocalListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "sendLocalListResponse")
    public JAXBElement<SendLocalListResponse> createSendLocalListResponse(SendLocalListResponse value) {
        return new JAXBElement<SendLocalListResponse>(_SendLocalListResponse_QNAME, SendLocalListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateFirmwareRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "updateFirmwareRequest")
    public JAXBElement<UpdateFirmwareRequest> createUpdateFirmwareRequest(UpdateFirmwareRequest value) {
        return new JAXBElement<UpdateFirmwareRequest>(_UpdateFirmwareRequest_QNAME, UpdateFirmwareRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLocalListVersionRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "getLocalListVersionRequest")
    public JAXBElement<GetLocalListVersionRequest> createGetLocalListVersionRequest(GetLocalListVersionRequest value) {
        return new JAXBElement<GetLocalListVersionRequest>(_GetLocalListVersionRequest_QNAME, GetLocalListVersionRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendLocalListRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "sendLocalListRequest")
    public JAXBElement<SendLocalListRequest> createSendLocalListRequest(SendLocalListRequest value) {
        return new JAXBElement<SendLocalListRequest>(_SendLocalListRequest_QNAME, SendLocalListRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "chargeBoxIdentity")
    public JAXBElement<String> createChargeBoxIdentity(String value) {
        return new JAXBElement<String>(_ChargeBoxIdentity_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataTransferResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "dataTransferResponse")
    public JAXBElement<DataTransferResponse> createDataTransferResponse(DataTransferResponse value) {
        return new JAXBElement<DataTransferResponse>(_DataTransferResponse_QNAME, DataTransferResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClearCacheRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "clearCacheRequest")
    public JAXBElement<ClearCacheRequest> createClearCacheRequest(ClearCacheRequest value) {
        return new JAXBElement<ClearCacheRequest>(_ClearCacheRequest_QNAME, ClearCacheRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateFirmwareResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "updateFirmwareResponse")
    public JAXBElement<UpdateFirmwareResponse> createUpdateFirmwareResponse(UpdateFirmwareResponse value) {
        return new JAXBElement<UpdateFirmwareResponse>(_UpdateFirmwareResponse_QNAME, UpdateFirmwareResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://Ocpp/Cp/2012/06/", name = "resetResponse")
    public JAXBElement<ResetResponse> createResetResponse(ResetResponse value) {
        return new JAXBElement<ResetResponse>(_ResetResponse_QNAME, ResetResponse.class, null, value);
    }

}
