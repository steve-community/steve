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

import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.core.WebSocketConstants;
import org.eclipse.jetty.websocket.server.JettyWebSocketServerContainer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;
import static de.rwth.idsg.steve.config.WebSocketConfiguration.IDLE_TIMEOUT;
import static de.rwth.idsg.steve.config.WebSocketConfiguration.MAX_MSG_SIZE;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 07.04.2015
 */
public class SteveAppContext {

    private final AnnotationConfigWebApplicationContext springContext;
    private final WebAppContext webAppContext;

    public SteveAppContext() {
        springContext = new AnnotationConfigWebApplicationContext();
        springContext.scan("de.rwth.idsg.steve.config");
        webAppContext = initWebApp();
    }

    public HandlerCollection getHandlers() {
        return new HandlerList(
            getRedirectHandler(),
            getWebApp()
        );
    }

    /**
     * Otherwise, defaults come from {@link WebSocketConstants}
     */
    public void configureWebSocket() {
        JettyWebSocketServerContainer container = JettyWebSocketServerContainer.getContainer(webAppContext.getServletContext());
        container.setInputBufferSize(MAX_MSG_SIZE);
        container.setOutputBufferSize(MAX_MSG_SIZE);
        container.setMaxTextMessageSize(MAX_MSG_SIZE);
        container.setIdleTimeout(IDLE_TIMEOUT);
    }

    private Handler getWebApp() {
        if (!CONFIG.getJetty().isGzipEnabled()) {
            return webAppContext;
        }

        // Wraps the whole web app in a gzip handler to make Jetty return compressed content
        // http://www.eclipse.org/jetty/documentation/current/gzip-filter.html
        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(webAppContext);
        return gzipHandler;
    }

    private WebAppContext initWebApp() {
        WebAppContext ctx = new WebAppContext();
        ctx.setContextPath(CONFIG.getContextPath());
        ctx.setResourceBase(getWebAppURIAsString());

        // if during startup an exception happens, do not swallow it, throw it
        ctx.setThrowUnavailableOnStartupException(true);

        // Disable directory listings if no index.html is found.
        ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        ServletHolder web = new ServletHolder("spring-dispatcher", new DispatcherServlet(springContext));
        ServletHolder cxf = new ServletHolder("cxf", new CXFServlet());

        ctx.addEventListener(new ContextLoaderListener(springContext));
        ctx.addServlet(web, CONFIG.getSpringMapping());
        ctx.addServlet(cxf, CONFIG.getCxfMapping());

        if (CONFIG.getProfile().isProd()) {
            // If PROD, add security filter
            ctx.addFilter(
                // The bean name is not arbitrary, but is as expected by Spring
                new FilterHolder(new DelegatingFilterProxy(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)),
                CONFIG.getSpringManagerMapping(),
                EnumSet.allOf(DispatcherType.class)
            );
        }

        initJSP(ctx);
        return ctx;
    }

    private Handler getRedirectHandler() {
        RewriteHandler rewrite = new RewriteHandler();
        rewrite.setRewriteRequestURI(true);
        rewrite.setRewritePathInfo(true);

        for (String redirect : getRedirectSet()) {
            RedirectPatternRule rule = new RedirectPatternRule();
            rule.setTerminating(true);
            rule.setPattern(redirect);
            rule.setLocation(CONFIG.getContextPath() + "/manager/home");
            rewrite.addRule(rule);
        }
        return rewrite;
    }

    private HashSet<String> getRedirectSet() {
        String path = CONFIG.getContextPath();

        HashSet<String> redirectSet = new HashSet<>(3);
        redirectSet.add("");
        redirectSet.add(path + "");

        // Otherwise (if path = ""), we would already be at root of the server ("/")
        // and using the redirection below would cause an infinite loop.
        if (!"".equals(path)) {
            redirectSet.add(path + "/");
        }

        return redirectSet;
    }

    /**
     * Help by:
     * https://github.com/jetty-project/embedded-jetty-jsp
     * https://github.com/jasonish/jetty-springmvc-jsp-template
     * http://examples.javacodegeeks.com/enterprise-java/jetty/jetty-jsp-example
     */
    private void initJSP(WebAppContext ctx) {
        // Ensure the JSP engine is initialized correctly
        List<ContainerInitializer> initializers = new ArrayList<>();
        initializers.add(new ContainerInitializer(new JettyJasperInitializer(), null));

        ctx.setAttribute("org.eclipse.jetty.containerInitializers", initializers);
        ctx.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
        ctx.addBean(new ServletContainerInitializersStarter(ctx), true);
    }

    private static String getWebAppURIAsString() {
        try {
            return new ClassPathResource("webapp").getURI().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
