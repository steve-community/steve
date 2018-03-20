package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.*;
import de.rwth.idsg.steve.ocpp.task.TriggerMessageTask;
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
@Slf4j
@Service
@Qualifier("ChargePointService16_Client")
public class ChargePointService16_Client extends ChargePointService15_Client {

    @Autowired private ChargePointService16_InvokerImpl invoker16;

    @Override
    protected OcppVersion getVersion() {
        return OcppVersion.V_16;
    }

    @Override
    protected ChargePointService12_Invoker getOcpp12Invoker() {
        return invoker16;
    }

    @Override
    protected ChargePointService15_Invoker getOcpp15Invoker() {
        return invoker16;
    }

    protected ChargePointService16_Invoker getOcpp16Invoker() {
        return invoker16;
    }

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.6
    // -------------------------------------------------------------------------

    public int triggerMessage(TriggerMessageParams params) {
        TriggerMessageTask task = new TriggerMessageTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp16Invoker().triggerMessage(c, task));

        return requestTaskStore.add(task);
    }
}
