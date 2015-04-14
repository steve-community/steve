package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.RequestTask;
import ocpp.cp._2012._06.GetConfigurationResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
public class GetConfigurationResponseHandler extends AbstractOcppResponseHandler<GetConfigurationResponse> {

    public GetConfigurationResponseHandler(RequestTask requestTask, String chargeBoxId) {
        super(requestTask, chargeBoxId);
    }

    @Override
    public void handleResult(GetConfigurationResponse response) {
        // TODO: Not sure whether this prints nicely
        String str = "Known keys: "
                   + response.getConfigurationKey().toString()
                   + " / Unknown keys: "
                   + response.getUnknownKey().toString();

        requestTask.addNewResponse(chargeBoxId, str);
    }
}
