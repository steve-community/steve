package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2012._06.SendLocalListResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
public class SendLocalListResponseHandler extends AbstractOcppResponseHandler<SendLocalListResponse> {

    public SendLocalListResponseHandler(RequestTask requestTask, String chargeBoxId) {
        super(requestTask, chargeBoxId);
    }

    @Override
    public void handleResult(SendLocalListResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}
