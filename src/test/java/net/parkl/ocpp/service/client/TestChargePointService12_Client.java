package net.parkl.ocpp.service.client;

import de.rwth.idsg.steve.ocpp.ChargePointService12_Invoker;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.ChangeAvailabilityTask;
import de.rwth.idsg.steve.ocpp.task.ChangeConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.ClearCacheTask;
import de.rwth.idsg.steve.ocpp.task.GetDiagnosticsTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStartTransactionTask;
import de.rwth.idsg.steve.ocpp.task.RemoteStopTransactionTask;
import de.rwth.idsg.steve.ocpp.task.ResetTask;
import de.rwth.idsg.steve.ocpp.task.UnlockConnectorTask;
import de.rwth.idsg.steve.ocpp.task.UpdateFirmwareTask;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.service.BackgroundService;
import de.rwth.idsg.steve.service.IChargePointService12_Client;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetDiagnosticsParams;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.ResetParams;
import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;
import de.rwth.idsg.steve.web.dto.ocpp.UpdateFirmwareParams;
import net.parkl.ocpp.service.chargepoint.TestChargePoint;
import net.parkl.ocpp.service.cluster.PersistentTaskService;
import net.parkl.ocpp.service.cluster.PersistentTaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;

@Service
@Qualifier("ChargePointService12_Client")
@Primary
public class TestChargePointService12_Client implements IChargePointService12_Client {
	@Autowired
	protected TestChargePoint invoker;
	
	@Autowired protected ScheduledExecutorService executorService;
    @Autowired protected TaskStore taskStore;
	@Autowired protected PersistentTaskService persistentTaskService;

    
    protected OcppVersion getVersion() {
        return OcppVersion.V_12;
    }


	protected ChargePointService12_Invoker getOcpp12Invoker() {
		return invoker;
	}

	public int changeAvailability(ChangeAvailabilityParams params) {
		ChangeAvailabilityTask task = new ChangeAvailabilityTask(persistentTaskService, getVersion(), params);

		Integer taskId = taskStore.add(task);

		BackgroundService.with(executorService).forEach(task.getParams().getChargePointSelectList())
				.execute(c -> getOcpp12Invoker().changeAvailability(c, task));

		return taskId;
	}

	public int changeConfiguration(ChangeConfigurationParams params) {
		ChangeConfigurationTask task = new ChangeConfigurationTask(persistentTaskService, getVersion(), params);

		Integer taskId = taskStore.add(task);

		BackgroundService.with(executorService).forEach(task.getParams().getChargePointSelectList())
				.execute(c -> getOcpp12Invoker().changeConfiguration(c, task));

		return taskId;
	}

	public int clearCache(MultipleChargePointSelect params) {
		ClearCacheTask task = new ClearCacheTask(persistentTaskService, getVersion(), params);

		Integer taskId = taskStore.add(task);

		BackgroundService.with(executorService).forEach(task.getParams().getChargePointSelectList())
				.execute(c -> getOcpp12Invoker().clearCache(c, task));

		return taskId;
	}

	public int getDiagnostics(GetDiagnosticsParams params) {
		GetDiagnosticsTask task = new GetDiagnosticsTask(persistentTaskService, getVersion(), params);

		Integer taskId = taskStore.add(task);

		BackgroundService.with(executorService).forEach(task.getParams().getChargePointSelectList())
				.execute(c -> getOcpp12Invoker().getDiagnostics(c, task));

		return taskId;
	}

	public int reset(ResetParams params) {
		ResetTask task = new ResetTask(persistentTaskService, getVersion(), params);

		Integer taskId = taskStore.add(task);

		BackgroundService.with(executorService).forEach(task.getParams().getChargePointSelectList())
				.execute(c -> getOcpp12Invoker().reset(c, task));

		return taskId;
	}

	public int updateFirmware(UpdateFirmwareParams params) {
		UpdateFirmwareTask task = new UpdateFirmwareTask(persistentTaskService, getVersion(), params);

		Integer taskId = taskStore.add(task);

		BackgroundService.with(executorService).forEach(task.getParams().getChargePointSelectList())
				.execute(c -> getOcpp12Invoker().updateFirmware(c, task));

		return taskId;
	}

	// -------------------------------------------------------------------------
	// Single Execution - since OCPP 1.2
	// -------------------------------------------------------------------------

	public int remoteStartTransaction(RemoteStartTransactionParams params) {
		RemoteStartTransactionTask task = new RemoteStartTransactionTask(persistentTaskService, getVersion(), params);

		Integer taskId = taskStore.add(task);

		BackgroundService.with(executorService).forFirst(task.getParams().getChargePointSelectList())
				.execute(c -> getOcpp12Invoker().remoteStartTransaction(c, task));

		return taskId;
	}

	public int remoteStopTransaction(RemoteStopTransactionParams params) {
		RemoteStopTransactionTask task = new RemoteStopTransactionTask(persistentTaskService, getVersion(), params);

		Integer taskId = taskStore.add(task);

		BackgroundService.with(executorService).forFirst(task.getParams().getChargePointSelectList())
				.execute(c -> getOcpp12Invoker().remoteStopTransaction(c, task));

		return taskId;
	}

	public int unlockConnector(UnlockConnectorParams params) {
		UnlockConnectorTask task = new UnlockConnectorTask(persistentTaskService, getVersion(), params);

		Integer taskId = taskStore.add(task);

		BackgroundService.with(executorService).forFirst(task.getParams().getChargePointSelectList())
				.execute(c -> getOcpp12Invoker().unlockConnector(c, task));

		return taskId;
	}


}
