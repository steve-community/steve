package de.rwth.idsg.steve.handler.ocpp12;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2010._08.RemoteStartTransactionRequest;
import ocpp.cp._2010._08.RemoteStartTransactionResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
public class RemoteStartTransactionResponseHandler
        extends AbstractOcppResponseHandler<RemoteStartTransactionRequest, RemoteStartTransactionResponse> {

    public RemoteStartTransactionResponseHandler(RemoteStartTransactionRequest req, RequestTask task, String chargeBoxId) {
        super(req, task, chargeBoxId);
    }

    @Override
    public void handleResult(RemoteStartTransactionResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}
