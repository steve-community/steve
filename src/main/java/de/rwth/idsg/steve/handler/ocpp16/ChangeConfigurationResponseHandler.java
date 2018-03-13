/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.handler.ocpp16;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.ChangeConfigurationRequest;
import ocpp.cp._2015._10.ChangeConfigurationResponse;

/**
 *
 * @author david
 */
public class ChangeConfigurationResponseHandler
        extends AbstractOcppResponseHandler<ChangeConfigurationRequest, ChangeConfigurationResponse> {

    public ChangeConfigurationResponseHandler(RequestTask<ChangeConfigurationRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(ChangeConfigurationResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}