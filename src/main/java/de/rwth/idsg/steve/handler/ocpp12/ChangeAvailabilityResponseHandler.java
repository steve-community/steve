package de.rwth.idsg.steve.handler.ocpp12;

import de.rwth.idsg.steve.web.RequestTask;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2010._08.ChangeAvailabilityResponse;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
@RequiredArgsConstructor
public class ChangeAvailabilityResponseHandler implements AsyncHandler<ChangeAvailabilityResponse> {
    private final RequestTask requestTask;
    private final String chargeBoxId;

    @Override
    public void handleResponse(Response<ChangeAvailabilityResponse> res) {
        try {
            requestTask.addNewResponse(chargeBoxId, res.get().getStatus().value());

        } catch (InterruptedException | CancellationException | ExecutionException e) {
            requestTask.addNewError(chargeBoxId, e);
        }
    }
}
