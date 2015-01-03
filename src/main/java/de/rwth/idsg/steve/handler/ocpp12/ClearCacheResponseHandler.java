package de.rwth.idsg.steve.handler.ocpp12;

import de.rwth.idsg.steve.web.RequestTask;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2010._08.ClearCacheResponse;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
@RequiredArgsConstructor
public class ClearCacheResponseHandler implements AsyncHandler<ClearCacheResponse> {
    private final RequestTask requestTask;
    private final String chargeBoxId;

    @Override
    public void handleResponse(Response<ClearCacheResponse> res) {
        try {
            requestTask.addNewResponse(chargeBoxId, res.get().getStatus().value());

        } catch (InterruptedException | CancellationException | ExecutionException e) {
            requestTask.addNewError(chargeBoxId, e);
        }
    }
}
