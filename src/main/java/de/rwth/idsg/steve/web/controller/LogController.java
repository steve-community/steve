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

    @RequestMapping(value = "/log", method = RequestMethod.GET)
    public void log(HttpServletResponse response) {

        try (PrintWriter writer = response.getWriter()) {
            response.setContentType("text/plain");

            File logDir = new File(System.getProperty("catalina.base"), "logs");
            File cxfLog = new File(logDir, "steve.log");

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(cxfLog))) {
                String sCurrentLine;
                while ((sCurrentLine = bufferedReader.readLine()) != null) {
                    writer.println(sCurrentLine);
                }
            }
        } catch (Exception e) {
            log.error("Exception happened", e);
        }
    }
}