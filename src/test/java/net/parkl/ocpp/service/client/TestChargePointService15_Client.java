package net.parkl.ocpp.service.client;

import de.rwth.idsg.steve.ocpp.ChargePointService15_Invoker;
import de.rwth.idsg.steve.ocpp.task.*;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.InsertReservationParams;
import de.rwth.idsg.steve.service.BackgroundService;
import de.rwth.idsg.steve.service.IChargePointService15_Client;
import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.service.dto.EnhancedReserveNowParams;
import de.rwth.idsg.steve.web.dto.ocpp.*;
import net.parkl.ocpp.service.cs.ReservationService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("ChargePointService15_Client")
public class TestChargePointService15_Client extends TestChargePointService12_Client implements IChargePointService15_Client {
    @Autowired protected OcppTagService ocppTagService;
    @Autowired protected ReservationService reservationService;

	
	protected ChargePointService15_Invoker getOcpp15Invoker() {
		return invoker;
	}

	  // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.5
    // -------------------------------------------------------------------------

    public int dataTransfer(DataTransferParams params) {
        DataTransferTask task = new DataTransferTask(persistentTaskService,getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().dataTransfer(c, task));

        return taskId;
    }

    public int getConfiguration(GetConfigurationParams params) {
        GetConfigurationTask task = new GetConfigurationTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().getConfiguration(c, task));

        return taskId;
    }

    public int getLocalListVersion(MultipleChargePointSelect params) {
        GetLocalListVersionTask task = new GetLocalListVersionTask(persistentTaskService, getVersion(), params);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().getLocalListVersion(c, task));

        return taskId;
    }

    public int sendLocalList(SendLocalListParams params) {
        SendLocalListTask task = new SendLocalListTask(persistentTaskService, getVersion(), params, ocppTagService);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().sendLocalList(c, task));

        return taskId;
    }


    // -------------------------------------------------------------------------
    // Single Execution - since OCPP 1.5
    // -------------------------------------------------------------------------

    public int reserveNow(ReserveNowParams params) {
        List<ChargePointSelect> list = params.getChargePointSelectList();
        InsertReservationParams res = InsertReservationParams.builder()
                                                             .idTag(params.getIdTag())
                                                             .chargeBoxId(list.get(0).getChargeBoxId())
                                                             .connectorId(params.getConnectorId())
                                                             .startTimestamp(DateTime.now())
                                                             .expiryTimestamp(params.getExpiry().toDateTime())
                                                             .build();

        int reservationId = reservationService.insert(res);

        EnhancedReserveNowParams enhancedParams = new EnhancedReserveNowParams(params, reservationId);
        ReserveNowTask task = new ReserveNowTask(persistentTaskService, getVersion(), enhancedParams, reservationService);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().reserveNow(c, task));

        return taskId;
    }

    public int cancelReservation(CancelReservationParams params) {
        CancelReservationTask task = new CancelReservationTask(persistentTaskService, getVersion(), params, reservationService);

        Integer taskId = taskStore.add(task);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp15Invoker().cancelReservation(c, task));

        return taskId;
    }


}
