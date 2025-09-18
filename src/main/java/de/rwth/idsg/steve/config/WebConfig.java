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

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import java.util.EnumSet;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * Jetty will automatically detect this class because of {@link de.rwth.idsg.steve.JettyServer#SCAN_PATTERN} and
 * use it to initialize the Spring and servlets and filters.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.09.2025
 */
@Slf4j
public class WebConfig implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("Initializing");

        // Spring root context
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        springContext.scan("de.rwth.idsg.steve.config");
        servletContext.addListener(new ContextLoaderListener(springContext));

        // Spring MVC
        var dispatcher = servletContext.addServlet("spring-dispatcher", new DispatcherServlet(springContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping(CONFIG.getSpringMapping());

        // Apache CXF
        var cxf = servletContext.addServlet("cxf", new CXFServlet());
        cxf.setLoadOnStartup(1);
        cxf.addMapping(CONFIG.getCxfMapping() + "/*");

        // add spring security
        servletContext
            .addFilter(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, "org.springframework.web.filter.DelegatingFilterProxy")
            .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, CONFIG.getSpringMapping() + "*");
    }
}
