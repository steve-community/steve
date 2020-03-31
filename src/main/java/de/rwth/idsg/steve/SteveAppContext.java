/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2020 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 07.04.2015
 */
public class SteveAppContext {

    private AnnotationConfigWebApplicationContext springContext;

    public SteveAppContext() {
        springContext = new AnnotationConfigWebApplicationContext();
        springContext.scan("de.rwth.idsg.steve.config");
    }

    public HandlerCollection getHandlers() {
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(
                new Handler[]{
                        getRedirectHandler(),
                        getWebApp()
                });
        return handlerList;
    }

    private Handler getWebApp() {
        if (CONFIG.getJetty().isGzipEnabled()) {
            return enableGzip(initWebApp());
        } else {
            return initWebApp();
        }
    }

    /**
     * Wraps the whole web app in a gzip handler to make Jetty return compressed content
     *
     * http://www.eclipse.org/jetty/documentation/current/gzip-filter.html
     */
    private Handler enableGzip(WebAppContext ctx) {
        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(ctx);
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
            addSecurityFilter(ctx);
        }

        initJSP(ctx);
        return ctx;
    }

    private void addSecurityFilter(WebAppContext ctx) {
        // The bean name is not arbitrary, but is as expected by Spring
        Filter f = new DelegatingFilterProxy(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME);

        ctx.addFilter(
                new FilterHolder(f),
                CONFIG.getSpringManagerMapping(),
                EnumSet.allOf(DispatcherType.class)
        );
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

    private static String getWebAppURIAsString() {
        try {
            return new ClassPathResource("webapp").getURI().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------------------------------------------------------
    // JSP stuff
    //
    // Help by:
    //
    // https://github.com/jetty-project/embedded-jetty-jsp
    // https://github.com/jasonish/jetty-springmvc-jsp-template
    // http://examples.javacodegeeks.com/enterprise-java/jetty/jetty-jsp-example
    // -------------------------------------------------------------------------

    private void initJSP(WebAppContext ctx) {
        ctx.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        ctx.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
        ctx.addBean(new ServletContainerInitializersStarter(ctx), true);
    }

    /**
     * Ensure the JSP engine is initialized correctly
     */
    private List<ContainerInitializer> jspInitializers() {
        List<ContainerInitializer> initializers = new ArrayList<>();
        initializers.add(new ContainerInitializer(new JettyJasperInitializer(), null));
        return initializers;
    }
}
