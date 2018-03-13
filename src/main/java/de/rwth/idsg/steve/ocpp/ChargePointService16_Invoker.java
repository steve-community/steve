/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import ocpp.cp._2015._10.*;

/**
 *
 * @author david
 */
public interface ChargePointService16_Invoker {
    void reset(ChargePointSelect cp,
               OcppResponseHandler<ResetRequest, ResetResponse> handler);

    void clearCache(ChargePointSelect cp,
                    OcppResponseHandler<ClearCacheRequest, ClearCacheResponse> handler);

    void dataTransfer(ChargePointSelect cp,
                      OcppResponseHandler<DataTransferRequest, DataTransferResponse> handler);

    void updateFirmware(ChargePointSelect cp,
                        OcppResponseHandler<UpdateFirmwareRequest, UpdateFirmwareResponse> handler);

    void getDiagnostics(ChargePointSelect cp,
                        OcppResponseHandler<GetDiagnosticsRequest, GetDiagnosticsResponse> handler);

    void unlockConnector(ChargePointSelect cp,
                         OcppResponseHandler<UnlockConnectorRequest, UnlockConnectorResponse> handler);

    void getConfiguration(ChargePointSelect cp,
                          OcppResponseHandler<GetConfigurationRequest, GetConfigurationResponse> handler);

    void changeConfiguration(ChargePointSelect cp,
                             OcppResponseHandler<ChangeConfigurationRequest, ChangeConfigurationResponse> handler);

    void changeAvailability(ChargePointSelect cp,
                            OcppResponseHandler<ChangeAvailabilityRequest, ChangeAvailabilityResponse> handler);

    void getLocalListVersion(ChargePointSelect cp,
                             OcppResponseHandler<GetLocalListVersionRequest, GetLocalListVersionResponse> handler);

    void sendLocalList(ChargePointSelect cp,
                       OcppResponseHandler<SendLocalListRequest, SendLocalListResponse> handler);

    void remoteStartTransaction(ChargePointSelect cp,
                                OcppResponseHandler<RemoteStartTransactionRequest,
                                                    RemoteStartTransactionResponse> handler);

    void remoteStopTransaction(ChargePointSelect cp,
                               OcppResponseHandler<RemoteStopTransactionRequest,
                                                   RemoteStopTransactionResponse> handler);

    void reserveNow(ChargePointSelect cp,
                    OcppResponseHandler<ReserveNowRequest, ReserveNowResponse> handler);

    void cancelReservation(ChargePointSelect cp,
                           OcppResponseHandler<CancelReservationRequest, CancelReservationResponse> handler);

    void triggerMessage(ChargePointSelect cp,
                           OcppResponseHandler<TriggerMessageRequest, TriggerMessageResponse> handler);

    void setChargingProfile(ChargePointSelect cp,
                        OcppResponseHandler<SetChargingProfileRequest, SetChargingProfileResponse> handler);

    void getCompositeSchedule(ChargePointSelect cp,
                            OcppResponseHandler<GetCompositeScheduleRequest, GetCompositeScheduleResponse> handler);

    void clearChargingProfile(ChargePointSelect cp,
                              OcppResponseHandler<ClearChargingProfileRequest, ClearChargingProfileResponse> handler);
}
