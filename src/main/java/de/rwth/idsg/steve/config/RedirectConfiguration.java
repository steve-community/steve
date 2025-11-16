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
                        configureRedirectView(redirectView, request);
                    } 
                    // Handle string-based redirects (before view resolution)
                    else if (modelAndView.getViewName() != null 
                            && modelAndView.getViewName().startsWith("redirect:")) {
                        String redirectUrl = modelAndView.getViewName().substring("redirect:".length());
                        // Construct full URL with port if needed
                        String fullUrl = constructFullUrl(redirectUrl, request);
                        if (fullUrl != null) {
                            // Use the full URL as the redirect target
                            modelAndView.setViewName("redirect:" + fullUrl);
                        } else {
                            RedirectView redirectView = new RedirectView(redirectUrl, false);
                            configureRedirectView(redirectView, request);
                            modelAndView.setView(redirectView);
                        }
                    }
                }
            }
        }).order(Ordered.HIGHEST_PRECEDENCE);
    }
    
    private void configureRedirectView(RedirectView redirectView, HttpServletRequest request) {
        // Always generate absolute URLs (not context-relative)
        redirectView.setContextRelative(false);
        
        // Get hostname and port from X-Forwarded-* headers or external hostname config
        String host = null;
        String port = null;
        
        // Check X-Forwarded-Port header first (most reliable when behind proxy)
        String forwardedPort = request.getHeader("X-Forwarded-Port");
        if (forwardedPort != null && !forwardedPort.isEmpty()) {
            port = forwardedPort;
        }
        
        // Get hostname from X-Forwarded-Host or external hostname config
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        if (forwardedHost != null && !forwardedHost.isEmpty()) {
            // X-Forwarded-Host might include port, so extract just the hostname
            if (forwardedHost.contains(":")) {
                host = forwardedHost.split(":")[0];
                // If port wasn't set from X-Forwarded-Port, try to get it from X-Forwarded-Host
                if (port == null) {
                    port = forwardedHost.substring(forwardedHost.indexOf(":") + 1);
                }
            } else {
                host = forwardedHost;
            }
        }
        
        // Fallback to external hostname config if X-Forwarded-* headers not available
        if (host == null && externalHostname != null && !externalHostname.isEmpty()) {
            String hostname = externalHostname;
            // Remove protocol if present
            if (hostname.startsWith("http://") || hostname.startsWith("https://")) {
                hostname = hostname.replaceFirst("https?://", "");
            }
            // Extract hostname and port
            if (hostname.contains(":")) {
                String[] parts = hostname.split(":", 2);
                host = parts[0];
                if (port == null) {
                    port = parts[1];
                }
            } else {
                host = hostname;
            }
        }
        
        // Set hostname if we have it
        if (host != null && !host.isEmpty()) {
            redirectView.setHosts(host);
        }
        
        // Note: RedirectView doesn't have a direct way to set the port
        // The port should come from X-Forwarded-Port via server.forward-headers-strategy = native
        // But if that's not working, we need to construct the full URL with port
        // For now, we rely on Spring Boot's native forwarded headers support
    }
    
    private String constructFullUrl(String redirectUrl, HttpServletRequest request) {
        // Only construct full URL if redirectUrl is relative and we have forwarded headers
        if (redirectUrl.startsWith("http://") || redirectUrl.startsWith("https://")) {
            return null; // Already absolute
        }
        
        String host = null;
        String port = null;
        String scheme = request.getScheme();
        
        // Get scheme from X-Forwarded-Proto
        if (request.getHeader("X-Forwarded-Proto") != null) {
            scheme = request.getHeader("X-Forwarded-Proto");
        }
        
        // Get hostname and port from X-Forwarded-* headers
        String forwardedPort = request.getHeader("X-Forwarded-Port");
        if (forwardedPort != null && !forwardedPort.isEmpty()) {
            port = forwardedPort;
        }
        
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        if (forwardedHost != null && !forwardedHost.isEmpty()) {
            if (forwardedHost.contains(":")) {
                String[] parts = forwardedHost.split(":", 2);
                host = parts[0];
                if (port == null) {
                    port = parts[1];
                }
            } else {
                host = forwardedHost;
            }
        }
        
        // Fallback to external hostname config
        if (host == null && externalHostname != null && !externalHostname.isEmpty()) {
            String hostname = externalHostname;
            if (hostname.startsWith("http://") || hostname.startsWith("https://")) {
                scheme = hostname.startsWith("https://") ? "https" : "http";
                hostname = hostname.replaceFirst("https?://", "");
            }
            if (hostname.contains(":")) {
                String[] parts = hostname.split(":", 2);
                host = parts[0];
                if (port == null) {
                    port = parts[1];
                }
            } else {
                host = hostname;
            }
        }
        
        // Construct full URL if we have host and port
        if (host != null && !host.isEmpty() && port != null && !port.isEmpty()) {
            // Ensure redirectUrl starts with /
            if (!redirectUrl.startsWith("/")) {
                redirectUrl = "/" + redirectUrl;
            }
            return scheme + "://" + host + ":" + port + redirectUrl;
        }
        
        return null; // Let RedirectView handle it
    }
}

