package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2012._06.GetDiagnosticsRequest;
import ocpp.cp._2012._06.GetDiagnosticsResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
public class GetDiagnosticsResponseHandler
        extends AbstractOcppResponseHandler<GetDiagnosticsRequest, GetDiagnosticsResponse> {

    public GetDiagnosticsResponseHandler(RequestTask<GetDiagnosticsRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(GetDiagnosticsResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getFileName());
    }
}
