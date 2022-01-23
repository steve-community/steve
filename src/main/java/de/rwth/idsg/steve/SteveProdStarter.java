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
package de.rwth.idsg.steve;

import de.rwth.idsg.steve.utils.LogFileRetriever;
import de.rwth.idsg.steve.web.dto.EndpointInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * ApplicationStarter for PROD profile
 *
 * Since we log everything to a file, it can be confusing for the user to see nothing written to console, when starting
 * the app. So, this class prints some stuff to console.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 05.11.2015
 */
@Slf4j
public class SteveProdStarter implements ApplicationStarter {

    private static final String HINT = "Hint: You can stop the application by pressing CTRL+C" + sep();
    private static final String REFER = "Please refer to the log file for details";

    private final JettyServer jettyServer;
    private Thread dotThread;

    SteveProdStarter() {
        this.jettyServer = new JettyServer();
    }

    @Override
    public void start() throws Exception {
        starting();

        try {
            jettyServer.start();
            started();

        } catch (Exception e) {

            stopPrintingDots();
            log.error("Exception happened", e);

            if (jettyServer.isStarted()) {
                startedWithErrors();

            } else {
                failed();
                throw e;
            }
        }
    }

    @Override
    public void join() throws Exception {
        jettyServer.join();
    }

    @Override
    public void stop() throws Exception {
        jettyServer.stop();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void starting() {
        String msg = "Log file: "
                + LogFileRetriever.INSTANCE.getLogFilePathOrErrorMessage()
                + sep()
                + "Starting";

        print(msg);
        startPrintingDots();
    }

    private void started() {
        stopPrintingDots();
        String msg = " Done!" + sep() + HINT;
        println(msg);
        printURLs();
    }

    private void startedWithErrors() {
        String msg = " Done, but there were some errors! " + REFER + sep() + HINT;
        println(msg);
        printURLs();
    }

    private void failed() {
        String msg = " FAILED!" + sep() + REFER;
        println(msg);
    }

    private void printURLs() {
        EndpointInfo info = EndpointInfo.INSTANCE;

        printInfo(info.getWebInterface());
        printInfo(info.getOcppSoap());
        printInfo(info.getOcppWebSocket());
    }

    private void printInfo(EndpointInfo.ItemsWithInfo itemsWithInfo) {
        StringBuilder sb  = new StringBuilder(itemsWithInfo.getInfo())
                .append(sep());

        Iterator<String> it = itemsWithInfo.getData().iterator();

        if (it.hasNext()) {
            sb.append("- ")
              .append(it.next());

            while (it.hasNext()) {
                sb.append(sep())
                  .append("- ")
                  .append(it.next());
            }
        }

        println(sb.toString());
    }

    private static String sep() {
        return System.lineSeparator();
    }

    private static void println(String s) {
        System.out.println(s);
    }

    private static void print(String s) {
        System.out.print(s);
    }

    // -------------------------------------------------------------------------
    // Let's print some dots
    // -------------------------------------------------------------------------

    private void startPrintingDots() {
        dotThread = new Thread() {
            public void run() {
                try {
                    while (!isInterrupted()) {
                        print(".");
                        TimeUnit.MILLISECONDS.sleep(600);
                    }
                } catch (InterruptedException e) {
                    // This is expected, since stopPrintingDots() is called. Do nothing and let the thread end.
                }
            }
        };

        dotThread.start();
    }

    private void stopPrintingDots() {
        if (dotThread != null) {
            dotThread.interrupt();
        }
    }
}
