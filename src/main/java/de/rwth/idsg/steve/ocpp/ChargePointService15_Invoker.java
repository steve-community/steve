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

    void reset(ChargePointSelect cp, ResetRequest request,
               OcppResponseHandler<ResetResponse> handler);

    void clearCache(ChargePointSelect cp, ClearCacheRequest request,
                    OcppResponseHandler<ClearCacheResponse> handler);

    void dataTransfer(ChargePointSelect cp, DataTransferRequest request,
                      OcppResponseHandler<DataTransferResponse> handler);

    void updateFirmware(ChargePointSelect cp, UpdateFirmwareRequest request,
                        OcppResponseHandler<UpdateFirmwareResponse> handler);

    void getDiagnostics(ChargePointSelect cp, GetDiagnosticsRequest request,
                        OcppResponseHandler<GetDiagnosticsResponse> handler);

    void unlockConnector(ChargePointSelect cp, UnlockConnectorRequest request,
                         OcppResponseHandler<UnlockConnectorResponse> handler);

    void getConfiguration(ChargePointSelect cp, GetConfigurationRequest request,
                          OcppResponseHandler<GetConfigurationResponse> handler);

    void changeConfiguration(ChargePointSelect cp, ChangeConfigurationRequest request,
                             OcppResponseHandler<ChangeConfigurationResponse> handler);

    void changeAvailability(ChargePointSelect cp, ChangeAvailabilityRequest request,
                            OcppResponseHandler<ChangeAvailabilityResponse> handler);

    void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionRequest request,
                             OcppResponseHandler<GetLocalListVersionResponse> handler);

    void sendLocalList(ChargePointSelect cp, SendLocalListRequest request,
                       OcppResponseHandler<SendLocalListResponse> handler);

    void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionRequest request,
                                OcppResponseHandler<RemoteStartTransactionResponse> handler);

    void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionRequest request,
                               OcppResponseHandler<RemoteStopTransactionResponse> handler);

    void reserveNow(ChargePointSelect cp, ReserveNowRequest request,
                    OcppResponseHandler<ReserveNowResponse> handler);

    void cancelReservation(ChargePointSelect cp, CancelReservationRequest request,
                           OcppResponseHandler<CancelReservationResponse> handler);
}
