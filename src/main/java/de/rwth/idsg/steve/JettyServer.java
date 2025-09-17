/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;

import java.util.concurrent.TimeUnit;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.12.2014
 */
@Slf4j
public class JettyServer {

    private Server server;
    private SteveAppContext steveAppContext;

    private static final int MIN_THREADS = 4;
    private static final int MAX_THREADS = 50;

    private static final long STOP_TIMEOUT = TimeUnit.SECONDS.toMillis(5);
    private static final long IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(1);

    /**
     * A fully configured Jetty Server instance
     */
    private void prepare() {

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

        // make sure X-Forwarded-For headers are picked up if set (e.g. by a load balancer)
        // https://github.com/steve-community/steve/pull/570
        httpConfig.addCustomizer(new ForwardedRequestCustomizer());

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

        steveAppContext = new SteveAppContext();
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
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(CONFIG.getJetty().getKeyStorePath());
        sslContextFactory.setKeyStorePassword(CONFIG.getJetty().getKeyStorePassword());
        sslContextFactory.setKeyManagerPassword(CONFIG.getJetty().getKeyStorePassword());

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
        prepare();

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

    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
}
