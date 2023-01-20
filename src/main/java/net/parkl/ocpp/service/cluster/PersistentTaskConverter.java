package net.parkl.ocpp.service.cluster;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.*;
import de.rwth.idsg.steve.service.OcppTagService;
import de.rwth.idsg.steve.service.dto.EnhancedReserveNowParams;
import de.rwth.idsg.steve.service.dto.EnhancedSetChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.parkl.ocpp.entities.PersistentTask;
import net.parkl.ocpp.service.cs.ChargingProfileService;
import net.parkl.ocpp.service.cs.ReservationService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersistentTaskConverter {
    private final ReservationService reservationService;
    private final ChargingProfileService chargingProfileService;
    private final OcppTagService ocppTagService;
    private final PersistentTaskService persistentTaskService;

    public PersistentTask toPersistentTask(CommunicationTask task) {
        PersistentTask persistentTask = new PersistentTask();
        persistentTask.setOcppVersion(task.getOcppVersion().getValue());
        persistentTask.setClassName(task.getClass().getSimpleName());
        persistentTask.setParams(serializeParams(task.getParams()));
        return persistentTask;
    }

    @SneakyThrows
    private String serializeParams(ChargePointSelection params) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(params);
    }

    public CommunicationTask fromPersistentTask(PersistentTask persistentTask) {
        CommunicationTask task = createTask(persistentTask);
        task.setEndTimestamp(persistentTask.getEndTimestamp());
        return task;
    }

    private CommunicationTask createTask(PersistentTask persistentTask) {
        OcppVersion version = OcppVersion.fromValue(persistentTask.getOcppVersion());
        String className = persistentTask.getClassName();
        if (className.equals(CancelReservationTask.class.getSimpleName())) {
            return new CancelReservationTask(version,
                    deserializeParams(persistentTask.getParams(), CancelReservationParams.class), reservationService);
        }
        if (className.equals(ChangeAvailabilityTask.class.getSimpleName())) {
            return new ChangeAvailabilityTask(persistentTaskService, version,
                    deserializeParams(persistentTask.getParams(), ChangeAvailabilityParams.class));
        }
        if (className.equals(ChangeConfigurationTask.class.getSimpleName())) {
            return new ChangeConfigurationTask(persistentTaskService, version,
                    deserializeParams(persistentTask.getParams(), ChangeConfigurationParams.class));
        }
        if (className.equals(ClearCacheTask.class.getSimpleName())) {
            return new ClearCacheTask(persistentTaskService, version,
                    deserializeParams(persistentTask.getParams(), MultipleChargePointSelect.class));
        }
        if (className.equals(ClearChargingProfileTask.class.getSimpleName())) {
            return new ClearChargingProfileTask(version,
                    deserializeParams(persistentTask.getParams(), ClearChargingProfileParams.class),
                    chargingProfileService);
        }
        if (className.equals(DataTransferTask.class.getSimpleName())) {
            return new DataTransferTask(version,
                    deserializeParams(persistentTask.getParams(), DataTransferParams.class));
        }
        if (className.equals(GetCompositeScheduleTask.class.getSimpleName())) {
            return new GetCompositeScheduleTask(version,
                    deserializeParams(persistentTask.getParams(), GetCompositeScheduleParams.class));
        }
        if (className.equals(GetConfigurationTask.class.getSimpleName())) {
            return new GetConfigurationTask(version,
                    deserializeParams(persistentTask.getParams(), GetConfigurationParams.class));
        }
        if (className.equals(GetDiagnosticsTask.class.getSimpleName())) {
            return new GetDiagnosticsTask(persistentTaskService, version,
                    deserializeParams(persistentTask.getParams(), GetDiagnosticsParams.class));
        }
        if (className.equals(GetLocalListVersionTask.class.getSimpleName())) {
            return new GetLocalListVersionTask(version,
                    deserializeParams(persistentTask.getParams(), MultipleChargePointSelect.class));
        }
        if (className.equals(RemoteStartTransactionTask.class.getSimpleName())) {
            return new RemoteStartTransactionTask(persistentTaskService, version,
                    deserializeParams(persistentTask.getParams(), RemoteStartTransactionParams.class));
        }
        if (className.equals(RemoteStopTransactionTask.class.getSimpleName())) {
            return new RemoteStopTransactionTask(persistentTaskService, version,
                    deserializeParams(persistentTask.getParams(), RemoteStopTransactionParams.class));
        }
        if (className.equals(ReserveNowTask.class.getSimpleName())) {
            return new ReserveNowTask(version,
                    deserializeParams(persistentTask.getParams(), EnhancedReserveNowParams.class), reservationService);
        }
        if (className.equals(ResetTask.class.getSimpleName())) {
            return new ResetTask(persistentTaskService, version,
                    deserializeParams(persistentTask.getParams(), ResetParams.class));
        }
        if (className.equals(SendLocalListTask.class.getSimpleName())) {
            return new SendLocalListTask(version,
                    deserializeParams(persistentTask.getParams(), SendLocalListParams.class), ocppTagService);
        }
        if (className.equals(SetChargingProfileTask.class.getSimpleName())) {
            return new SetChargingProfileTask(version,
                    deserializeParams(persistentTask.getParams(), EnhancedSetChargingProfileParams.class),
                    chargingProfileService);
        }
        if (className.equals(TriggerMessageTask.class.getSimpleName())) {
            return new TriggerMessageTask(version,
                    deserializeParams(persistentTask.getParams(), TriggerMessageParams.class));
        }
        if (className.equals(UnlockConnectorTask.class.getSimpleName())) {
            return new UnlockConnectorTask(persistentTaskService, version,
                    deserializeParams(persistentTask.getParams(), UnlockConnectorParams.class));
        }
        if (className.equals(UpdateFirmwareTask.class.getSimpleName())) {
            return new UpdateFirmwareTask(persistentTaskService, version,
                    deserializeParams(persistentTask.getParams(), UpdateFirmwareParams.class));
        }
        throw new IllegalArgumentException("Invalid task class name: "+className);
    }

    @SneakyThrows
    private <T> T deserializeParams(String serializedParams, Class<T> paramsClass) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(serializedParams, paramsClass);
    }
}
