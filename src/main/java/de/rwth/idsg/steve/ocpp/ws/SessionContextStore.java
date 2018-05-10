package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import org.springframework.web.socket.WebSocketSession;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
public interface SessionContextStore {
    void add(String chargeBoxId, WebSocketSession session, ScheduledFuture pingSchedule);
    void remove(String chargeBoxId, WebSocketSession session);
    WebSocketSession getSession(String chargeBoxId);
    int getSize(String chargeBoxId);
    int getNumberOfChargeBoxes();
    List<String> getChargeBoxIdList();
    Map<String, Deque<SessionContext>> getACopy();
}
