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
 * X-Forwarded-* headers. This interceptor is only needed if native support doesn't work.
 * 
 * DISABLED by default - Spring Boot's native forwarded headers support should handle redirects.
 * Enable by setting steve.redirect-interceptor.enabled=true if needed.
 * 
 * @author Auto-generated
 */
@Configuration
public class RedirectConfiguration implements WebMvcConfigurer {

    @Value("${server.external-hostname:}")
    private String externalHostname;
    
    @Value("${steve.redirect-interceptor.enabled:false}")
    private boolean enabled;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Only register interceptor if explicitly enabled
        // Spring Boot's native forwarded headers support should handle redirects automatically
        if (!enabled) {
            return;
        }
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                                 Object handler, ModelAndView modelAndView) {
                if (modelAndView != null) {
                    String currentPath = request.getRequestURI();
                    
                    // CRITICAL: Skip ALL processing if we're on the signin page to avoid Spring Security loops
                    // This is the key difference between local (no proxy/context path issues) and Kubernetes
                    if (currentPath != null && currentPath.toLowerCase().contains("/signin")) {
                        // Don't modify anything on signin page - let Spring Security handle it natively
                        return;
                    }
                    
                    View view = modelAndView.getView();
                    
                    // Handle RedirectView instances
                    if (view instanceof RedirectView) {
                        RedirectView redirectView = (RedirectView) view;
                        String redirectUrl = redirectView.getUrl();
                        if (redirectUrl != null) {
                            // Skip all signin-related redirects to avoid loops with Spring Security
                            // Check both absolute and relative URLs
                            String urlToCheck = redirectUrl.toLowerCase();
                            if (urlToCheck.contains("/signin")) {
                                // Don't modify Spring Security signin redirects - return immediately
                                return;
                            }
                            // Skip if redirecting to an absolute URL that matches current path
                            if (redirectUrl.startsWith("http://") || redirectUrl.startsWith("https://")) {
                                // Extract path from absolute URL
                                try {
                                    java.net.URL url = new java.net.URL(redirectUrl);
                                    String redirectPath = url.getPath();
                                    if (redirectPath.equals(currentPath) || redirectPath.equals(currentPath + "/")) {
                                        // Redirecting to itself, don't modify
                                        return;
                                    }
                                } catch (java.net.MalformedURLException e) {
                                    // Invalid URL, skip modification
                                    return;
                                }
                            }
                            // Only configure if it's not already redirecting to the same path (avoid loops)
                            if (!redirectUrl.equals(currentPath) && !redirectUrl.equals(request.getContextPath() + currentPath)) {
                                configureRedirectView(redirectView, request);
                            }
                        }
                    } 
                    // Handle string-based redirects (before view resolution)
                    else if (modelAndView.getViewName() != null 
                            && modelAndView.getViewName().startsWith("redirect:")) {
                        String redirectUrl = modelAndView.getViewName().substring("redirect:".length());
                        // Skip if redirecting to an absolute URL (already handled)
                        if (redirectUrl.startsWith("http://") || redirectUrl.startsWith("https://")) {
                            // Already absolute, don't modify
                            return;
                        }
                        // Skip ALL signin-related redirects to avoid loops with Spring Security
                        if (redirectUrl.contains("/signin") || currentPath.contains("/signin")) {
                            // Don't modify Spring Security signin redirects - let Spring Security handle them
                            return;
                        }
                        // Also skip if the redirect URL matches the current path (avoid self-redirects)
                        String contextPath = request.getContextPath();
                        String normalizedRedirect = redirectUrl.startsWith("/") ? redirectUrl : "/" + redirectUrl;
                        String fullRedirectPath = contextPath + normalizedRedirect;
                        String normalizedCurrent = currentPath.endsWith("/") ? currentPath.substring(0, currentPath.length() - 1) : currentPath;
                        if (fullRedirectPath.equals(normalizedCurrent) || normalizedRedirect.equals(normalizedCurrent)) {
                            // Redirecting to itself, don't modify
                            return;
                        }
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
        
        // Skip if redirecting to signin page (avoid loops)
        if (redirectUrl.contains("/signin")) {
            return null;
        }
        
        String currentPath = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        // Normalize redirect URL
        String normalizedRedirect = redirectUrl.startsWith("/") ? redirectUrl : "/" + redirectUrl;
        String fullRedirectPath = contextPath + normalizedRedirect;
        
        // Skip if redirecting to the same path (avoid self-redirects)
        if (fullRedirectPath.equals(currentPath) || normalizedRedirect.equals(currentPath)) {
            return null;
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
            String fullUrl = scheme + "://" + host + ":" + port + normalizedRedirect;
            
            // Double-check: if the constructed URL path matches current path, don't return it
            try {
                java.net.URL url = new java.net.URL(fullUrl);
                String constructedPath = url.getPath();
                if (constructedPath.equals(currentPath) || constructedPath.equals(currentPath + "/")) {
                    return null; // Would redirect to itself
                }
            } catch (java.net.MalformedURLException e) {
                // Invalid URL, return null
                return null;
            }
            
            return fullUrl;
        }
        
        return null; // Let RedirectView handle it
    }
}

