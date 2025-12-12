/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.FileAppender;
import com.google.common.collect.Streams;
import de.rwth.idsg.steve.config.SteveProperties;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import java.util.stream.Collectors;

/**
 * https://github.com/steve-community/steve/issues/1910
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.12.2025
 */
@Slf4j
public class SteveApplicationStartupListener implements Ordered, ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public int getOrder() {
        // Run right after LoggingApplicationListener
        return LoggingApplicationListener.DEFAULT_ORDER + 1;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        String startMsg = "------------------- Starting -------------------";

        log.info(startMsg);
        log.info("Date/time zone of the application is set to {}. Current date/time: {}", SteveProperties.TIME_ZONE_ID, DateTime.now());

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        var logFiles = context.getLoggerList().stream()
            .flatMap(logger -> Streams.stream(logger.iteratorForAppenders()))
            .filter(appender -> appender instanceof FileAppender)
            .map(appender -> (FileAppender<?>) appender)
            .map(FileAppender::getFile)
            .collect(Collectors.toSet());

        if (!logFiles.isEmpty()) {
            System.out.println(startMsg);
            logFiles.forEach(logFile -> System.out.println("Log file location: " + logFile));
        }
    }
}
