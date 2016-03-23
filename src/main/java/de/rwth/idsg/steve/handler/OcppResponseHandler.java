package de.rwth.idsg.steve.handler;

import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;

import javax.xml.ws.AsyncHandler;

/**
 * Handler for WebSocket and SOAP response messages
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2015
 */
public interface OcppResponseHandler<S extends RequestType, T extends ResponseType> extends AsyncHandler<T> {

    void addCallback(OcppCallback<T> cb);
    S getRequest();

    void handleResult(T response);
    void handleError(OcppJsonError error);
    void handleException(Exception e);
}
