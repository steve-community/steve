/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.handler.ocpp16;
import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.SendLocalListRequest;
import ocpp.cp._2015._10.SendLocalListResponse;

/**
 *
 * @author david
 */
public class SendLocalListResponseHandler
        extends AbstractOcppResponseHandler<SendLocalListRequest, SendLocalListResponse> {

    public SendLocalListResponseHandler(RequestTask<SendLocalListRequest> task, String chargeBoxId) {
        super(task, chargeBoxId);
    }

    @Override
    public void handleResult(SendLocalListResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}