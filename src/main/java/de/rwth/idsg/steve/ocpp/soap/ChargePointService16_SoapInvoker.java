/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.ocpp.soap;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.ocpp.ChargePointService16_Invoker;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import ocpp.cp._2015._10.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author david
 */
@Service
public class ChargePointService16_SoapInvoker implements ChargePointService16_Invoker {

    @Autowired private ClientProvider clientProvider;

    private ChargePointService create(String endpointAddress) {
        return clientProvider.getForOcpp16(endpointAddress);
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
    public void clearChargingProfile(ChargePointSelect cp,
                             OcppResponseHandler<ClearChargingProfileRequest, ClearChargingProfileResponse> handler) {
        create(cp.getEndpointAddress()).clearChargingProfileAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
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
    public void setChargingProfile(ChargePointSelect cp,
                                OcppResponseHandler<SetChargingProfileRequest, SetChargingProfileResponse> handler) {
        create(cp.getEndpointAddress()).setChargingProfileAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
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

    @Override
    public void triggerMessage(ChargePointSelect cp,
                                  OcppResponseHandler<TriggerMessageRequest, TriggerMessageResponse> handler) {
        create(cp.getEndpointAddress()).triggerMessageAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }

    @Override
    public void getCompositeSchedule(ChargePointSelect cp,
                               OcppResponseHandler<GetCompositeScheduleRequest, GetCompositeScheduleResponse> handler) {
        create(cp.getEndpointAddress()).getCompositeScheduleAsync(handler.getRequest(), cp.getChargeBoxId(), handler);
    }
}
