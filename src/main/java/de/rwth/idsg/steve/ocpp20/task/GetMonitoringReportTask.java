package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.GetMonitoringReportRequest;
import de.rwth.idsg.steve.ocpp20.model.GetMonitoringReportResponse;
import de.rwth.idsg.steve.ocpp20.model.MonitoringCriterionEnum;
import lombok.Getter;
import java.util.List;

@Getter
public class GetMonitoringReportTask extends Ocpp20Task<GetMonitoringReportRequest, GetMonitoringReportResponse> {

    private final Integer requestId;
    private final List<MonitoringCriterionEnum> criteria;

    public GetMonitoringReportTask(List<String> chargeBoxIdList, Integer requestId, List<MonitoringCriterionEnum> criteria) {
        super("GetMonitoringReport", chargeBoxIdList);
        this.requestId = requestId;
        this.criteria = criteria;
    }

    @Override
    public GetMonitoringReportRequest createRequest() {
        GetMonitoringReportRequest request = new GetMonitoringReportRequest();
        request.setRequestId(requestId);
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
