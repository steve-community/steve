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
package de.rwth.idsg.steve.config;

import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.utils.LogFileRetriever;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.EnumSet;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 * Jetty will automatically detect this class because of {@link de.rwth.idsg.steve.JettyServer#SCAN_PATTERN} and
 * use it to initialize the Spring and servlets and filters.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.09.2025
 */
@Slf4j
@RequiredArgsConstructor
public class WebConfig implements WebApplicationInitializer {

    // TEMP workaround before spring boot migration
    private static SteveConfiguration config;
    private static LogFileRetriever logFileRetriever;

    public static synchronized void initialize(SteveConfiguration cfg, LogFileRetriever retriever) {
        if (config != null || logFileRetriever != null) {
            throw new IllegalStateException("WebConfig already initialized");
        }
        config = cfg;
        logFileRetriever = retriever;
    }

    public static void clear() {
        config = null;
        logFileRetriever = null;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("Initializing");

        var context = new GenericApplicationContext();
        context.registerBean(SteveConfiguration.class, () -> config);
        context.registerBean(LogFileRetriever.class, () -> logFileRetriever);
        context.refresh();

        // Spring root context
        var springContext = new AnnotationConfigWebApplicationContext();
        springContext.setParent(context);
        springContext.scan("de.rwth.idsg.steve.config");
        servletContext.addListener(new ContextLoaderListener(springContext));

        // Spring MVC
        var web = servletContext.addServlet("spring-dispatcher", new DispatcherServlet(springContext));
        web.setLoadOnStartup(1);
        web.addMapping(config.getPaths().getRootMapping());

        // Apache CXF
        var cxf = servletContext.addServlet("cxf", new CXFServlet());
        cxf.setLoadOnStartup(1);
        cxf.addMapping(config.getPaths().getSoapMapping() + "/*");

        // add spring security
        servletContext
                .addFilter(
                        AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME,
                        DelegatingFilterProxy.class.getName())
                .addMappingForUrlPatterns(
                        EnumSet.allOf(DispatcherType.class),
                        true,
                        config.getPaths().getRootMapping() + "*");
    }
}
