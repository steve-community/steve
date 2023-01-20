package net.parkl.ocpp.service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.rwth.idsg.steve.ocpp.ChargePointService16_Invoker;
import de.rwth.idsg.steve.ocpp.task.TriggerMessageTask;
import de.rwth.idsg.steve.service.BackgroundService;
import de.rwth.idsg.steve.service.IChargePointService16_Client;
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;

@Service
@Qualifier("ChargePointService16_Client")
public class TestChargePointService16_Client extends TestChargePointService15_Client implements IChargePointService16_Client {
	protected ChargePointService16_Invoker getOcpp16Invoker() {
		return invoker;
	}

	 // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.6
    // -------------------------------------------------------------------------

    public int triggerMessage(TriggerMessageParams params) {
        TriggerMessageTask task = new TriggerMessageTask(persistentTaskService, getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp16Invoker().triggerMessage(c, task));

        return taskStore.add(task);
    }
}
