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
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Asynchronous in-memory OCPP JSON channels. Each channel has an isolated, bounded executor queue
 * and a dedicated worker thread pool so producers never run consumers on their own threads.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.08.2026
 */
@Configuration
@EnableIntegration
public class MessagingConfiguration {

    public static final String IN_CHANNEL = "ocppInChannel";
    public static final String OUT_CHANNEL = "ocppOutChannel";
    public static final String OUT_CALL_CHANNEL = "ocppOutCallChannel";

    private static final String IN_EXECUTOR = "ocppInExecutor";
    private static final String OUT_EXECUTOR = "ocppOutExecutor";
    private static final String OUT_CALL_EXECUTOR = "ocppOutCallExecutor";

    private static final int QUEUE_CAPACITY = 10_000;

    @Bean(name = IN_EXECUTOR)
    public ThreadPoolTaskExecutor ocppInExecutor() {
        return executor(IN_EXECUTOR, 4);
    }

    @Bean(name = OUT_EXECUTOR)
    public ThreadPoolTaskExecutor ocppOutExecutor() {
        return executor(OUT_EXECUTOR, 4);
    }

    @Bean(name = OUT_CALL_EXECUTOR)
    public ThreadPoolTaskExecutor ocppOutCallExecutor() {
        return executor(OUT_CALL_EXECUTOR, 4);
    }

    @Bean(name = IN_CHANNEL)
    public ExecutorChannel ocppInChannel(@Qualifier(IN_EXECUTOR) TaskExecutor executor) {
        return new ExecutorChannel(executor);
    }

    @Bean(name = OUT_CHANNEL)
    public ExecutorChannel ocppOutChannel(@Qualifier(OUT_EXECUTOR) TaskExecutor executor) {
        return new ExecutorChannel(executor);
    }

    @Bean(name = OUT_CALL_CHANNEL)
    public ExecutorChannel ocppOutCallChannel(@Qualifier(OUT_CALL_EXECUTOR) TaskExecutor executor) {
        return new ExecutorChannel(executor);
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
        if (!channel.send(message)) {
            throw new MessageDeliveryException(message, errorMessage);
        }
    }

    private static ThreadPoolTaskExecutor executor(String executorName, int consumerPoolSize) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(consumerPoolSize);
        executor.setMaxPoolSize(consumerPoolSize);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(executorName + "-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
}
