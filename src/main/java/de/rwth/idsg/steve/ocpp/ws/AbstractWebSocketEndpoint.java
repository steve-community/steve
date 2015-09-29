package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.config.WebSocketConfiguration;
import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategy;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.SessionContext;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Pipeline;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
public abstract class AbstractWebSocketEndpoint implements WebSocketHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired private ScheduledExecutorService service;
    @Autowired private OcppServerRepository ocppServerRepository;
    @Autowired private FutureResponseContextStore futureResponseContextStore;
    @Autowired private WsSessionSelectStrategy wsSessionSelectStrategy;

    public static final String CHARGEBOX_ID_KEY = "CHARGEBOX_ID_KEY";

    protected Pipeline pipeline;
    private SessionContextStoreImpl sessionContextStore;

    public void init() {
        sessionContextStore = new SessionContextStoreImpl(wsSessionSelectStrategy);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            handleTextMessage(session, (TextMessage) message);

        } else if (message instanceof PongMessage) {
            handlePongMessage(session);

        } else if (message instanceof BinaryMessage) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Binary messages not supported"));

        } else {
            throw new IllegalStateException("Unexpected WebSocket message type: " + message);
        }
    }

    private void handleTextMessage(WebSocketSession session, TextMessage webSocketMessage) throws Exception {
        String incomingString = webSocketMessage.getPayload();
        log.debug("[id={}] Received text message: {}", session.getId(), incomingString);

        String chargeBoxId = getChargeBoxId(session);

        CommunicationContext context = new CommunicationContext();
        context.setSession(session);
        context.setChargeBoxId(chargeBoxId);
        context.setIncomingString(incomingString);

        pipeline.run(context);
    }

    private void handlePongMessage(WebSocketSession session) {
        log.debug("[id={}] Received pong message", session.getId());

        // TODO: Not sure about the following. Should update DB? Should call directly repo?
        ocppServerRepository.updateChargeboxHeartbeat(getChargeBoxId(session), DateTime.now());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("New connection established: {}", session);

        // Just to keep the connection alive, such that the servers do not close
        // the connection because of a idle timeout, we ping-pong at fixed intervals.
        ScheduledFuture pingSchedule = service.scheduleAtFixedRate(
                new PingTask(session),
                WebSocketConfiguration.PING_INTERVAL,
                WebSocketConfiguration.PING_INTERVAL,
                TimeUnit.MINUTES);

        String chargeBoxId = getChargeBoxId(session);
        sessionContextStore.add(chargeBoxId, session, pingSchedule);
        futureResponseContextStore.addSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.warn("[id={}] Connection was closed, status: {}", session.getId(), closeStatus);

        String chargeBoxId = getChargeBoxId(session);
        sessionContextStore.remove(chargeBoxId, session);
        futureResponseContextStore.removeSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        log.error("Oops", throwable);
        // TODO: Do something about this
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String getChargeBoxId(WebSocketSession session) {
        return (String) session.getAttributes().get(CHARGEBOX_ID_KEY);
    }

    public List<String> getChargeBoxIdList() {
        return sessionContextStore.getChargeBoxIdList();
    }

    public int getNumberOfChargeBoxes() {
        return sessionContextStore.getNumberOfChargeBoxes();
    }

    public Map<String, Deque<SessionContext>> getACopy() {
        return sessionContextStore.getACopy();
    }

    public WebSocketSession getSession(String chargeBoxId) {
        return sessionContextStore.getSession(chargeBoxId);
    }

}
