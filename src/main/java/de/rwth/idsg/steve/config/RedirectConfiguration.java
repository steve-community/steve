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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Global configuration to ensure all redirects use absolute URLs with the correct hostname and port.
 * This is especially important when the application is behind a reverse proxy.
 * 
 * With server.forward-headers-strategy = native, Spring Boot should automatically use
 * X-Forwarded-* headers. This interceptor ensures all RedirectView instances generate absolute URLs.
 * 
 * @author Auto-generated
 */
@Configuration
public class RedirectConfiguration implements WebMvcConfigurer {

    @Value("${server.external-hostname:}")
    private String externalHostname;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                                 Object handler, ModelAndView modelAndView) {
                if (modelAndView != null) {
                    View view = modelAndView.getView();
                    
                    // Handle RedirectView instances
                    if (view instanceof RedirectView) {
                        RedirectView redirectView = (RedirectView) view;
                        configureRedirectView(redirectView);
                    } 
                    // Handle string-based redirects (before view resolution)
                    else if (modelAndView.getViewName() != null 
                            && modelAndView.getViewName().startsWith("redirect:")) {
                        String redirectUrl = modelAndView.getViewName().substring("redirect:".length());
                        RedirectView redirectView = new RedirectView(redirectUrl, false);
                        configureRedirectView(redirectView);
                        modelAndView.setView(redirectView);
                    }
                }
            }
        }).order(Ordered.HIGHEST_PRECEDENCE);
    }
    
    private void configureRedirectView(RedirectView redirectView) {
        // Always generate absolute URLs (not context-relative)
        redirectView.setContextRelative(false);
        
        // If external hostname is explicitly configured, use it
        // Otherwise, rely on X-Forwarded-* headers (via server.forward-headers-strategy = native)
        if (externalHostname != null && !externalHostname.isEmpty()) {
            String host = externalHostname;
            // Remove protocol if present
            if (host.startsWith("http://") || host.startsWith("https://")) {
                host = host.replaceFirst("https?://", "");
            }
            // Extract hostname (remove port if present - port comes from X-Forwarded-Port)
            if (host.contains(":")) {
                host = host.split(":")[0];
            }
            redirectView.setHosts(host);
        }
    }
}

