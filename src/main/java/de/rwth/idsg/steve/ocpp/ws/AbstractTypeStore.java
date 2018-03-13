package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ws.data.ActionResponsePair;

import java.util.HashMap;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
public abstract class AbstractTypeStore implements TypeStore {

    /**
     * Action field --> Request JAXB class
     */
    protected final HashMap<String, Class<? extends RequestType>> requestMap = new HashMap<>();

    /**
     * Request JAXB class --> Action field, Response JAXB class
     */
    protected final HashMap<Class<? extends RequestType>, ActionResponsePair> actionResponseMap = new HashMap<>();

    @Override
    public Class<? extends RequestType> findRequestClass(String action) {
        return requestMap.get(action);
    }

    @Override
    public ActionResponsePair findActionResponse(RequestType requestPayload) {
        return actionResponseMap.get(requestPayload.getClass());
    }
}
