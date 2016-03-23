package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2012._06.GetLocalListVersionRequest;
import ocpp.cp._2012._06.GetLocalListVersionResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
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
