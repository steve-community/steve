package de.rwth.idsg.steve.ocpp.soap;

import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.ext.logging.slf4j.Slf4jEventSender;
import org.apache.cxf.ext.logging.slf4j.Slf4jVerboseEventSender;

/**
 * Since {@link Slf4jEventSender} logs only the message and {@link Slf4jVerboseEventSender} logs everything, this
 * logging feature proxy finds a middle ground by logging the exchange id and the message (the most interesting parts).
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.04.2018
 */
public enum LoggingFeatureProxy {
    INSTANCE;

    private final LoggingFeature feature;

    LoggingFeatureProxy() {
        feature = new LoggingFeature();
        feature.setSender(new CustomSlf4jEventSender());
    }

    public LoggingFeature get() {
        return feature;
    }

    private static class CustomSlf4jEventSender extends Slf4jEventSender {
        @Override
        protected String getLogMessage(LogEvent event) {
            StringBuilder b = new StringBuilder();

            b.append('\n') // Start from the next line to have the output well-aligned
             .append("    ExchangeId: ").append(event.getExchangeId())
             .append('\n')
             .append("    Payload: ").append(event.getPayload());

            return b.toString();
        }
    }
}
