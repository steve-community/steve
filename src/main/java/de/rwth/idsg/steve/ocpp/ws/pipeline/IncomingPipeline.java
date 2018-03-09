package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonMessage;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.ws.Response;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * For all incoming message types.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 23.03.2015
 */
@Slf4j
@RequiredArgsConstructor
public class IncomingPipeline extends AbstractPipeline {
    private final Deserializer deserializer;
    private final AbstractCallHandler handler;
    private final OutgoingPipeline outgoingPipeline;

    @Override
    @SuppressWarnings("unchecked")
    public void process(CommunicationContext context) {
        deserializer.process(context);

        // When the incoming could not be deserialized
        if (context.isSetOutgoingError()) {
            outgoingPipeline.process(context);
            return;
        }

        OcppJsonMessage msg = context.getIncomingMessage();

        if (msg instanceof OcppJsonCall) {
            handler.process(context);
            outgoingPipeline.process(context);

        } else if (msg instanceof OcppJsonResult) {
            OcppJsonResult result = (OcppJsonResult) msg;

            // TODO: not so sure about this
            context.getTask()
                   .getHandler(context.getChargeBoxId())
                   .handleResponse(new DummyResponse(result.getPayload()));

        } else if (msg instanceof OcppJsonError) {
            OcppJsonError result = (OcppJsonError) msg;

            // TODO: not so sure about this
            context.getTask()
                   .defaultCallback()
                   .success(context.getChargeBoxId(), result);
        }
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
