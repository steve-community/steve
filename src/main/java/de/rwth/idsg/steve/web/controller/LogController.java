package de.rwth.idsg.steve.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@Slf4j
@Controller
public class LogController {

    private final File logDir = new File(System.getProperty("user.home"), "logs");
    private final File logFile = new File(logDir, "steve.log");

    @RequestMapping(value = "/log", method = RequestMethod.GET)
    public void log(HttpServletResponse response) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
             PrintWriter writer = response.getWriter()) {

            response.setContentType("text/plain");
            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                writer.println(sCurrentLine);
            }
        } catch (Exception e) {
            log.error("Exception happened", e);
        }
    }

    public String getLogFilePath() {
        return logFile.getAbsolutePath();
    }
}