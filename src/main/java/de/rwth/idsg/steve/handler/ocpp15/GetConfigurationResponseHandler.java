package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.web.RequestTask;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2012._06.GetConfigurationResponse;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
@RequiredArgsConstructor
public class GetConfigurationResponseHandler implements AsyncHandler<GetConfigurationResponse> {
    private final RequestTask requestTask;
    private final String chargeBoxId;

    @Override
    public void handleResponse(Response<GetConfigurationResponse> res) {
        try {
            GetConfigurationResponse r = res.get();

            // TODO: Not sure whether this prints nicely
            String str = "Known keys: " + r.getConfigurationKey().toString()
                    + " / Unknown keys: " + r.getUnknownKey().toString();

            requestTask.addNewResponse(chargeBoxId, str);

        } catch (InterruptedException | CancellationException | ExecutionException e) {
            requestTask.addNewError(chargeBoxId, e);
        }
    }
}
