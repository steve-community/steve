package de.rwth.idsg.steve;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.12.2014
 */
@Slf4j
public class JettyServer {

    private Server server;

    private static final int MIN_THREADS = 4;
    private static final int MAX_THREADS = 50;

    private static final long STOP_TIMEOUT = TimeUnit.SECONDS.toMillis(5);
    private static final long IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(1);

    /**
     * A fully configured Jetty Server instance
     */
    public void prepare() throws Exception {

        // === jetty.xml ===
        // Setup Threadpool
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(MIN_THREADS);
        threadPool.setMaxThreads(MAX_THREADS);

        // Server
        server = new Server(threadPool);

        // Scheduler
        server.addBean(new ScheduledExecutorScheduler());

        // HTTP Configuration
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSecureScheme(HttpScheme.HTTPS.asString());
        httpConfig.setSecurePort(CONFIG.getJetty().getHttpsPort());
        httpConfig.setOutputBufferSize(32768);
        httpConfig.setRequestHeaderSize(8192);
        httpConfig.setResponseHeaderSize(8192);
        httpConfig.setSendServerVersion(false);
        httpConfig.setSendDateHeader(false);
        httpConfig.setSendXPoweredBy(false);

        // Extra options
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);
        server.setStopTimeout(STOP_TIMEOUT);

        if (CONFIG.getJetty().isHttpEnabled()) {
            server.addConnector(httpConnector(httpConfig));
        }

        if (CONFIG.getJetty().isHttpsEnabled()) {
            server.addConnector(httpsConnector(httpConfig));
        }

        SteveAppContext steveAppContext = new SteveAppContext();
        server.setHandler(steveAppContext.getHandlers());
    }

    private ServerConnector httpConnector(HttpConfiguration httpConfig) {
        // === jetty-http.xml ===
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        http.setHost(CONFIG.getJetty().getServerHost());
        http.setPort(CONFIG.getJetty().getHttpPort());
        http.setIdleTimeout(IDLE_TIMEOUT);
        return http;
    }

    private ServerConnector httpsConnector(HttpConfiguration httpConfig) {
        // === jetty-https.xml ===
        // SSL Context Factory
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(CONFIG.getJetty().getKeyStorePath());
        sslContextFactory.setKeyStorePassword(CONFIG.getJetty().getKeyStorePassword());
        sslContextFactory.setKeyManagerPassword(CONFIG.getJetty().getKeyStorePassword());
        sslContextFactory.setExcludeCipherSuites(
                "SSL_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

        // SSL HTTP Configuration
        HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        // SSL Connector
        ServerConnector https = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(httpsConfig));
        https.setHost(CONFIG.getJetty().getServerHost());
        https.setPort(CONFIG.getJetty().getHttpsPort());
        https.setIdleTimeout(IDLE_TIMEOUT);
        return https;
    }

    /**
     * Starts the Jetty Server instance
     */
    public void start() throws Exception {
        if (server != null) {
            server.start();
        }
    }

    /**
     * Join the server thread with the current thread
     */
    public void join() throws Exception {
        if (server != null) {
            server.join();
        }
    }

    public boolean isStarted() {
        return server != null && server.isStarted();
    }

    public List<String> getConnectorPathList() {
        if (server == null) {
            return Collections.emptyList();
        }

        Connector[] connectors = server.getConnectors();
        List<String> list = new ArrayList<>(connectors.length);
        for (Connector c : connectors) {
            list.add(getConnectorPath((ServerConnector) c));
        }
        return list;
    }

    private String getConnectorPath(ServerConnector sc) {
        String prefix = "http";
        String host = sc.getHost();
        int port = sc.getPort();

        ConnectionFactory cf = sc.getDefaultConnectionFactory();
        if (cf instanceof SslConnectionFactory) {
            prefix = "https";
        }

        if (host == null || host.equals("0.0.0.0")) {
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                // Well, we failed to read from system, fall back to main.properties.
                // Better than nothing
                host = CONFIG.getJetty().getServerHost();
            }
        }

        String layout = "%s://%s:%d" + CONFIG.getContextPath();

        return String.format(layout, prefix, host, port);
    }
}
