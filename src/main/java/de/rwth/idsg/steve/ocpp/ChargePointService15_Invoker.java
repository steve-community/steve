package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.steve.ocpp.task.CancelReservationTask;
import de.rwth.idsg.steve.ocpp.task.DataTransferTask;
import de.rwth.idsg.steve.ocpp.task.GetConfigurationTask;
import de.rwth.idsg.steve.ocpp.task.GetLocalListVersionTask;
import de.rwth.idsg.steve.ocpp.task.ReserveNowTask;
import de.rwth.idsg.steve.ocpp.task.SendLocalListTask;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.03.2015
 */
public interface ChargePointService15_Invoker extends ChargePointService12_Invoker {

    void dataTransfer(ChargePointSelect cp, DataTransferTask task);

    void getConfiguration(ChargePointSelect cp, GetConfigurationTask task);

    void getLocalListVersion(ChargePointSelect cp, GetLocalListVersionTask task);

    void sendLocalList(ChargePointSelect cp, SendLocalListTask task);

    void reserveNow(ChargePointSelect cp, ReserveNowTask task);

    void cancelReservation(ChargePointSelect cp, CancelReservationTask task);
}
