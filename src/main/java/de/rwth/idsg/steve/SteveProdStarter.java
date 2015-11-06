package de.rwth.idsg.steve;

import de.rwth.idsg.steve.utils.LogFileRetriever;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * ApplicationStarter for PROD profile
 *
 * Since we log everything to a file, it can be confusing for the user to see nothing written to console, when starting
 * the app. So, this class prints some stuff to console.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 05.11.2015
 */
@Slf4j
public class SteveProdStarter implements ApplicationStarter {

    private static final String HINT = "Hint: You can stop the application by pressing CTRL+C";
    private static final String REFER = "Please refer to the log file for details";

    private Thread dotThread;

    /**
     * TODO: Is this flow correct? Not sure about the error handling. Do the branches cover all situations?
     */
    @Override
    public void start() throws Exception {

        starting();
        JettyServer jettyServer = new JettyServer();

        try {
            jettyServer.prepare();
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

        jettyServer.join();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void starting() {
        String msg = "Log file: "
                + new LogFileRetriever().getLogFilePathOrErrorMessage()
                + System.lineSeparator()
                + "Starting";

        System.out.print(msg);
        startPrintingDots();
    }

    private void started() {
        stopPrintingDots();
        String msg = " Done!" + System.lineSeparator() + HINT;
        System.out.println(msg);
    }

    private void startedWithErrors() {
        String msg = " Done, but there were some errors! " + REFER + System.lineSeparator() + HINT;
        System.out.println(msg);
    }

    private void failed() {
        String msg = " FAILED!" + System.lineSeparator() + REFER;
        System.out.println(msg);
    }

    // -------------------------------------------------------------------------
    // Let's print some dots
    // -------------------------------------------------------------------------

    private void startPrintingDots() {
        dotThread = new Thread() {
            public void run() {
                try {
                    while (!isInterrupted()) {
                        System.out.print(".");
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
