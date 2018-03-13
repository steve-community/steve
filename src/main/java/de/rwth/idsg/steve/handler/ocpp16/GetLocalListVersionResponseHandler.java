/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.handler.ocpp16;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.GetLocalListVersionRequest;
import ocpp.cp._2015._10.GetLocalListVersionResponse;

/**
 *
 * @author david
 */
public class GetLocalListVersionResponseHandler
        extends AbstractOcppResponseHandler<GetLocalListVersionRequest, GetLocalListVersionResponse> {

    public GetLocalListVersionResponseHandler(RequestTask<GetLocalListVersionRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(GetLocalListVersionResponse response) {
        requestTask.addNewResponse(chargeBoxId, String.valueOf(response.getListVersion()));
    }
}
