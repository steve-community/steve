package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
public interface TypeStore {
    Class<? extends RequestType> findRequestClass(String action);
    <T extends RequestType> ActionResponsePair findActionResponse(T requestPayload);
}
