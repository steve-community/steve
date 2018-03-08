package de.rwth.idsg.steve.ocpp.ws.ocpp12;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.ocpp.ChargePointService12_Invoker;
import de.rwth.idsg.steve.ocpp.ws.AbstractChargePointServiceInvoker;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import ocpp.cp._2010._08.ChangeAvailabilityRequest;
import ocpp.cp._2010._08.ChangeAvailabilityResponse;
import ocpp.cp._2010._08.ChangeConfigurationRequest;
import ocpp.cp._2010._08.ChangeConfigurationResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
@Component
public class ChargePointService12_WsInvoker
        extends AbstractChargePointServiceInvoker
        implements ChargePointService12_Invoker {

    @Autowired private Ocpp12TypeStore typeStore;
    @Autowired private Ocpp12WebSocketEndpoint endpoint;

    @PostConstruct
    public void init() {
        super.setTypeStore(typeStore);
        super.setEndpoint(endpoint);
    }

    @Override
    public void reset(ChargePointSelect cp, OcppResponseHandler<ResetRequest, ResetResponse> handler) {
        runPipeline(cp, handler);
    }

    @Override
    public void clearCache(ChargePointSelect cp, OcppResponseHandler<ClearCacheRequest, ClearCacheResponse> handler) {
        runPipeline(cp, handler);
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp,
                               OcppResponseHandler<GetDiagnosticsRequest, GetDiagnosticsResponse> handler) {
        runPipeline(cp, handler);
    }

    @Override
    public void updateFirmware(ChargePointSelect cp,
                               OcppResponseHandler<UpdateFirmwareRequest, UpdateFirmwareResponse> handler) {
        runPipeline(cp, handler);
    }

    @Override
    public void unlockConnector(ChargePointSelect cp,
                                OcppResponseHandler<UnlockConnectorRequest, UnlockConnectorResponse> handler) {
        runPipeline(cp, handler);
    }

    @Override
    public void changeAvailability(ChargePointSelect cp,
                                   OcppResponseHandler<ChangeAvailabilityRequest, ChangeAvailabilityResponse> handler) {
        runPipeline(cp, handler);
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp,
                                    OcppResponseHandler<ChangeConfigurationRequest, ChangeConfigurationResponse> handler) {
        runPipeline(cp, handler);
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp,
                                       OcppResponseHandler<RemoteStartTransactionRequest, RemoteStartTransactionResponse> handler) {
        runPipeline(cp, handler);
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp,
                                      OcppResponseHandler<RemoteStopTransactionRequest, RemoteStopTransactionResponse> handler) {
        runPipeline(cp, handler);
    }
}
