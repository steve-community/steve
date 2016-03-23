package de.rwth.idsg.steve.ocpp.soap;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.ocpp.ChargePointService15_Invoker;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import ocpp.cp._2012._06.CancelReservationRequest;
import ocpp.cp._2012._06.CancelReservationResponse;
import ocpp.cp._2012._06.ChangeAvailabilityRequest;
import ocpp.cp._2012._06.ChangeAvailabilityResponse;
import ocpp.cp._2012._06.ChangeConfigurationRequest;
import ocpp.cp._2012._06.ChangeConfigurationResponse;
import ocpp.cp._2012._06.ChargePointService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class has methods to dynamically create and call SOAP clients. Since there are multiple charge points and
 * their endpoint addresses vary, the clients need to be created dynamically.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
@Service
public class ChargePointService15_SoapInvoker implements ChargePointService15_Invoker {

    @Autowired private ClientProvider clientProvider;

    private ChargePointService create(String endpointAddress) {
        return clientProvider.getForOcpp15(endpointAddress);
    }

    @Override
    public void reset(ChargePointSelect cp,
                      OcppResponseHandler<ResetRequest, ResetResponse> handler) {
        create(cp.getEndpointAddress()).resetAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void clearCache(ChargePointSelect cp,
                           OcppResponseHandler<ClearCacheRequest, ClearCacheResponse> handler) {
        create(cp.getEndpointAddress()).clearCacheAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void dataTransfer(ChargePointSelect cp,
                             OcppResponseHandler<DataTransferRequest, DataTransferResponse> handler) {
        create(cp.getEndpointAddress()).dataTransferAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void updateFirmware(ChargePointSelect cp,
                               OcppResponseHandler<UpdateFirmwareRequest, UpdateFirmwareResponse> handler) {
        create(cp.getEndpointAddress()).updateFirmwareAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp,
                               OcppResponseHandler<GetDiagnosticsRequest, GetDiagnosticsResponse> handler) {
        create(cp.getEndpointAddress()).getDiagnosticsAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void unlockConnector(ChargePointSelect cp,
                                OcppResponseHandler<UnlockConnectorRequest, UnlockConnectorResponse> handler) {
        create(cp.getEndpointAddress()).unlockConnectorAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void getConfiguration(ChargePointSelect cp,
                                 OcppResponseHandler<GetConfigurationRequest, GetConfigurationResponse> handler) {
        create(cp.getEndpointAddress()).getConfigurationAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp,
                                    OcppResponseHandler<ChangeConfigurationRequest,
                                                        ChangeConfigurationResponse> handler) {
        create(cp.getEndpointAddress()).changeConfigurationAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void changeAvailability(ChargePointSelect cp,
                                   OcppResponseHandler<ChangeAvailabilityRequest,
                                                       ChangeAvailabilityResponse> handler) {
        create(cp.getEndpointAddress()).changeAvailabilityAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void getLocalListVersion(ChargePointSelect cp,
                                    OcppResponseHandler<GetLocalListVersionRequest,
                                                        GetLocalListVersionResponse> handler) {
        create(cp.getEndpointAddress()).getLocalListVersionAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void sendLocalList(ChargePointSelect cp,
                              OcppResponseHandler<SendLocalListRequest, SendLocalListResponse> handler) {
        create(cp.getEndpointAddress()).sendLocalListAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp,
                                       OcppResponseHandler<RemoteStartTransactionRequest,
                                                           RemoteStartTransactionResponse> handler) {
        create(cp.getEndpointAddress()).remoteStartTransactionAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp,
                                      OcppResponseHandler<RemoteStopTransactionRequest,
                                                          RemoteStopTransactionResponse> handler) {
        create(cp.getEndpointAddress()).remoteStopTransactionAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void reserveNow(ChargePointSelect cp,
                           OcppResponseHandler<ReserveNowRequest, ReserveNowResponse> handler) {
        create(cp.getEndpointAddress()).reserveNowAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void cancelReservation(ChargePointSelect cp,
                                  OcppResponseHandler<CancelReservationRequest, CancelReservationResponse> handler) {
        create(cp.getEndpointAddress()).cancelReservationAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }
}
