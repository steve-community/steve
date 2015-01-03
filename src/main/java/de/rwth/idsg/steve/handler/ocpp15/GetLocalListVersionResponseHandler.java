package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.web.RequestTask;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2012._06.GetLocalListVersionResponse;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
 */
@RequiredArgsConstructor
public class GetLocalListVersionResponseHandler implements AsyncHandler<GetLocalListVersionResponse> {
    private final RequestTask requestTask;
    private final String chargeBoxId;

    @Override
    public void handleResponse(Response<GetLocalListVersionResponse> res) {
        try {
            requestTask.addNewResponse(chargeBoxId, Integer.toString(res.get().getListVersion()));

        } catch (InterruptedException | CancellationException | ExecutionException e) {
            requestTask.addNewError(chargeBoxId, e);
        }
    }
}
