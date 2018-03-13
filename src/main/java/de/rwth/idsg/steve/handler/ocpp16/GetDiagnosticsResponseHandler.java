/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.handler.ocpp16;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.GetDiagnosticsRequest;
import ocpp.cp._2015._10.GetDiagnosticsResponse;

/**
 *
 * @author david
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
