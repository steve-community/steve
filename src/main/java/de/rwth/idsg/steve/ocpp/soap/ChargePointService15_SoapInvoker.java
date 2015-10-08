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
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("ocpp15")
    private JaxWsProxyFactoryBean factory;

    private static final Object LOCK = new Object();

    private ChargePointService create(String endpointAddress) {
        // Should concurrency really be a concern?
        synchronized (LOCK) {
            factory.setAddress(endpointAddress);
            return (ChargePointService) factory.create();
        }
    }

    @Override
    public void reset(ChargePointSelect cp, ResetRequest request,
                      OcppResponseHandler<ResetResponse> handler) {
        create(cp.getEndpointAddress()).resetAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void clearCache(ChargePointSelect cp, ClearCacheRequest request,
                           OcppResponseHandler<ClearCacheResponse> handler) {
        create(cp.getEndpointAddress()).clearCacheAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void dataTransfer(ChargePointSelect cp, DataTransferRequest request,
                             OcppResponseHandler<DataTransferResponse> handler) {
        create(cp.getEndpointAddress()).dataTransferAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void updateFirmware(ChargePointSelect cp, UpdateFirmwareRequest request,
                               OcppResponseHandler<UpdateFirmwareResponse> handler) {
        create(cp.getEndpointAddress()).updateFirmwareAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsRequest request,
                               OcppResponseHandler<GetDiagnosticsResponse> handler) {
        create(cp.getEndpointAddress()).getDiagnosticsAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void unlockConnector(ChargePointSelect cp, UnlockConnectorRequest request,
                                OcppResponseHandler<UnlockConnectorResponse> handler) {
        create(cp.getEndpointAddress()).unlockConnectorAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void getConfiguration(ChargePointSelect cp, GetConfigurationRequest request,
                                 OcppResponseHandler<GetConfigurationResponse> handler) {
        create(cp.getEndpointAddress()).getConfigurationAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationRequest request,
                                    OcppResponseHandler<ChangeConfigurationResponse> handler) {
        create(cp.getEndpointAddress()).changeConfigurationAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityRequest request,
                                   OcppResponseHandler<ChangeAvailabilityResponse> handler) {
        create(cp.getEndpointAddress()).changeAvailabilityAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionRequest request,
                                    OcppResponseHandler<GetLocalListVersionResponse> handler) {
        create(cp.getEndpointAddress()).getLocalListVersionAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void sendLocalList(ChargePointSelect cp, SendLocalListRequest request,
                              OcppResponseHandler<SendLocalListResponse> handler) {
        create(cp.getEndpointAddress()).sendLocalListAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionRequest request,
                                       OcppResponseHandler<RemoteStartTransactionResponse> handler) {
        create(cp.getEndpointAddress()).remoteStartTransactionAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionRequest request,
                                      OcppResponseHandler<RemoteStopTransactionResponse> handler) {
        create(cp.getEndpointAddress()).remoteStopTransactionAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void reserveNow(ChargePointSelect cp, ReserveNowRequest request,
                           OcppResponseHandler<ReserveNowResponse> handler) {
        create(cp.getEndpointAddress()).reserveNowAsync(request, cp.getChargeBoxId(), handler);
    }

    @Override
    public void cancelReservation(ChargePointSelect cp, CancelReservationRequest request,
                                  OcppResponseHandler<CancelReservationResponse> handler) {
        create(cp.getEndpointAddress()).cancelReservationAsync(request, cp.getChargeBoxId(), handler);
    }
}
