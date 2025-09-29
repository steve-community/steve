package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.SetMonitoringBaseRequest;
import de.rwth.idsg.steve.ocpp20.model.SetMonitoringBaseResponse;
import de.rwth.idsg.steve.ocpp20.model.MonitoringBaseEnum;
import lombok.Getter;
import java.util.List;

@Getter
public class SetMonitoringBaseTask extends Ocpp20Task<SetMonitoringBaseRequest, SetMonitoringBaseResponse> {

    private final MonitoringBaseEnum monitoringBase;

    public SetMonitoringBaseTask(List<String> chargeBoxIdList, MonitoringBaseEnum monitoringBase) {
        super("SetMonitoringBase", chargeBoxIdList);
        this.monitoringBase = monitoringBase;
    }

    @Override
    public SetMonitoringBaseRequest createRequest() {
        SetMonitoringBaseRequest request = new SetMonitoringBaseRequest();
        request.setMonitoringBase(monitoringBase);
        return request;
    }

    @Override
    public Class<SetMonitoringBaseResponse> getResponseClass() {
        return SetMonitoringBaseResponse.class;
    }
}
