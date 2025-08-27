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
package de.rwth.idsg.steve.utils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 05.11.2015
 */
@Slf4j
public enum LogFileRetriever {
    INSTANCE;

    private final List<Path> logPathList;

    LogFileRetriever() {
        logPathList = getActiveLogFilePaths();
    }

    public Optional<Path> getPath() {
        Path p;
        if (logPathList.isEmpty()) {
            p = null;
        } else if (logPathList.size() == 1) {
            p = logPathList.get(0);
        } else {
            p = rollTheDice();
        }
        return Optional.ofNullable(p);
    }

    public String getLogFilePathOrErrorMessage() {
        return getPath().map(path -> path.toAbsolutePath().toString()).orElseGet(this::getErrorMessage);
    }

    public String getErrorMessage() {
        return "Logs are not available here, because they are being directed to console/stdout instead";
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * If the user configured multiple file appenders, which log file should we choose?
     * Clearly, the only sane solution is rolling the dice.
     * Easter egg mode: On
     */
    private Path rollTheDice() {
        log.trace("Rolling the dice...");
        int index = ThreadLocalRandom.current().nextInt(logPathList.size());
        return logPathList.get(index);
    }

    /**
     * We cannot presume that the default file name/location setting won't be changed by the user.
     * Therefore, we should be able to retrieve that info from the underlying logging mechanism
     * by iterating over appenders.
     */
    private List<Path> getActiveLogFilePaths() {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        Iterator<Appender<ILoggingEvent>> appenderIterator = logger.iteratorForAppenders();

        List<Path> fileNameList = new ArrayList<>();
        if (appenderIterator.hasNext()) {
            var appender = appenderIterator.next();
            String fileName = extractFileName(appender);
            if (fileName != null) {
                fileNameList.add(Paths.get(fileName));
            }
        }
        return fileNameList;
    }

    /**
     * File appender types do not share a "write-to-file" superclass.
     */
    private String extractFileName(Appender<ILoggingEvent> a) {
        if (a instanceof FileAppender) {
            return ((FileAppender<ILoggingEvent>) a).getFile();
        } else {
            return null;
        }
    }
}
