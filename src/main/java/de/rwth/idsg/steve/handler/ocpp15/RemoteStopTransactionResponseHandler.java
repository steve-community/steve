package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.web.RequestTask;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2012._06.RemoteStopTransactionResponse;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
@RequiredArgsConstructor
public class RemoteStopTransactionResponseHandler implements AsyncHandler<RemoteStopTransactionResponse> {
    private final RequestTask requestTask;
    private final String chargeBoxId;

    @Override
    public void handleResponse(Response<RemoteStopTransactionResponse> res) {
        try {
            requestTask.addNewResponse(chargeBoxId, res.get().getStatus().value());

        } catch (InterruptedException | CancellationException | ExecutionException e) {
            requestTask.addNewError(chargeBoxId, e);
        }
    }
}
