package de.rwth.idsg.steve.ocpp.ws.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 25.03.2015
 */
@Getter
@RequiredArgsConstructor
public class SessionContext {
    private final WebSocketSession session;
    private final ScheduledFuture pingSchedule;
    private final DateTime openSince;
}
