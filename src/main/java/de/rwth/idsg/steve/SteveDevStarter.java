package de.rwth.idsg.steve;

/**
 * ApplicationStarter for DEV profile
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 05.11.2015
 */
public class SteveDevStarter implements ApplicationStarter {

    private final JettyServer jettyServer;

    SteveDevStarter() {
        this.jettyServer = new JettyServer();
    }

    @Override
    public void start() throws Exception {
        jettyServer.start();
    }

    @Override
    public void stop() throws Exception {
        jettyServer.stop();
    }

    @Override
    public void join() throws Exception {
        jettyServer.join();
    }
}
