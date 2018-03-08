package de.rwth.idsg.steve.ocpp.soap;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.ocpp.ChargePointService12_Invoker;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import ocpp.cp._2010._08.ChangeAvailabilityRequest;
import ocpp.cp._2010._08.ChangeAvailabilityResponse;
import ocpp.cp._2010._08.ChangeConfigurationRequest;
import ocpp.cp._2010._08.ChangeConfigurationResponse;
import ocpp.cp._2010._08.ChargePointService;
import ocpp.cp._2010._08.ClearCacheRequest;
import ocpp.cp._2010._08.ClearCacheResponse;
import ocpp.cp._2010._08.GetDiagnosticsRequest;
import ocpp.cp._2010._08.GetDiagnosticsResponse;
import ocpp.cp._2010._08.RemoteStartTransactionRequest;
import ocpp.cp._2010._08.RemoteStartTransactionResponse;
import ocpp.cp._2010._08.RemoteStopTransactionRequest;
import ocpp.cp._2010._08.RemoteStopTransactionResponse;
import ocpp.cp._2010._08.ResetRequest;
import ocpp.cp._2010._08.ResetResponse;
import ocpp.cp._2010._08.UnlockConnectorRequest;
import ocpp.cp._2010._08.UnlockConnectorResponse;
import ocpp.cp._2010._08.UpdateFirmwareRequest;
import ocpp.cp._2010._08.UpdateFirmwareResponse;
import org.springframework.stereotype.Service;

/**
 * This class has methods to dynamically create and call SOAP clients. Since there are multiple charge points and
 * their endpoint addresses vary, the clients need to be created dynamically.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
@Service
public class ChargePointService12_SoapInvoker implements ChargePointService12_Invoker {

    private static ChargePointService create(String endpointAddress) {
        return ClientProvider.getForOcpp12(endpointAddress);
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
    public void getDiagnostics(ChargePointSelect cp,
                               OcppResponseHandler<GetDiagnosticsRequest, GetDiagnosticsResponse> handler) {
        create(cp.getEndpointAddress()).getDiagnosticsAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void updateFirmware(ChargePointSelect cp,
                               OcppResponseHandler<UpdateFirmwareRequest, UpdateFirmwareResponse> handler) {
        create(cp.getEndpointAddress()).updateFirmwareAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void unlockConnector(ChargePointSelect cp,
                                OcppResponseHandler<UnlockConnectorRequest, UnlockConnectorResponse> handler) {
        create(cp.getEndpointAddress()).unlockConnectorAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void changeAvailability(ChargePointSelect cp,
                                   OcppResponseHandler<ChangeAvailabilityRequest,
                                                       ChangeAvailabilityResponse> handler) {
        create(cp.getEndpointAddress()).changeAvailabilityAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp,
                                    OcppResponseHandler<ChangeConfigurationRequest,
                                                        ChangeConfigurationResponse> handler) {
        create(cp.getEndpointAddress()).changeConfigurationAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
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
}
