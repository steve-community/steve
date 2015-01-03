package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.web.RequestTask;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2012._06.UpdateFirmwareResponse;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
@RequiredArgsConstructor
public class UpdateFirmwareResponseHandler implements AsyncHandler<UpdateFirmwareResponse> {
    private final RequestTask requestTask;
    private final String chargeBoxId;

    @Override
    public void handleResponse(Response<UpdateFirmwareResponse> res) {
        try {
            res.get();
            requestTask.addNewResponse(chargeBoxId, "OK");

        } catch (InterruptedException | CancellationException | ExecutionException e) {
            requestTask.addNewError(chargeBoxId, e);
        }
    }
}
