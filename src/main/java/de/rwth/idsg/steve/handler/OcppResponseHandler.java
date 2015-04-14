package de.rwth.idsg.steve.handler;

import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;

import javax.xml.ws.AsyncHandler;

/**
 * Handler for WebSocket and SOAP response messages
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2015
 */
public interface OcppResponseHandler<T> extends AsyncHandler<T> {
    void handleResult(T response);
    void handleError(OcppJsonError error);
    void handleException(Exception e);
}
