package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.ComponentVariable;
import de.rwth.idsg.steve.ocpp20.model.GetMonitoringReportRequest;
import de.rwth.idsg.steve.ocpp20.model.GetMonitoringReportResponse;
import de.rwth.idsg.steve.ocpp20.model.MonitoringCriterionEnum;
import lombok.Getter;
import java.util.List;

@Getter
public class GetMonitoringReportTask extends Ocpp20Task<GetMonitoringReportRequest, GetMonitoringReportResponse> {

    private final Integer requestId;
    private final List<MonitoringCriterionEnum> criteria;
    private final List<ComponentVariable> componentVariables;

    public GetMonitoringReportTask(List<String> chargeBoxIdList,
                                   Integer requestId,
                                   List<MonitoringCriterionEnum> criteria,
                                   List<ComponentVariable> componentVariables) {
        super("GetMonitoringReport", chargeBoxIdList);
        this.requestId = requestId;
        this.criteria = criteria;
        this.componentVariables = componentVariables;
    }

    @Override
    public GetMonitoringReportRequest createRequest() {
        GetMonitoringReportRequest request = new GetMonitoringReportRequest();
        request.setRequestId(requestId);
        if (componentVariables != null && !componentVariables.isEmpty()) {
            request.getComponentVariable().addAll(componentVariables);
        }
        if (criteria != null && !criteria.isEmpty()) {
            request.setMonitoringCriteria(criteria);
        }
        return request;
    }

    @Override
    public Class<GetMonitoringReportResponse> getResponseClass() {
        return GetMonitoringReportResponse.class;
    }
}
