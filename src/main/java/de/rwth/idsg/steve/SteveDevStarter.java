package de.rwth.idsg.steve;

/**
 * ApplicationStarter for DEV profile
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 05.11.2015
 */
public class SteveDevStarter implements ApplicationStarter {

    @Override
    public void start() throws Exception {
        JettyServer jettyServer = new JettyServer();
        jettyServer.prepare();
        jettyServer.start();
        jettyServer.join();
    }
}
