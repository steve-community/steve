package de.rwth.idsg.steve.handler;

import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.06.2016
 */
public interface OcppJsonResponseHandler<T extends ResponseType> {
    void handleResponse(T response);
    void handleException(Exception e);
    void handleError(OcppJsonError error);
}
