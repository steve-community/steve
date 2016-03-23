package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import ocpp.cp._2012._06.CancelReservationRequest;
import ocpp.cp._2012._06.CancelReservationResponse;
import ocpp.cp._2012._06.ChangeAvailabilityRequest;
import ocpp.cp._2012._06.ChangeAvailabilityResponse;
import ocpp.cp._2012._06.ChangeConfigurationRequest;
import ocpp.cp._2012._06.ChangeConfigurationResponse;
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

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
public interface ChargePointService15_Invoker {

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
}
