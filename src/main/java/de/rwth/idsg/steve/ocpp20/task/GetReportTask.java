package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.*;
import lombok.Getter;
import java.util.List;

/**
 * OCPP 2.0 GetReport task implementation
 */
@Getter
public class GetReportTask extends Ocpp20Task<GetReportRequest, GetReportResponse> {

    private final Integer requestId;
    private final List<ComponentVariable> componentVariables;
    private final List<ComponentCriterionEnum> componentCriteria;

    public GetReportTask(List<String> chargeBoxIds, Integer requestId,
                         List<ComponentVariable> componentVariables,
                         List<ComponentCriterionEnum> componentCriteria) {
        super("GetReport", chargeBoxIds);
        this.requestId = requestId;
        this.componentVariables = componentVariables;
        this.componentCriteria = componentCriteria;
    }

    @Override
    public GetReportRequest createRequest() {
        GetReportRequest request = new GetReportRequest();
        request.setRequestId(requestId);

        if (componentVariables != null && !componentVariables.isEmpty()) {
            request.getComponentVariable().addAll(componentVariables);
        }

        if (componentCriteria != null && !componentCriteria.isEmpty()) {
            request.getComponentCriteria().addAll(componentCriteria);
        }

        return request;
    }

    @Override
    public Class<GetReportResponse> getResponseClass() {
        return GetReportResponse.class;
    }
}