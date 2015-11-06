package de.rwth.idsg.steve.web.controller;

import com.google.common.base.Optional;
import de.rwth.idsg.steve.utils.LogFileRetriever;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Slf4j
@Controller
@RequestMapping(value = "/manager")
public class LogController {

    private LogFileRetriever logFileRetriever;

    @PostConstruct
    private void init() {
        logFileRetriever = new LogFileRetriever();
    }

    @RequestMapping(value = "/log", method = RequestMethod.GET)
    public void log(HttpServletResponse response) {
        response.setContentType("text/plain");

        try (PrintWriter writer = response.getWriter()) {
            Optional<Path> p = logFileRetriever.getPath();
            if (p.isPresent()) {
                Files.lines(p.get(), StandardCharsets.UTF_8)
                     .forEach(writer::println);
            } else {
                writer.write(logFileRetriever.getErrorMessage());
            }
        } catch (IOException e) {
            log.error("Exception happened", e);
        }
    }

    public String getLogFilePath() {
        return logFileRetriever.getLogFilePathOrErrorMessage();
    }

}
