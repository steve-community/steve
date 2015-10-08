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
    public void reset(ChargePointSelect cp, ResetRequest request,
                      OcppResponseHandler<ResetResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.reset(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }

    @Override
    public void clearCache(ChargePointSelect cp, ClearCacheRequest request,
                           OcppResponseHandler<ClearCacheResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.clearCache(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsRequest request,
                               OcppResponseHandler<GetDiagnosticsResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.getDiagnostics(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }

    @Override
    public void updateFirmware(ChargePointSelect cp, UpdateFirmwareRequest request,
                               OcppResponseHandler<UpdateFirmwareResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.updateFirmware(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }

    @Override
    public void unlockConnector(ChargePointSelect cp, UnlockConnectorRequest request,
                                OcppResponseHandler<UnlockConnectorResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.unlockConnector(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }

    @Override
    public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityRequest request,
                                   OcppResponseHandler<ChangeAvailabilityResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.changeAvailability(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationRequest request,
                                    OcppResponseHandler<ChangeConfigurationResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.changeConfiguration(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionRequest request,
                                       OcppResponseHandler<RemoteStartTransactionResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.remoteStartTransaction(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionRequest request,
                                      OcppResponseHandler<RemoteStopTransactionResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.remoteStopTransaction(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }
}
