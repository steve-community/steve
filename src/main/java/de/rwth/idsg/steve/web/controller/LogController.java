package de.rwth.idsg.steve.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Slf4j
@Controller
@RequestMapping(value = "/manager")
public class LogController {

    private final Path logPath = Paths.get(System.getProperty("user.home"), "logs", "steve.log");

    @RequestMapping(value = "/log", method = RequestMethod.GET)
    public void log(HttpServletResponse response) {

        try (PrintWriter writer = response.getWriter()) {
            response.setContentType("text/plain");

            Files.lines(logPath, StandardCharsets.UTF_8)
                 .forEach(writer::println);

        } catch (IOException e) {
            log.error("Exception happened", e);
        }
    }

    public String getLogFilePath() {
        return logPath.toAbsolutePath().toString();
    }
}
