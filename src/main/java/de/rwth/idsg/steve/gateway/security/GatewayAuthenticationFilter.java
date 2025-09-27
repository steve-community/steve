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
package de.rwth.idsg.steve.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.gateway.repository.GatewayPartnerRepository;
import jooq.steve.db.tables.records.GatewayPartnerRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Token ";

    private final GatewayPartnerRepository partnerRepository;
    private final TokenEncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!isGatewayEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            log.warn("Missing or invalid Authorization header for {}", path);
            sendUnauthorizedResponse(response, "Missing or invalid Authorization header");
            return;
        }

        String encryptedToken = authHeader.substring(TOKEN_PREFIX.length()).trim();

        try {
            String token = encryptionService.decrypt(encryptedToken);

            Optional<GatewayPartnerRecord> partnerOpt = partnerRepository.findByToken(token);

            if (partnerOpt.isEmpty()) {
                log.warn("Invalid token for {}", path);
                sendUnauthorizedResponse(response, "Invalid token");
                return;
            }

            GatewayPartnerRecord partner = partnerOpt.get();

            if (!partner.getEnabled()) {
                log.warn("Partner {} is disabled", partner.getName());
                sendUnauthorizedResponse(response, "Partner disabled");
                return;
            }

            if (!isAuthorizedForPath(partner, path)) {
                log.warn("Partner {} not authorized for {}", partner.getName(), path);
                sendForbiddenResponse(response, "Not authorized for this resource");
                return;
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                partner,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_GATEWAY_PARTNER"))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authenticated partner: {} for {}", partner.getName(), path);

            filterChain.doFilter(request, response);

        } catch (TokenEncryptionService.EncryptionException e) {
            log.error("Token decryption failed", e);
            sendUnauthorizedResponse(response, "Invalid token format");
        }
    }

    private boolean isGatewayEndpoint(String path) {
        return path.startsWith("/ocpi/") || path.startsWith("/oicp/");
    }

    private boolean isAuthorizedForPath(GatewayPartnerRecord partner, String path) {
        if (path.startsWith("/ocpi/")) {
            return partner.getProtocol().getLiteral().equals("OCPI");
        }
        if (path.startsWith("/oicp/")) {
            return partner.getProtocol().getLiteral().equals("OICP");
        }
        return false;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), message);
    }

    private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        sendErrorResponse(response, HttpStatus.FORBIDDEN.value(), message);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status_code", status);
        errorResponse.put("status_message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}