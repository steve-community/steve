package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
public interface SessionContextStore {
    void add(String chargeBoxId, SessionContext context);
    void remove(String chargeBoxId);
    List<String> getChargeBoxIdList();
    Map<String, SessionContext> getACopy();
    int getSize();
    WebSocketSession getSession(String chargeBoxId);
}
