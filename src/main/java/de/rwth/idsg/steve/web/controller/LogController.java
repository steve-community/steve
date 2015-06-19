package de.rwth.idsg.steve.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Slf4j
@Controller
@RequestMapping(value = "/manager")
public class LogController {

    private final File logDir = new File(System.getProperty("user.home"), "logs");
    private final File logFile = new File(logDir, "steve.log");

    @RequestMapping(value = "/log", method = RequestMethod.GET)
    public void log(HttpServletResponse response) {
        try (InputStreamReader ist = new InputStreamReader(new FileInputStream(logFile), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(ist);
             PrintWriter writer = response.getWriter()) {

            response.setContentType("text/plain");
            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                writer.println(sCurrentLine);
            }
        } catch (IOException e) {
            log.error("Exception happened", e);
        }
    }

    public String getLogFilePath() {
        return logFile.getAbsolutePath();
    }
}