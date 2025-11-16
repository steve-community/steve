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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.session.InvalidSessionStrategy;

import java.io.IOException;

/**
 * Custom InvalidSessionStrategy that prevents redirect loops.
 * If the request is already for the signin page, it doesn't redirect again.
 */
@Slf4j
public class NoRedirectInvalidSessionStrategy implements InvalidSessionStrategy {

    private final String invalidSessionUrl;
    private final String signinPath;

    public NoRedirectInvalidSessionStrategy(String invalidSessionUrl, String signinPath) {
        this.invalidSessionUrl = invalidSessionUrl;
        this.signinPath = signinPath;
    }

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestPath = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        // Remove context path from request path for comparison
        String pathWithoutContext = requestPath;
        if (contextPath != null && !contextPath.isEmpty() && requestPath.startsWith(contextPath)) {
            pathWithoutContext = requestPath.substring(contextPath.length());
        }
        
        // Check if we're already on the signin page
        if (pathWithoutContext.equals(signinPath) || pathWithoutContext.equals(invalidSessionUrl)) {
            log.debug("Already on signin page ({}), not redirecting to avoid loop", pathWithoutContext);
            // Don't redirect - just let the request proceed
            // The signin page should be accessible without a valid session
            return;
        }
        
        log.debug("Invalid session detected, redirecting from {} to {}", pathWithoutContext, invalidSessionUrl);
        // Redirect to signin page
        response.sendRedirect(invalidSessionUrl);
    }
}

