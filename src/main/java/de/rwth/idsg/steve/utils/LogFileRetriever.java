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
package de.rwth.idsg.steve.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.MemoryMappedFileAppender;
import org.apache.logging.log4j.core.appender.RandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.config.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        return getPath().map(path -> path.toAbsolutePath().toString())
                        .orElseGet(this::getErrorMessage);
    }

    public String getErrorMessage() {
        return "Not available";
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
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        List<Path> fileNameList = new ArrayList<>();
        for (Appender appender : config.getAppenders().values()) {
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
    private String extractFileName(Appender a) {
        if (a instanceof FileAppender) {
            return ((FileAppender) a).getFileName();

        } else if (a instanceof RollingFileAppender) {
            return ((RollingFileAppender) a).getFileName();

        } else if (a instanceof RollingRandomAccessFileAppender) {
            return ((RollingRandomAccessFileAppender) a).getFileName();

        } else if (a instanceof RandomAccessFileAppender) {
            return ((RandomAccessFileAppender) a).getFileName();

        } else if (a instanceof MemoryMappedFileAppender) {
            return ((MemoryMappedFileAppender) a).getFileName();

        } else {
            return null;
        }
    }
}
