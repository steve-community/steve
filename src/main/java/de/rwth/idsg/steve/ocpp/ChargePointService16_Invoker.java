package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.steve.ocpp.task.ClearChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.GetCompositeScheduleTask;
import de.rwth.idsg.steve.ocpp.task.SetChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.TriggerMessageTask;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
public interface ChargePointService16_Invoker extends ChargePointService15_Invoker {

    void clearChargingProfile(ChargePointSelect cp, ClearChargingProfileTask task);

    void setChargingProfile(ChargePointSelect cp, SetChargingProfileTask task);

    void getCompositeSchedule(ChargePointSelect cp, GetCompositeScheduleTask task);

    void triggerMessage(ChargePointSelect cp, TriggerMessageTask task);

}
