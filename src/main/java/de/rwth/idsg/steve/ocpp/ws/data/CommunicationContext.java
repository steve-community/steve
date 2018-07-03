package de.rwth.idsg.steve.ocpp.ws.data;

import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import javax.xml.ws.Response;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Default holder/context of incoming and outgoing messages.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 23.03.2015
 */
@RequiredArgsConstructor
@Getter
public class CommunicationContext {

    private final WebSocketSession session;
    private final String chargeBoxId;

    @Setter private String incomingString;
    @Setter private String outgoingString;

    @Setter private OcppJsonMessage incomingMessage;
    @Setter private OcppJsonMessage outgoingMessage;

    @Setter private FutureResponseContext futureResponseContext;

    // for incoming responses to previously sent requests
    private Consumer<OcppJsonResult> resultHandler;
    private Consumer<OcppJsonError> errorHandler;

    public boolean isSetOutgoingError() {
        return (outgoingMessage != null) && (outgoingMessage instanceof OcppJsonError);
    }

    @SuppressWarnings("unchecked")
    public void createResultHandler(CommunicationTask task) {
        // TODO: not so sure about this
        resultHandler = result -> task.getHandler(chargeBoxId)
                                      .handleResponse(new DummyResponse(result.getPayload()));
    }

    public void createErrorHandler(CommunicationTask task) {
        // TODO: not so sure about this
        errorHandler = result -> task.defaultCallback()
                                     .success(chargeBoxId, result);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class DummyResponse implements Response<ResponseType> {
        private final ResponseType payload;

        @Override
        public Map<String, Object> getContext() {
            return null;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public ResponseType get() {
            return payload;
        }

        @Override
        public ResponseType get(long timeout, TimeUnit unit) {
            return payload;
        }
    }
}
