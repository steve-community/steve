package de.rwth.idsg.steve.handler.ocpp12;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2010._08.ResetRequest;
import ocpp.cp._2010._08.ResetResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
public class ResetResponseHandler extends AbstractOcppResponseHandler<ResetRequest, ResetResponse> {

    public ResetResponseHandler(RequestTask<ResetRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(ResetResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}
