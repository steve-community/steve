/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.handler.ocpp16;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.DataTransferRequest;
import ocpp.cp._2015._10.DataTransferResponse;

/**
 *
 * @author david
 */
public class DataTransferResponseHandler
        extends AbstractOcppResponseHandler<DataTransferRequest, DataTransferResponse> {

    public DataTransferResponseHandler(RequestTask<DataTransferRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(DataTransferResponse response) {
        StringBuilder builder = new StringBuilder(response.getStatus().value());
        if (response.isSetData()) {
            builder.append(" / Data: ").append(response.getData());
        }
        requestTask.addNewResponse(chargeBoxId, builder.toString());
    }
}