package de.rwth.idsg.steve.handler.ocpp12;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2010._08.UnlockConnectorRequest;
import ocpp.cp._2010._08.UnlockConnectorResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
public class UnlockConnectorResponseHandler
        extends AbstractOcppResponseHandler<UnlockConnectorRequest, UnlockConnectorResponse> {

    public UnlockConnectorResponseHandler(RequestTask<UnlockConnectorRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(UnlockConnectorResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}
