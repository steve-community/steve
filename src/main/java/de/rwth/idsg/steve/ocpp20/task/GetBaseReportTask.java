package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.GetBaseReportRequest;
import de.rwth.idsg.steve.ocpp20.model.GetBaseReportResponse;
import de.rwth.idsg.steve.ocpp20.model.ReportBaseEnum;
import lombok.Getter;
import java.util.List;

/**
 * OCPP 2.0 GetBaseReport task implementation
 */
@Getter
public class GetBaseReportTask extends Ocpp20Task<GetBaseReportRequest, GetBaseReportResponse> {

    private final Integer requestId;
    private final ReportBaseEnum reportBase;

    public GetBaseReportTask(List<String> chargeBoxIds, Integer requestId, ReportBaseEnum reportBase) {
        super("GetBaseReport", chargeBoxIds);
        this.requestId = requestId;
        this.reportBase = reportBase;
    }

    @Override
    public GetBaseReportRequest createRequest() {
        GetBaseReportRequest request = new GetBaseReportRequest();
        request.setRequestId(requestId);
        request.setReportBase(reportBase);
        return request;
    }

    @Override
    public Class<GetBaseReportResponse> getResponseClass() {
        return GetBaseReportResponse.class;
    }
}