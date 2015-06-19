package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.RequestTask;
import ocpp.cp._2012._06.ClearCacheResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
public class ClearCacheResponseHandler extends AbstractOcppResponseHandler<ClearCacheResponse> {

    public ClearCacheResponseHandler(RequestTask requestTask, String chargeBoxId) {
        super(requestTask, chargeBoxId);
    }

    @Override
    public void handleResult(ClearCacheResponse response) {
        requestTask.addNewResponse(chargeBoxId, response.getStatus().value());
    }
}
