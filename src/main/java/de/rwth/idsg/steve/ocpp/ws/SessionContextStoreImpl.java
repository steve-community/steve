package de.rwth.idsg.steve.ocpp.ws;

import com.google.common.collect.ImmutableMap;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
public class SessionContextStoreImpl implements SessionContextStore {

    private final ConcurrentHashMap<String, SessionContext> lookupTable = new ConcurrentHashMap<>();

    @Override
    public void add(String chargeBoxId, SessionContext context) {
        lookupTable.put(chargeBoxId, context);
    }

    @Override
    public void remove(String chargeBoxId) {
        SessionContext pair = lookupTable.remove(chargeBoxId);
        pair.getPingSchedule().cancel(true);
    }

    @Override
    public List<String> getChargeBoxIdList() {
        return Collections.list(lookupTable.keys());
    }

    @Override
    public Map<String, SessionContext> getACopy() {
        return ImmutableMap.copyOf(lookupTable);
    }

    @Override
    public int getSize() {
        return lookupTable.size();
    }

    @Override
    public WebSocketSession getSession(String chargeBoxId) {
        return lookupTable.get(chargeBoxId).getSession();
    }

}
