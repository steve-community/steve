package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
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

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
public interface ChargePointService12_Invoker {

    void reset(ChargePointSelect cp, ResetRequest request,
               OcppResponseHandler<ResetResponse> handler);

    void clearCache(ChargePointSelect cp, ClearCacheRequest request,
                    OcppResponseHandler<ClearCacheResponse> handler);

    void getDiagnostics(ChargePointSelect cp, GetDiagnosticsRequest request,
                        OcppResponseHandler<GetDiagnosticsResponse> handler);

    void updateFirmware(ChargePointSelect cp, UpdateFirmwareRequest request,
                        OcppResponseHandler<UpdateFirmwareResponse> handler);

    void unlockConnector(ChargePointSelect cp, UnlockConnectorRequest request,
                         OcppResponseHandler<UnlockConnectorResponse> handler);

    void changeAvailability(ChargePointSelect cp, ChangeAvailabilityRequest request,
                            OcppResponseHandler<ChangeAvailabilityResponse> handler);

    void changeConfiguration(ChargePointSelect cp, ChangeConfigurationRequest request,
                             OcppResponseHandler<ChangeConfigurationResponse> handler);

    void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionRequest request,
                                OcppResponseHandler<RemoteStartTransactionResponse> handler);

    void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionRequest request,
                               OcppResponseHandler<RemoteStopTransactionResponse> handler);
}
