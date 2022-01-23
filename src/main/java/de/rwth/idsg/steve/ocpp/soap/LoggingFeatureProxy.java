/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.ocpp.soap;

import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.ext.logging.slf4j.Slf4jEventSender;
import org.apache.cxf.ext.logging.slf4j.Slf4jVerboseEventSender;

/**
 * Since {@link Slf4jEventSender} logs only the message and {@link Slf4jVerboseEventSender} logs everything, this
 * logging feature proxy finds a middle ground by logging the exchange id and the message (the most interesting parts).
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
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
