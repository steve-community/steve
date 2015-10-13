package de.rwth.idsg.steve.handler;

import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.web.dto.RequestTask;
import lombok.RequiredArgsConstructor;

import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2015
 */
@RequiredArgsConstructor
public abstract class AbstractOcppResponseHandler<T> implements OcppResponseHandler<T> {
    protected final RequestTask requestTask;
    protected final String chargeBoxId;

    @Override
    public void handleResponse(Response<T> res) {
        try {
            handleResult(res.get());
        } catch (InterruptedException | CancellationException | ExecutionException e) {
            handleException(e);
        }
    }

    @Override
    public void handleResult(T response) {
        // Must override!
    }

    @Override
    public void handleException(Exception e) {
        requestTask.addNewError(chargeBoxId, e);
    }

    /**
     * Even though we have an error, this object is still a valid response from charge point
     * and RequestTask should treat it as such.
     * {@link RequestTask#addNewError(java.lang.String, java.lang.Exception)}
     * should be used when the request could not be delivered and there is a Java exception.
     */
    @Override
    public void handleError(OcppJsonError error) {
        requestTask.addNewResponse(chargeBoxId, error.toString());
    }
}
