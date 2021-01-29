package net.parkl.ocpp.service.chargepoint;

import de.rwth.idsg.steve.ocpp.ChargePointService16_Invoker;
import de.rwth.idsg.steve.ocpp.task.*;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.CentralSystemService16_Service;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp.SendLocalListParams;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.GetLocalListVersionResponse;
import ocpp.cp._2015._10.RemoteStartStopStatus;
import ocpp.cp._2015._10.RemoteStartTransactionResponse;
import ocpp.cp._2015._10.RemoteStopTransactionResponse;
import ocpp.cp._2015._10.ResetResponse;
import ocpp.cp._2015._10.ResetStatus;
import ocpp.cp._2015._10.SendLocalListResponse;
import ocpp.cp._2015._10.UpdateStatus;
import ocpp.cs._2015._10.Reason;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StopTransactionRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Getter
@Setter
public class TestChargePoint implements ChargePointService16_Invoker {

    @Autowired
    private CentralSystemService16_Service centralSystemService16_service;

    @Autowired
    private TaskExecutor taskExecutor;

    private String lastIdTag;

    private int consumptionStart;
    private int consumptionStop;

    @Override
    public void dataTransfer(ChargePointSelect cp, DataTransferTask task) {
        log.info("dataTransfer method called on test charge point invoker");
    }

    @Override
    public void getConfiguration(ChargePointSelect cp, GetConfigurationTask task) {
        log.info("getConfiguration method called on test charge point invoker");
    }

    @Override
    public void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionTask task) {
        task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
                new ResponseWrapper<>(createGetLocalListVersionResponse(task.getParams())));
    }

    @Override
    public void sendLocalList(ChargePointSelect cp, SendLocalListTask task) {
        task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
                new ResponseWrapper<>(createSendLocalListResponse(task.getParams())));
    }

    @Override
    public void reserveNow(ChargePointSelect cp, ReserveNowTask task) {
        log.info("reserveNow method called on test charge point invoker");
    }

    @Override
    public void cancelReservation(ChargePointSelect cp, CancelReservationTask task) {
        log.info("cancelReservation method called on test charge point invoker");
    }

    @Override
    public void reset(ChargePointSelect cp, ResetTask task) {
        task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
                new ResponseWrapper<>(createResetResponse(task.getParams())));
    }

    private ResetResponse createResetResponse(ResetParams params) {
        ResetResponse resp = new ResetResponse();
        resp.setStatus(ResetStatus.ACCEPTED);
        return resp;
    }

    private SendLocalListResponse createSendLocalListResponse(SendLocalListParams params) {
        SendLocalListResponse resp = new SendLocalListResponse();
        resp.setStatus(UpdateStatus.ACCEPTED);
        return resp;
    }

    private GetLocalListVersionResponse createGetLocalListVersionResponse(MultipleChargePointSelect params) {
        GetLocalListVersionResponse resp = new GetLocalListVersionResponse();
        resp.setListVersion(1);
        return resp;
    }

    @Override
    public void clearCache(ChargePointSelect cp, ClearCacheTask task) {
        log.info("clearCache method called on test charge point invoker");
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task) {
        log.info("getDiagnostics method called on test charge point invoker");
    }

    @Override
    public void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task) {
        log.info("updateFirmware method called on test charge point invoker");
    }

    @Override
    public void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task) {
        log.info("unlockConnector method called on test charge point invoker");
    }

    @Override
    public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task) {
        log.info("changeAvailability method called on test charge point invoker");
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task) {
        log.info("changeConfiguration method called on test charge point invoker");
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task) {
        task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
                new ResponseWrapper<>(createRemoteStartTransactionResponse(task.getParams())));

        taskExecutor.execute(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            StartTransactionRequest req = new StartTransactionRequest();
            req.setConnectorId(task.getParams().getConnectorId());
            req.setIdTag(task.getParams().getIdTag());
            req.setMeterStart(consumptionStart);
            req.setTimestamp(DateTime.now());

            centralSystemService16_service.startTransaction(req, cp.getChargeBoxId());
        });

        lastIdTag = task.getParams().getIdTag();
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionTask task) {
        task.getOcpp16Handler(cp.getChargeBoxId()).handleResponse(
                new ResponseWrapper<>(createRemoteStopTransactionResponse(task.getParams())));

        taskExecutor.execute(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            StopTransactionRequest req = new StopTransactionRequest();
            req.setTimestamp(DateTime.now());
            req.setMeterStop(consumptionStop);
            req.setReason(Reason.LOCAL);
            req.setTransactionId(task.getParams().getTransactionId());
            req.setIdTag(lastIdTag);
            centralSystemService16_service.stopTransaction(req, cp.getChargeBoxId());
        });
    }

    private RemoteStartTransactionResponse createRemoteStartTransactionResponse(RemoteStartTransactionParams params) {
        RemoteStartTransactionResponse resp = new RemoteStartTransactionResponse();
        resp.setStatus(RemoteStartStopStatus.ACCEPTED);
        return resp;
    }

    private RemoteStopTransactionResponse createRemoteStopTransactionResponse(RemoteStopTransactionParams params) {
        RemoteStopTransactionResponse resp = new RemoteStopTransactionResponse();
        resp.setStatus(RemoteStartStopStatus.ACCEPTED);
        return resp;
    }


    @Override
    public void clearChargingProfile(ChargePointSelect cp, ClearChargingProfileTask task) {
        log.info("clearChargingProfile method called on test charge point invoker");

    }

    @Override
    public void setChargingProfile(ChargePointSelect cp, SetChargingProfileTask task) {
        log.info("setChargingProfile method called on test charge point invoker");

    }

    @Override
    public void getCompositeSchedule(ChargePointSelect cp, GetCompositeScheduleTask task) {
        log.info("getCompositeSchedule method called on test charge point invoker");

    }

    @Override
    public void triggerMessage(ChargePointSelect cp, TriggerMessageTask task) {
        log.info("triggerMessage method called on test charge point invoker");
    }

}
