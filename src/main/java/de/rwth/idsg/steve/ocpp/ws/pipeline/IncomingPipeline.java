package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonMessage;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * For all incoming message types.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 23.03.2015
 */
@Slf4j
@RequiredArgsConstructor
public class IncomingPipeline implements Consumer<CommunicationContext> {

    private final Deserializer deserializer;
    private final AbstractCallHandler handler;
    private final Serializer serializer;
    private final Sender sender;

    @Override
    public void accept(CommunicationContext context) {
        deserializer.accept(context);

        // When the incoming could not be deserialized
        if (context.isSetOutgoingError()) {
            serializer.accept(context);
            sender.accept(context);
            return;
        }

        OcppJsonMessage msg = context.getIncomingMessage();

        if (msg instanceof OcppJsonCall) {
            handler.accept(context);
            serializer.accept(context);
            sender.accept(context);

        } else if (msg instanceof OcppJsonResult) {
            context.getResultHandler()
                   .accept((OcppJsonResult) msg);

        } else if (msg instanceof OcppJsonError) {
            context.getErrorHandler()
                   .accept((OcppJsonError) msg);
        }
    }

}
