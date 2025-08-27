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
import jakarta.servlet.DispatcherType;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.ee10.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.ee10.websocket.server.JettyWebSocketServerContainer;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.websocket.core.WebSocketConstants;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static de.rwth.idsg.steve.config.OcppWebSocketConfiguration.IDLE_TIMEOUT;
import static de.rwth.idsg.steve.config.OcppWebSocketConfiguration.MAX_MSG_SIZE;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 07.04.2015
 */
public class SteveAppContext {

    private final SteveConfiguration config;
    private final AnnotationConfigWebApplicationContext springContext;
    private final WebAppContext webAppContext;

    public SteveAppContext(SteveConfiguration config, EndpointInfo info) {
        this.config = config;
        springContext = new AnnotationConfigWebApplicationContext();
        var context = new GenericApplicationContext();
        context.registerBean(SteveConfiguration.class, () -> config);
        context.registerBean(EndpointInfo.class, () -> info);
        context.refresh();
        context.setParent(springContext.getParent());
        springContext.setParent(context);
        springContext.scan("de.rwth.idsg.steve.config");
        webAppContext = initWebApp();
    }

    public ContextHandlerCollection getHandlers() {
        return new ContextHandlerCollection(
            new ContextHandler(getRedirectHandler()),
            new ContextHandler(getWebApp())
        );
    }

    /**
     * Otherwise, defaults come from {@link WebSocketConstants}
     */
    public void configureWebSocket() {
        var container = JettyWebSocketServerContainer.getContainer(webAppContext.getServletContext());
        container.setMaxTextMessageSize(MAX_MSG_SIZE);
        container.setIdleTimeout(IDLE_TIMEOUT);
    }

    private Handler getWebApp() {
        if (!config.getJetty().isGzipEnabled()) {
            return webAppContext;
        }

        // Wraps the whole web app in a gzip handler to make Jetty return compressed content
        // http://www.eclipse.org/jetty/documentation/current/gzip-filter.html
        return new GzipHandler(webAppContext);
    }

    private WebAppContext initWebApp() {
        var ctx = new WebAppContext();
        ctx.setContextPath(config.getPaths().getContextPath());
        ctx.setBaseResourceAsString(getWebAppURIAsString());

        // if during startup an exception happens, do not swallow it, throw it
        ctx.setThrowUnavailableOnStartupException(true);

        // Disable directory listings if no index.html is found.
        ctx.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        ctx.addEventListener(new ContextLoaderListener(springContext));

        var web = new ServletHolder("spring-dispatcher", new DispatcherServlet(springContext));
        ctx.addServlet(web, config.getPaths().getRootMapping());

        var cxf = new ServletHolder("cxf", new CXFServlet());
        ctx.addServlet(cxf, config.getPaths().getSoapMapping() + "/*");

        // add spring security
        ctx.addFilter(
            // The bean name is not arbitrary, but is as expected by Spring
            new FilterHolder(new DelegatingFilterProxy(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)),
            config.getPaths().getRootMapping() + "*",
            EnumSet.allOf(DispatcherType.class)
        );

        initJSP(ctx);
        return ctx;
    }

    private Handler getRedirectHandler() {
        var rewrite = new RewriteHandler();
        for (var redirect : getRedirectSet()) {
            var rule = new RedirectPatternRule();
            rule.setTerminating(true);
            rule.setPattern(redirect);
            rule.setLocation(config.getPaths().getContextPath() + config.getPaths().getManagerMapping() + "/home");
            rewrite.addRule(rule);
        }
        return rewrite;
    }

    private Set<String> getRedirectSet() {
        var path = config.getPaths().getContextPath();

        var redirectSet = HashSet.<String>newHashSet(3);
        redirectSet.add("");
        redirectSet.add(path);

        // Otherwise (if path = ""), we would already be at root of the server ("/")
        // and using the redirection below would cause an infinite loop.
        if (!"".equals(path)) {
            redirectSet.add(path + "/");
        }

        return redirectSet;
    }

    /**
     * Help by:
     * https://github.com/jetty/jetty-examples/tree/12.0.x/embedded/ee10-jsp
     * https://github.com/jetty-project/embedded-jetty-jsp
     * https://github.com/jasonish/jetty-springmvc-jsp-template
     * http://examples.javacodegeeks.com/enterprise-java/jetty/jetty-jsp-example
     */
    private void initJSP(WebAppContext ctx) {
        ctx.addBean(new EmbeddedJspStarter(ctx));
        ctx.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
    }

    private static String getWebAppURIAsString() {
        try {
            return new ClassPathResource("webapp").getURI().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * From: https://github.com/jetty/jetty-examples/blob/12.0.x/embedded/ee10-jsp/src/main/java/examples/EmbeddedJspStarter.java
     *
     * JspStarter for embedded ServletContextHandlers
     *
     * This is added as a bean that is a jetty LifeCycle on the ServletContextHandler.
     * This bean's doStart method will be called as the ServletContextHandler starts,
     * and will call the ServletContainerInitializer for the jsp engine.
     */
    public static class EmbeddedJspStarter extends AbstractLifeCycle {

        private final JettyJasperInitializer sci;
        private final ServletContextHandler context;

        public EmbeddedJspStarter(ServletContextHandler context) {
            this.sci = new JettyJasperInitializer();
            this.context = context;

            // we dont need all this from the example, since our JSPs are precompiled
            //
            // StandardJarScanner jarScanner = new StandardJarScanner();
            // StandardJarScanFilter jarScanFilter = new StandardJarScanFilter();
            // jarScanFilter.setTldScan("taglibs-standard-impl-*");
            // jarScanFilter.setTldSkip("apache-*,ecj-*,jetty-*,asm-*,javax.servlet-*,javax.annotation-*,taglibs-standard-spec-*");
            // jarScanner.setJarScanFilter(jarScanFilter);
            // this.context.setAttribute("org.apache.tomcat.JarScanner", jarScanner);
        }

        @Override
        protected void doStart() throws Exception {
            var old = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(context.getClassLoader());
            try {
                sci.onStartup(null, context.getServletContext());
                super.doStart();
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        }
    }
}
