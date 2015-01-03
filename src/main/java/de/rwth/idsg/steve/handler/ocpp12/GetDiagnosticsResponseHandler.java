package de.rwth.idsg.steve.handler.ocpp12;

import de.rwth.idsg.steve.web.RequestTask;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2010._08.GetDiagnosticsResponse;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
@RequiredArgsConstructor
public class GetDiagnosticsResponseHandler implements AsyncHandler<GetDiagnosticsResponse> {
    private final RequestTask requestTask;
    private final String chargeBoxId;

    @Override
    public void handleResponse(Response<GetDiagnosticsResponse> res) {
        try {
            requestTask.addNewResponse(chargeBoxId, res.get().getFileName());

        } catch (InterruptedException | CancellationException | ExecutionException e) {
            requestTask.addNewError(chargeBoxId, e);
        }
    }
}
