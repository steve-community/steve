package de.rwth.idsg.steve;

import de.rwth.idsg.steve.utils.LogFileRetriever;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
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

    private static final String HINT = "Hint: You can stop the application by pressing CTRL+C" + sep();
    private static final String REFER = "Please refer to the log file for details";

    private JettyServer jettyServer;
    private Thread dotThread;

    /**
     * TODO: Is this flow correct? Not sure about the error handling. Do the branches cover all situations?
     */
    @Override
    public void start() throws Exception {

        starting();
        jettyServer = new JettyServer();

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
        List<String> list = jettyServer.getConnectorPathList();

        printList(list.iterator(), false,
                "Access the web interface using:",
                "/manager");

        printList(list.iterator(), false,
                "SOAP endpoint for OCPP:",
                "/services/CentralSystemService");

        printList(list.iterator(), true,
                "WebSocket/JSON endpoint for OCPP:",
                "/websocket/CentralSystemService/<chargeBoxId>");
    }

    private void printList(Iterator<String> it, boolean replaceHttp, String title, String elementPostfix) {
        StringBuilder sb  = new StringBuilder(title)
                .append(sep());

        if (it.hasNext()) {
            sb.append("- ")
              .append(getElementPrefix(it.next(), replaceHttp))
              .append(elementPostfix);

            while (it.hasNext()) {
                sb.append(sep())
                  .append("- ")
                  .append(getElementPrefix(it.next(), replaceHttp))
                  .append(elementPostfix);
            }
        }

        println(sb.toString());
    }

    private String getElementPrefix(String str, boolean replaceHttp) {
        if (replaceHttp) {
            return str.replaceFirst("http", "ws");
        } else {
            return str;
        }
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
