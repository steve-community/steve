/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.ocpp.ChargePointService16_Invoker;
import de.rwth.idsg.steve.ocpp.soap.ChargePointService16_SoapInvoker;
import de.rwth.idsg.steve.ocpp.ws.ocpp16.ChargePointService16_WsInvoker;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import ocpp.cp._2015._10.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author david
 */
@Service
public class ChargePointService16_Dispatcher implements ChargePointService16_Invoker {

    @Autowired private ChargePointService16_SoapInvoker soapInvoker;
    @Autowired private ChargePointService16_WsInvoker wsInvoker;

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
    public void dataTransfer(ChargePointSelect cp,
                             OcppResponseHandler<DataTransferRequest, DataTransferResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.dataTransfer(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void clearChargingProfile(ChargePointSelect cp,
                             OcppResponseHandler<ClearChargingProfileRequest, ClearChargingProfileResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.clearChargingProfile(cp, handler);
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
    public void getDiagnostics(ChargePointSelect cp,
                               OcppResponseHandler<GetDiagnosticsRequest, GetDiagnosticsResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.getDiagnostics(cp, handler);
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
    public void setChargingProfile(ChargePointSelect cp,
                                OcppResponseHandler<SetChargingProfileRequest, SetChargingProfileResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.setChargingProfile(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void getConfiguration(ChargePointSelect cp,
                                 OcppResponseHandler<GetConfigurationRequest, GetConfigurationResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.getConfiguration(cp, handler);
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
    public void getLocalListVersion(ChargePointSelect cp,
                                    OcppResponseHandler<GetLocalListVersionRequest,
                                                        GetLocalListVersionResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.getLocalListVersion(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void sendLocalList(ChargePointSelect cp,
                              OcppResponseHandler<SendLocalListRequest, SendLocalListResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.sendLocalList(cp, handler);
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

    @Override
    public void reserveNow(ChargePointSelect cp,
                           OcppResponseHandler<ReserveNowRequest, ReserveNowResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.reserveNow(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void cancelReservation(ChargePointSelect cp,
                                  OcppResponseHandler<CancelReservationRequest, CancelReservationResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.cancelReservation(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void triggerMessage(ChargePointSelect cp,
                                  OcppResponseHandler<TriggerMessageRequest, TriggerMessageResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.triggerMessage(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }

    @Override
    public void getCompositeSchedule(ChargePointSelect cp,
                               OcppResponseHandler<GetCompositeScheduleRequest, GetCompositeScheduleResponse> handler) {
        if (cp.isSoap()) {
            soapInvoker.getCompositeSchedule(cp, handler);
        } else {
            wsInvoker.runPipeline(cp.getChargeBoxId(), handler);
        }
    }
}