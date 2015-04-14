package de.rwth.idsg.steve.handler.ocpp12;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.RequestTask;
import ocpp.cp._2010._08.GetDiagnosticsResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
public class GetDiagnosticsResponseHandler extends AbstractOcppResponseHandler<GetDiagnosticsResponse> {

    public GetDiagnosticsResponseHandler(RequestTask requestTask, String chargeBoxId) {
        super(requestTask, chargeBoxId);
    }

    @Override
    public void handleResult(GetDiagnosticsResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getFileName());
    }
}
