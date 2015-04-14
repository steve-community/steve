package de.rwth.idsg.steve;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
    public JettyServer() throws IOException {

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
        httpConfig.setSecureScheme("https");
        httpConfig.setSecurePort(SteveConfiguration.Jetty.HTTPS_PORT);
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

        if (SteveConfiguration.Jetty.HTTP_ENABLED) {
            server.addConnector(httpConnector(httpConfig));
        }

        if (SteveConfiguration.Jetty.HTTPS_ENABLED) {
            server.addConnector(httpsConnector(httpConfig));
        }

        SteveAppContext steveAppContext = new SteveAppContext();
        server.setHandler(steveAppContext.getHandlers());
    }

    private ServerConnector httpConnector(HttpConfiguration httpConfig) {
        // === jetty-http.xml ===
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        http.setHost(SteveConfiguration.Jetty.SERVER_HOST);
        http.setPort(SteveConfiguration.Jetty.HTTP_PORT);
        http.setIdleTimeout(IDLE_TIMEOUT);
        return http;
    }

    private ServerConnector httpsConnector(HttpConfiguration httpConfig) {
        // === jetty-https.xml ===
        // SSL Context Factory
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(SteveConfiguration.Jetty.KEY_STORE_PATH);
        sslContextFactory.setKeyStorePassword(SteveConfiguration.Jetty.KEY_STORE_PASSWORD);
        sslContextFactory.setKeyManagerPassword(SteveConfiguration.Jetty.KEY_STORE_PASSWORD);
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
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(httpsConfig));
        https.setHost(SteveConfiguration.Jetty.SERVER_HOST);
        https.setPort(SteveConfiguration.Jetty.HTTPS_PORT);
        https.setIdleTimeout(IDLE_TIMEOUT);
        return https;
    }

    /**
     * Starts the Jetty Server instance
     */
    public void start() {
        try {
            server.start();
            server.join();
        } catch (InterruptedException ie) {
            log.error("Exception happened", ie);
        } catch (Exception e) {
            log.error("Failed to start Jetty server.", e);
        }
    }

    /**
     * Stops the Jetty Server instance
     */
    public void stop() {
        new Thread() {
            @Override
            public void run() {
                try {
                    server.stop();
                } catch (Exception e) {
                    log.error("Failed to stop Jetty server.", e);
                }
            }
        }.start();
    }
}