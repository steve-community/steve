package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.SetVariableMonitoringRequest;
import de.rwth.idsg.steve.ocpp20.model.SetVariableMonitoringResponse;
import de.rwth.idsg.steve.ocpp20.model.SetMonitoringData;
import lombok.Getter;
import java.util.List;

@Getter
public class SetVariableMonitoringTask extends Ocpp20Task<SetVariableMonitoringRequest, SetVariableMonitoringResponse> {

    private final List<SetMonitoringData> monitoringData;

    public SetVariableMonitoringTask(List<String> chargeBoxIdList, List<SetMonitoringData> monitoringData) {
        super("SetVariableMonitoring", chargeBoxIdList);
        this.monitoringData = monitoringData;
    }

    @Override
    public SetVariableMonitoringRequest createRequest() {
        SetVariableMonitoringRequest request = new SetVariableMonitoringRequest();
        request.setSetMonitoringData(monitoringData);
        return request;
    }

    @Override
    public Class<SetVariableMonitoringResponse> getResponseClass() {
        return SetVariableMonitoringResponse.class;
    }
}
