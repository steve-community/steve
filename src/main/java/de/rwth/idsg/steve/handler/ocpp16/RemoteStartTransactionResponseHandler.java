/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.handler.ocpp16;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.RemoteStartTransactionRequest;
import ocpp.cp._2015._10.RemoteStartTransactionResponse;

/**
 *
 * @author david
 */
public class RemoteStartTransactionResponseHandler
        extends AbstractOcppResponseHandler<RemoteStartTransactionRequest, RemoteStartTransactionResponse> {

    public RemoteStartTransactionResponseHandler(RequestTask<RemoteStartTransactionRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(RemoteStartTransactionResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}

