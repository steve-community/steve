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

import de.rwth.idsg.steve.web.dto.EndpointInfo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
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

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 12.12.2014
 */
@Slf4j
public class JettyServer {

    private final SteveConfiguration config;
    private final EndpointInfo info;

    private Server server;
    private SteveAppContext steveAppContext;

    private static final int MIN_THREADS = 4;
    private static final int MAX_THREADS = 50;

    private static final long STOP_TIMEOUT = TimeUnit.SECONDS.toMillis(5);
    private static final long IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(1);

    public JettyServer(SteveConfiguration config) {
        this.config = config;
        this.info = new EndpointInfo(config);
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
            server.addConnector(httpConnector(httpConfig));
        }

        if (config.getJetty().isHttpsEnabled()) {
            server.addConnector(httpsConnector(httpConfig));
        }

        steveAppContext = new SteveAppContext(config, info);
        server.setHandler(steveAppContext.getHandlers());
    }

    private ServerConnector httpConnector(HttpConfiguration httpconfig) {
        // === jetty-http.xml ===
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpconfig));
        http.setHost(config.getJetty().getServerHost());
        http.setPort(config.getJetty().getHttpPort());
        http.setIdleTimeout(IDLE_TIMEOUT);
        return http;
    }

    private ServerConnector httpsConnector(HttpConfiguration httpconfig) {
        // === jetty-https.xml ===
        // SSL Context Factory
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(config.getJetty().getKeyStorePath());
        sslContextFactory.setKeyStorePassword(config.getJetty().getKeyStorePassword());
        sslContextFactory.setKeyManagerPassword(config.getJetty().getKeyStorePassword());

        // SSL HTTP Configuration
        HttpConfiguration httpsConfig = new HttpConfiguration(httpconfig);
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        // SSL Connector
        ServerConnector https = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(httpsConfig));
        https.setHost(config.getJetty().getServerHost());
        https.setPort(config.getJetty().getHttpsPort());
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
            steveAppContext.configureWebSocket();
            populateEndpointInfo();
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

    public boolean isStarted() {
        return server != null && server.isStarted();
    }

    public void populateEndpointInfo() {
        if (server == null) {
            return;
        }
        var list = Arrays.stream(server.getConnectors())
                .map(this::getConnectorPath)
                .flatMap(ips -> ips.entrySet().stream())
                .toList();
        setList(list, isSecured -> isSecured ? "https" : "http", info.getWebInterface(), info.getOcppSoap());
        setList(list, isSecured -> isSecured ? "wss" : "ws", info.getOcppWebSocket());
    }

    private Map<String, Boolean> getConnectorPath(Connector c) {
        var sc = (ServerConnector) c;

        var isSecure = sc.getDefaultConnectionFactory() instanceof SslConnectionFactory;
        var layout = "://%s:%d" + config.getPaths().getContextPath();

        return getIps(sc, config.getJetty().getServerHost())
                .stream()
                .map(k -> String.format(layout, k, sc.getPort()))
                .collect(HashMap::new, (m, v) -> m.put(v, isSecure), HashMap::putAll);
    }

    private static Set<String> getIps(ServerConnector sc, String serverHost) {
        var ips = new HashSet<String>();
        var host = sc.getHost();
        if (host == null || host.equals("0.0.0.0")) {
            ips.addAll(getPossibleIpAddresses(serverHost));
        } else {
            ips.add(host);
        }
        return ips;
    }

    private static void setList(List<Map.Entry<String, Boolean>> list, Function<Boolean, String> prefix, EndpointInfo.ItemsWithInfo... items) {
        var ws = list.stream()
                .map(e -> prefix.apply(e.getValue()) + e.getKey())
                .toList();
        for (EndpointInfo.ItemsWithInfo item : items) {
            item.setData(ws);
        }
    }

    /**
     * Uses different APIs to find out the IP of this machine.
     */
    private static List<String> getPossibleIpAddresses(String serverHost) {
        final String host = "treibhaus.informatik.rwth-aachen.de";
        final List<String> ips = new ArrayList<>();

        try {
            ips.add(InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            // fail silently
        }

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, 80));
            ips.add(socket.getLocalAddress().getHostAddress());
        } catch (Exception e) {
            // fail silently
        }

        // https://stackoverflow.com/a/38342964
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ips.add(socket.getLocalAddress().getHostAddress());
        } catch (Exception e) {
            // fail silently
        }

        // https://stackoverflow.com/a/20418809
        try {
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = ifaces.nextElement();
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress() && (inetAddr instanceof Inet4Address)) {
                        ips.add(inetAddr.getHostAddress());
                    }
                }
            }

        } catch (Exception e) {
            // fail silently
        }

        ips.removeIf("0.0.0.0"::equals);

        if (ips.isEmpty()) {
            // Well, we failed to read from system, fall back to main.properties.
            // Better than nothing
            ips.add(serverHost);
        }

        return ips;
    }
}
