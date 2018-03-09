package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.steve.ocpp.task.ChangeAvailabilityTask;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.ClearCacheTask;
import de.rwth.idsg.steve.ocpp.task.GetDiagnosticsTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStartTransactionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStopTransactionTask;
import de.rwth.idsg.steve.ocpp.task.ResetTask;
import de.rwth.idsg.steve.ocpp.task.UnlockConnectorTask;
import de.rwth.idsg.steve.ocpp.task.UpdateFirmwareTask;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
public interface ChargePointService12_Invoker {

    void reset(ChargePointSelect cp, ResetTask task);

    void clearCache(ChargePointSelect cp, ClearCacheTask task);

    void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task);

    void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task);

    void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task);

    void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task);

    void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task);

    void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task);

    void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionTask task);
}
