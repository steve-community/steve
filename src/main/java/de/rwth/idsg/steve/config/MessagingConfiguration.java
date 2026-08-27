/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.config;

import de.rwth.idsg.steve.messaging.Messaging;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.08.2026
 */
@Configuration
@EnableIntegration
public class MessagingConfiguration {

    public static final String IN_CHANNEL = "ocppInChannel";
    public static final String OUT_CHANNEL = "ocppOutChannel";
    public static final String OUT_CALL_CHANNEL = "ocppOutCallChannel";

    public static final String POLL_INTERVAL_MILLIS = "100";

    private static final int QUEUE_CAPACITY = 10_000;
    private static final long SEND_TIMEOUT_MILLIS = 2_000;

    @Bean(name = IN_CHANNEL)
    public QueueChannel ocppInChannel() {
        return new QueueChannel(QUEUE_CAPACITY);
    }

    @Bean(name = OUT_CHANNEL)
    public QueueChannel ocppOutChannel() {
        return new QueueChannel(QUEUE_CAPACITY);
    }

    @Bean(name = OUT_CALL_CHANNEL)
    public QueueChannel ocppOutCallChannel() {
        return new QueueChannel(QUEUE_CAPACITY);
    }

    @Bean
    public Messaging.In.Producer inProducer(@Qualifier(IN_CHANNEL) MessageChannel channel) {
        return message -> send(channel, message, "Could not send to incoming OCPP JSON message queue");
    }

    @Bean
    public Messaging.Out.Producer outProducer(@Qualifier(OUT_CHANNEL) MessageChannel channel) {
        return message -> send(channel, message, "Could not send to outgoing OCPP JSON message queue");
    }

    @Bean
    public Messaging.OutCall.Producer outCallProducer(@Qualifier(OUT_CALL_CHANNEL) MessageChannel channel) {
        return message -> send(channel, message, "Could not send to outgoing OCPP JSON call queue");
    }

    private static void send(MessageChannel channel, Message<?> message, String errorMessage) {
        if (!channel.send(message, SEND_TIMEOUT_MILLIS)) {
            throw new MessageDeliveryException(message, errorMessage);
        }
    }
}
