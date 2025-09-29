package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.ClearVariableMonitoringRequest;
import de.rwth.idsg.steve.ocpp20.model.ClearVariableMonitoringResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class ClearVariableMonitoringTask extends Ocpp20Task<ClearVariableMonitoringRequest, ClearVariableMonitoringResponse> {

    private final List<Integer> monitoringIds;

    public ClearVariableMonitoringTask(List<String> chargeBoxIdList, List<Integer> monitoringIds) {
        super("ClearVariableMonitoring", chargeBoxIdList);
        this.monitoringIds = monitoringIds;
    }

    @Override
    public ClearVariableMonitoringRequest createRequest() {
        ClearVariableMonitoringRequest request = new ClearVariableMonitoringRequest();
        request.setId(monitoringIds);
        return request;
    }

    @Override
    public Class<ClearVariableMonitoringResponse> getResponseClass() {
        return ClearVariableMonitoringResponse.class;
    }
}
