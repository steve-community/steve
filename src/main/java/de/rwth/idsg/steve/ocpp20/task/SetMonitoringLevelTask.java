package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.SetMonitoringLevelRequest;
import de.rwth.idsg.steve.ocpp20.model.SetMonitoringLevelResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class SetMonitoringLevelTask extends Ocpp20Task<SetMonitoringLevelRequest, SetMonitoringLevelResponse> {

    private final Integer severity;

    public SetMonitoringLevelTask(List<String> chargeBoxIdList, Integer severity) {
        super("SetMonitoringLevel", chargeBoxIdList);
        this.severity = severity;
    }

    @Override
    public SetMonitoringLevelRequest createRequest() {
        SetMonitoringLevelRequest request = new SetMonitoringLevelRequest();
        request.setSeverity(severity);
        return request;
    }

    @Override
    public Class<SetMonitoringLevelResponse> getResponseClass() {
        return SetMonitoringLevelResponse.class;
    }
}
