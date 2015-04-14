package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.ocpp.ChargePointService15_Invoker;
import de.rwth.idsg.steve.ocpp.soap.ChargePointService15_SoapInvoker;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.ChargePointService15_WsInvoker;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import ocpp.cp._2012._06.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 07.04.2015
 */
@Service
public class ChargePointService15_Dispatcher implements ChargePointService15_Invoker {

    @Autowired private ChargePointService15_SoapInvoker soapInvoker;
    @Autowired private ChargePointService15_WsInvoker wsInvoker;

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
    public void dataTransfer(ChargePointSelect cp, DataTransferRequest request,
                             OcppResponseHandler<DataTransferResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.dataTransfer(cp, request, handler);
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
    public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsRequest request,
                               OcppResponseHandler<GetDiagnosticsResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.getDiagnostics(cp, request, handler);
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
    public void getConfiguration(ChargePointSelect cp, GetConfigurationRequest request,
                                 OcppResponseHandler<GetConfigurationResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.getConfiguration(cp, request, handler);
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
    public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityRequest request,
                                   OcppResponseHandler<ChangeAvailabilityResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.changeAvailability(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }

    @Override
    public void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionRequest request,
                                    OcppResponseHandler<GetLocalListVersionResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.getLocalListVersion(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }

    @Override
    public void sendLocalList(ChargePointSelect cp, SendLocalListRequest request,
                              OcppResponseHandler<SendLocalListResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.sendLocalList(cp, request, handler);
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

    @Override
    public void reserveNow(ChargePointSelect cp, ReserveNowRequest request,
                           OcppResponseHandler<ReserveNowResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.reserveNow(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }

    @Override
    public void cancelReservation(ChargePointSelect cp, CancelReservationRequest request,
                                  OcppResponseHandler<CancelReservationResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.cancelReservation(cp, request, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), request, handler);
        }
    }
}
