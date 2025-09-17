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

import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashSet;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 07.04.2015
 */
public class SteveAppContext {

    // scan all jars in the classpath for ServletContainerInitializers
    // (e.g. Spring's WebApplicationInitializer)
    private static final String SCAN_PATTERN = ".*\\.jar$|.*/classes/.*";

    private final WebAppContext webAppContext;

    public SteveAppContext() {
        webAppContext = new WebAppContext();
        webAppContext.setContextPath(CONFIG.getContextPath());
        webAppContext.setBaseResourceAsString(getWebAppURIAsString());

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
    }

    public ContextHandlerCollection getHandlers() {
        return new ContextHandlerCollection(
            new ContextHandler(getRedirectHandler()),
            new ContextHandler(getWebApp())
        );
    }

    private Handler getWebApp() {
        if (!CONFIG.getJetty().isGzipEnabled()) {
            return webAppContext;
        }

        // Wraps the whole web app in a gzip handler to make Jetty return compressed content
        // http://www.eclipse.org/jetty/documentation/current/gzip-filter.html
        return new GzipHandler(webAppContext);
    }

    private Handler getRedirectHandler() {
        RewriteHandler rewrite = new RewriteHandler();
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
}
