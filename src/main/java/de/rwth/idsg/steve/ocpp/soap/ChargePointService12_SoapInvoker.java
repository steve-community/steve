package de.rwth.idsg.steve.ocpp.soap;

import de.rwth.idsg.steve.ocpp.ChargePointService12_Invoker;
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
import ocpp.cp._2010._08.ChargePointService;
import org.springframework.stereotype.Service;

/**
 * This class has methods to dynamically create and call SOAP clients. Since there are multiple charge points and
 * their endpoint addresses vary, the clients need to be created dynamically.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
@Service
public class ChargePointService12_SoapInvoker implements ChargePointService12_Invoker {

    private static ChargePointService create(String endpointAddress) {
        return ClientProvider.getForOcpp12(endpointAddress);
    }

    @Override
    public void reset(ChargePointSelect cp, ResetTask task) {
        create(cp.getEndpointAddress()).resetAsync(
                task.getOcpp12Request(), cp.getChargeBoxId(), task.getOcpp12Handler(cp.getChargeBoxId()));
    }

    @Override
    public void clearCache(ChargePointSelect cp, ClearCacheTask task) {
        create(cp.getEndpointAddress()).clearCacheAsync(
                task.getOcpp12Request(), cp.getChargeBoxId(), task.getOcpp12Handler(cp.getChargeBoxId()));
    }

    @Override
    public void getDiagnostics(ChargePointSelect cp, GetDiagnosticsTask task) {
        create(cp.getEndpointAddress()).getDiagnosticsAsync(
                task.getOcpp12Request(), cp.getChargeBoxId(), task.getOcpp12Handler(cp.getChargeBoxId()));
    }

    @Override
    public void updateFirmware(ChargePointSelect cp, UpdateFirmwareTask task) {
        create(cp.getEndpointAddress()).updateFirmwareAsync(
                task.getOcpp12Request(), cp.getChargeBoxId(), task.getOcpp12Handler(cp.getChargeBoxId()));
    }

    @Override
    public void unlockConnector(ChargePointSelect cp, UnlockConnectorTask task) {
        create(cp.getEndpointAddress()).unlockConnectorAsync(
                task.getOcpp12Request(), cp.getChargeBoxId(), task.getOcpp12Handler(cp.getChargeBoxId()));
    }

    @Override
    public void changeAvailability(ChargePointSelect cp, ChangeAvailabilityTask task) {
        create(cp.getEndpointAddress()).changeAvailabilityAsync(
                task.getOcpp12Request(), cp.getChargeBoxId(), task.getOcpp12Handler(cp.getChargeBoxId()));
    }

    @Override
    public void changeConfiguration(ChargePointSelect cp, ChangeConfigurationTask task) {
        create(cp.getEndpointAddress()).changeConfigurationAsync(
                task.getOcpp12Request(), cp.getChargeBoxId(), task.getOcpp12Handler(cp.getChargeBoxId()));
    }

    @Override
    public void remoteStartTransaction(ChargePointSelect cp, RemoteStartTransactionTask task) {
        create(cp.getEndpointAddress()).remoteStartTransactionAsync(
                task.getOcpp12Request(), cp.getChargeBoxId(), task.getOcpp12Handler(cp.getChargeBoxId()));
    }

    @Override
    public void remoteStopTransaction(ChargePointSelect cp, RemoteStopTransactionTask task) {
        create(cp.getEndpointAddress()).remoteStopTransactionAsync(
                task.getOcpp12Request(), cp.getChargeBoxId(), task.getOcpp12Handler(cp.getChargeBoxId()));
    }

}
