package de.rwth.idsg.steve.ocpp.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
@Slf4j
@RequiredArgsConstructor
public class PingTask implements Runnable {
    private final String chargeBoxId;
    private final WebSocketSession session;

    private static final PingMessage PING_MESSAGE = new PingMessage(ByteBuffer.wrap("ping".getBytes(UTF_8)));

    @Override
    public void run() {
        WebSocketLogger.sendingPing(chargeBoxId, session);
        try {
            session.sendMessage(PING_MESSAGE);
        } catch (IOException e) {
            WebSocketLogger.pingError(chargeBoxId, session, e);
            // TODO: Do something about this
        }
    }
}
