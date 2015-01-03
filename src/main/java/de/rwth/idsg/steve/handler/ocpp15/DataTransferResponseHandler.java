package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.web.RequestTask;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2012._06.DataTransferResponse;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
 */
@RequiredArgsConstructor
public class DataTransferResponseHandler implements AsyncHandler<DataTransferResponse> {
    private final RequestTask requestTask;
    private final String chargeBoxId;

    @Override
    public void handleResponse(Response<DataTransferResponse> res) {
        try {
            DataTransferResponse d = res.get();
            StringBuilder builder = new StringBuilder(d.getStatus().value());
            if (d.isSetData()) {
                builder.append(" / Data: ").append(d.getData());
            }
            requestTask.addNewResponse(chargeBoxId, builder.toString());

        } catch (InterruptedException | CancellationException | ExecutionException e) {
            requestTask.addNewError(chargeBoxId, e);
        }
    }
}
