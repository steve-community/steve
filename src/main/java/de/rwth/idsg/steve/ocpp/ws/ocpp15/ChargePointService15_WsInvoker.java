package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import de.rwth.idsg.steve.ocpp.ChargePointService15_Invoker;
import de.rwth.idsg.steve.ocpp.task.CancelReservationTask;
import de.rwth.idsg.steve.ocpp.task.ChangeAvailabilityTask;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.ClearCacheTask;
import de.rwth.idsg.steve.ocpp.task.DataTransferTask;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.GetDiagnosticsTask;
import de.rwth.idsg.steve.ocpp.task.GetLocalListVersionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStartTransactionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStopTransactionTask;
import de.rwth.idsg.steve.ocpp.task.ReserveNowTask;
import de.rwth.idsg.steve.ocpp.task.ResetTask;
import de.rwth.idsg.steve.ocpp.task.SendLocalListTask;
import de.rwth.idsg.steve.ocpp.task.UnlockConnectorTask;
import de.rwth.idsg.steve.ocpp.task.UpdateFirmwareTask;
import de.rwth.idsg.steve.ocpp.ws.AbstractChargePointServiceInvoker;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
@Component
public class ChargePointService15_WsInvoker
        extends AbstractChargePointServiceInvoker
        implements ChargePointService15_Invoker {

    @Autowired private Ocpp15TypeStore typeStore;
    @Autowired private Ocpp15WebSocketEndpoint endpoint;

    @PostConstruct
    public void init() {
        super.setTypeStore(typeStore);
        super.setEndpoint(endpoint);
    }


    @Override
    public void dataTransfer(ChargePointSelect cp, DataTransferTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void getConfiguration(ChargePointSelect cp, GetConfigurationTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void sendLocalList(ChargePointSelect cp, SendLocalListTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void reserveNow(ChargePointSelect cp, ReserveNowTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void cancelReservation(ChargePointSelect cp, CancelReservationTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void reset(ChargePointSelect cp, ResetTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void clearCache(ChargePointSelect cp, ClearCacheTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task) {
        runPipeline(cp, task);
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionTask task) {
        runPipeline(cp, task);
    }
}
