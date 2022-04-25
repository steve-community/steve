/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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

import de.rwth.idsg.steve.config.WebSocketConfiguration;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
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

    private final DefaultHandshakeHandler delegate;
    private final List<AbstractWebSocketEndpoint> endpoints;
    private final ChargePointHelperService chargePointHelperService;

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
        Optional<RegistrationStatus> status = chargePointHelperService.getRegistrationStatus(chargeBoxId);

        // Allow connections, if station is in db (registration_status field from db does not matter)
        boolean allowConnection = status.isPresent();

        if (!allowConnection) {
            log.error("ChargeBoxId '{}' is not recognized.", chargeBoxId);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        attributes.put(AbstractWebSocketEndpoint.CHARGEBOX_ID_KEY, chargeBoxId);

        // -------------------------------------------------------------------------
        // 2. Route according to the selected protocol
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

        log.debug("ChargeBoxId '{}' will be using {}", chargeBoxId, endpoint.getClass().getSimpleName());
        return delegate.doHandshake(request, response, endpoint, attributes);
    }

    private AbstractWebSocketEndpoint selectEndpoint(List<String> requestedProtocols ) {
        for (String requestedProcotol : requestedProtocols) {
            for (AbstractWebSocketEndpoint item : endpoints) {
                if (item.getVersion().getValue().equals(requestedProcotol)) {
                    return item;
                }
            }
        }
        return null;
    }
}
