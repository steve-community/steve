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
import org.eclipse.jetty.compression.server.CompressionHandler;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.12.2014
 */
@Slf4j
public class JettyServer {

    private final SteveConfiguration config;

    private @Nullable Server server;

    private static final int MIN_THREADS = 4;
    private static final int MAX_THREADS = 50;

    private static final long STOP_TIMEOUT = TimeUnit.SECONDS.toMillis(5);
    private static final long IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(1);

    // scan all jars in the classpath for ServletContainerInitializers
    // (e.g. Spring's WebApplicationInitializer)
    private static final String SCAN_PATTERN = ".*\\.jar$|.*/classes/.*";

    // ClosedFileSystemException if the resource factory is linked to the context
    // https://github.com/jetty/jetty.project/issues/13592
    private final ResourceFactory.Closeable webAppResourceFactory = ResourceFactory.closeable();

    public JettyServer(SteveConfiguration config) {
        this.config = config;
    }

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
        httpConfig.setSecurePort(config.getJetty().getHttpsPort());
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

        if (config.getJetty().isHttpEnabled()) {
            server.addConnector(createHttpConnector(server, config, httpConfig));
        }

        if (config.getJetty().isHttpsEnabled()) {
            server.addConnector(createHttpsConnector(server, config, httpConfig));
        }

        server.setHandler(createWebApp(config, webAppResourceFactory));
    }

    private static ServerConnector createHttpConnector(
            Server server, SteveConfiguration config, HttpConfiguration httpConfig) {
        // === jetty-http.xml ===
        var http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        http.setHost(config.getJetty().getServerHost());
        http.setPort(config.getJetty().getHttpPort());
        http.setIdleTimeout(IDLE_TIMEOUT);
        return http;
    }

    private static ServerConnector createHttpsConnector(
            Server server, SteveConfiguration config, HttpConfiguration httpConfig) {
        // === jetty-https.xml ===
        // SSL Context Factory
        var sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(config.getJetty().getKeyStorePath());
        sslContextFactory.setKeyStorePassword(config.getJetty().getKeyStorePassword());
        sslContextFactory.setKeyManagerPassword(config.getJetty().getKeyStorePassword());

        // SSL HTTP Configuration
        var httpsConfig = new HttpConfiguration(httpConfig);
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        // SSL Connector
        var https = new ServerConnector(
                server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(httpsConfig));
        https.setHost(config.getJetty().getServerHost());
        https.setPort(config.getJetty().getHttpsPort());
        https.setIdleTimeout(IDLE_TIMEOUT);
        return https;
    }

    private static Handler createWebApp(SteveConfiguration config, ResourceFactory webAppResourceFactory) {
        var webAppContext = new WebAppContext();
        webAppContext.setContextPath(config.getPaths().getContextPath());
        webAppContext.setBaseResource(webAppResourceFactory.newClassLoaderResource("webapp/"));

        // if during startup an exception happens, do not swallow it, throw it
        webAppContext.setThrowUnavailableOnStartupException(true);

        // Disable directory listings if no index.html is found.
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        // Crucial for Spring's WebApplicationInitializer to be discovered
        // and for the DispatcherServlet to be initialized.
        // It tells Jetty to scan for classes implementing WebApplicationInitializer.
        // The pattern ensures that Jetty finds the Spring classes in the classpath.
        //
        // https://jetty.org/docs/jetty/12.1/programming-guide/maven-jetty/jetty-maven-plugin.html
        // https://jetty.org/docs/jetty/12.1/operations-guide/annotations/index.html#og-container-include-jar-pattern
        webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", SCAN_PATTERN);

        if (config.getJetty().isGzipEnabled()) {
            // Insert compression in front of the webapp handler chain, keep the context intact
            webAppContext.insertHandler(new CompressionHandler());
        }
        return webAppContext;
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
            webAppResourceFactory.close();
        }
    }
}
