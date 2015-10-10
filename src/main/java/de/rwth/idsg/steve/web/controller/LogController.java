package de.rwth.idsg.steve.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.MemoryMappedFileAppender;
import org.apache.logging.log4j.core.appender.RandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Slf4j
@Controller
@RequestMapping(value = "/manager")
public class LogController {

    private List<Path> logPathList;
    private Random random = new Random();
    private static final String ERROR_MESSAGE = "Not available";

    @PostConstruct
    private void init() {
        logPathList = getActiveLogFilePaths();
    }

    @RequestMapping(value = "/log", method = RequestMethod.GET)
    public void log(HttpServletResponse response) {
        response.setContentType("text/plain");

        try (PrintWriter writer = response.getWriter()) {
            Path p = decidePath();
            if (p == null) {
                writer.write(ERROR_MESSAGE);
            } else {
                Files.lines(p, StandardCharsets.UTF_8)
                     .forEach(writer::println);
            }
        } catch (IOException e) {
            log.error("Exception happened", e);
        }
    }

    public String getLogFilePath() {
        Path p = decidePath();
        if (p == null) {
            return ERROR_MESSAGE;
        } else {
            return p.toAbsolutePath().toString();
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Path decidePath() {
        if (logPathList.isEmpty()) {
            return null;
        } else if (logPathList.size() == 1) {
            return logPathList.get(0);
        } else {
            return rollTheDice();
        }
    }

    /**
     * If the user configured multiple file appenders, which log file should we choose?
     * Clearly, the only sane solution is rolling the dice.
     * Easter egg mode: On
     */
    private Path rollTheDice() {
        log.trace("Rolling the dice...");
        int index = random.nextInt(logPathList.size());
        return logPathList.get(index);
    }

    /**
     * We cannot presume that the default file name/location setting won't be changed by the user.
     * Therefore, we should be able to retrieve that info from the underlying logging mechanism
     * by iterating over appenders.
     */
    private List<Path> getActiveLogFilePaths() {
        LoggerContextFactory factory = LogManager.getFactory();
        ContextSelector selector = ((Log4jContextFactory) factory).getSelector();

        List<Path> fileNameList = new ArrayList<>();
        for (LoggerContext ctx : selector.getLoggerContexts()) {
            for (Appender appender : ctx.getConfiguration().getAppenders().values()) {
                String fileName = extractFileName(appender);
                if (fileName != null) {
                    fileNameList.add(Paths.get(fileName));
                }
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
