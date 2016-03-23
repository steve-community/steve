package de.rwth.idsg.steve.handler;

import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Response;
import java.util.ArrayList;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2015
 */
@RequiredArgsConstructor
public abstract class AbstractOcppResponseHandler<S extends RequestType, T extends ResponseType>
        implements OcppResponseHandler<T> {

    protected final S request;
    protected final RequestTask requestTask;
    protected final String chargeBoxId;

    private final Logger log = LoggerFactory.getLogger(getClass());

    // The default initial capacity is 10. We probably won't need that much.
    private ArrayList<OcppCallback<T>> callbackList = new ArrayList<>(2);

    @Override
    public void addCallback(OcppCallback<T> cb) {
        callbackList.add(cb);
    }

    // -------------------------------------------------------------------------
    // AsyncHandler
    // -------------------------------------------------------------------------

    @Override
    public void handleResponse(Response<T> res) {
        try {
            handleResult(res.get());
            success(res.get());

        } catch (Exception e) {
            handleException(e);
            failed(e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // OcppResponseHandler
    //
    // Skip the method handleResult(T response), since it should be
    // implemented by subclasses depending on the actual response
    // -------------------------------------------------------------------------

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

        // But, as far as the callbacks are concerned, this is still a failure.
        failed(error.getErrorDescription());
    }

    // -------------------------------------------------------------------------
    // OcppCallback helpers
    //
    // OcppCallback exceptions should be handled silently, that is they should
    // not take the ongoing process/thread or system down. With this, we just
    // log the exception and allow the application to continue with the next
    // callback in line.
    // -------------------------------------------------------------------------

    private void success(T response) {
        for (OcppCallback<T> c : callbackList) {
            try {
                c.success(response);
            } catch (Exception e) {
                log.error("Exception occurred in OcppCallback", e);
            }
        }
    }

    private void failed(String errorMessage) {
        for (OcppCallback<T> c : callbackList) {
            try {
                c.failed(errorMessage);
            } catch (Exception e) {
                log.error("Exception occurred in OcppCallback", e);
            }
        }
    }
}
