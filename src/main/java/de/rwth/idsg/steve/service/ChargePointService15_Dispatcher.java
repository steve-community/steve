package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.handler.OcppResponseHandler;
import de.rwth.idsg.steve.ocpp.ChargePointService15_Invoker;
import de.rwth.idsg.steve.ocpp.soap.ChargePointService15_SoapInvoker;
import de.rwth.idsg.steve.ocpp.ws.ocpp15.ChargePointService15_WsInvoker;
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
}
