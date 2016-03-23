package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.ocpp.ChargePointService12_Invoker;
import de.rwth.idsg.steve.ocpp.soap.ChargePointService12_SoapInvoker;
import de.rwth.idsg.steve.ocpp.ws.ocpp12.ChargePointService12_WsInvoker;
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
import org.springframework.stereotype.Service;

/**
 * Depending on the transport type, dispatches the OCPP requests to charge points
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 07.04.2015
 */
@Service
public class ChargePointService12_Dispatcher implements ChargePointService12_Invoker {

    @Autowired private ChargePointService12_SoapInvoker soapInvoker;
    @Autowired private ChargePointService12_WsInvoker wsInvoker;

    @Override
    public void reset(ChargePointSelect cp,
                      OcppResponseHandler<ResetRequest, ResetResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.reset(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void clearCache(ChargePointSelect cp,
                           OcppResponseHandler<ClearCacheRequest, ClearCacheResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.clearCache(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp,
                               OcppResponseHandler<GetDiagnosticsRequest, GetDiagnosticsResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.getDiagnostics(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void updateFirmware(ChargePointSelect cp,
                               OcppResponseHandler<UpdateFirmwareRequest, UpdateFirmwareResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.updateFirmware(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void unlockConnector(ChargePointSelect cp,
                                OcppResponseHandler<UnlockConnectorRequest, UnlockConnectorResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.unlockConnector(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void changeAvailability(ChargePointSelect cp,
                                   OcppResponseHandler<ChangeAvailabilityRequest,
                                                       ChangeAvailabilityResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.changeAvailability(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp,
                                    OcppResponseHandler<ChangeConfigurationRequest,
                                                        ChangeConfigurationResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.changeConfiguration(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp,
                                       OcppResponseHandler<RemoteStartTransactionRequest,
                                                           RemoteStartTransactionResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.remoteStartTransaction(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp,
                                      OcppResponseHandler<RemoteStopTransactionRequest,
                                                          RemoteStopTransactionResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.remoteStopTransaction(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }
}
