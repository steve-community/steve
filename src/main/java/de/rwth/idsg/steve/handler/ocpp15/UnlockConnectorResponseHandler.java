package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.RequestTask;
import ocpp.cp._2012._06.UnlockConnectorResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
public class UnlockConnectorResponseHandler extends AbstractOcppResponseHandler<UnlockConnectorResponse> {

    public UnlockConnectorResponseHandler(RequestTask requestTask, String chargeBoxId) {
        super(requestTask, chargeBoxId);
    }

    @Override
    public void handleResult(UnlockConnectorResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}
