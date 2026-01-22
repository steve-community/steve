/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.repository.dto.ChargePointRegistration;
import de.rwth.idsg.steve.service.CertificateValidator;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.web.validation.ChargeBoxIdValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.rwth.idsg.steve.utils.StringUtils.getLastBitFromUrl;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 05.03.2022
 */
@Slf4j
@RequiredArgsConstructor
public class OcppWebSocketHandshakeHandler implements HandshakeHandler {

    private final ChargeBoxIdValidator chargeBoxIdValidator;
    private final DefaultHandshakeHandler delegate;
    private final List<AbstractWebSocketEndpoint> endpoints;
    private final ChargePointService chargePointService;
    private final CertificateValidator certificateValidator;

    private final BasicAuthenticationConverter converter = new BasicAuthenticationConverter();

    /**
     * We need some WebSocketHandler just for Spring to register it for the path. We will not use it for the actual
     * operations. This instance will be passed to doHandshake(..) below. We will find the proper WebSocketEndpoint
     * based on the selectedProtocol and replace the dummy one with the proper one in the subsequent call chain.
     */
    public WebSocketHandler getDummyWebSocketHandler() {
        return new TextWebSocketHandler();
    }

    @Override
    public boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Map<String, Object> attributes) throws HandshakeFailureException {

        // -------------------------------------------------------------------------
        // 1. Check the chargeBoxId
        // -------------------------------------------------------------------------

        String chargeBoxId = getLastBitFromUrl(request.getURI().getPath());
        boolean isValid = chargeBoxIdValidator.isValid(chargeBoxId);
        if (!isValid) {
            log.error("ChargeBoxId '{}' violates the configured pattern.", chargeBoxId);
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        Optional<ChargePointRegistration> registration = chargePointService.getRegistration(chargeBoxId);

        // Allow connections, if station is in DB and its registration_status is not Rejected
        boolean allowConnection = registration.isPresent() && registration.get().registrationStatus() != RegistrationStatus.REJECTED;

        // https://github.com/steve-community/steve/issues/1020
        if (!allowConnection) {
            log.error("ChargeBoxId '{}' is not recognized or its registration status is 'Rejected'.", chargeBoxId);
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return false;
        }

        // original value in the connection URL provided by the station might have a different uppercase/lowercase
        // configuration than the one from database. functionally this is not an issue, since the entries in the
        // database are case-insensitive. but still, let's use the value from DB from here on (and also reference it in
        // sessions) to prevent confusion.
        chargeBoxId = registration.get().chargeBoxId();

        // -------------------------------------------------------------------------
        // 2. Check Ocpp security profiles (if needed)
        // -------------------------------------------------------------------------

        OcppSecurityProfile profile = registration.get().securityProfile();

        // Basic auth for profiles 1 and 2
        if (profile.requiresBasicAuth()) {
            ServletServerHttpRequest casted = (ServletServerHttpRequest) request;

            UsernamePasswordAuthenticationToken authentication;
            try {
                authentication = converter.convert(casted.getServletRequest());
            } catch (Exception e) {
                log.warn("Failed to extract Authentication from request", e);
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return false;
            }

            boolean valid = chargePointService.validateBasicAuth(registration.get(), authentication);
            if (!valid) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
        }

        // Client cert checks for profile 3
        if (profile == OcppSecurityProfile.Profile_3) {
            var cert = certificateValidator.getCertificate(request);
            boolean valid = certificateValidator.validate(registration.get(), cert);
            if (!valid) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
        }

        // -------------------------------------------------------------------------
        // 3. Route according to the selected protocol
        // -------------------------------------------------------------------------

        List<String> requestedProtocols = new WebSocketHttpHeaders(request.getHeaders()).getSecWebSocketProtocol();

        if (CollectionUtils.isEmpty(requestedProtocols)) {
            log.error("No protocol (OCPP version) is specified.");
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        AbstractWebSocketEndpoint endpoint = selectEndpoint(requestedProtocols);

        if (endpoint == null) {
            log.error("None of the requested protocols '{}' is supported", requestedProtocols);
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return false;
        }

        attributes.put(AbstractWebSocketEndpoint.CHARGEBOX_ID_KEY, chargeBoxId);
        log.debug("ChargeBoxId '{}' will be using {}", chargeBoxId, endpoint.getClass().getSimpleName());
        return delegate.doHandshake(request, response, endpoint, attributes);
    }

    private AbstractWebSocketEndpoint selectEndpoint(List<String> requestedProtocols ) {
        for (String requestedProtocol : requestedProtocols) {
            for (AbstractWebSocketEndpoint item : endpoints) {
                if (item.getVersion().getValue().equals(requestedProtocol)) {
                    return item;
                }
            }
        }
        return null;
    }
}
