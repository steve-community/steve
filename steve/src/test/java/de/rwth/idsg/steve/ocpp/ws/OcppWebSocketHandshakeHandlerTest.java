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
package de.rwth.idsg.steve.ocpp.ws;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OcppWebSocketHandshakeHandlerTest {

    private static final String PATH_INFIX = "/steve/websocket/CentralSystemService/";

    @Test
    public void testGetLastBitFromUrl_empty() {
        var in = "";
        var out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(PATH_INFIX, in);
        assertThat(out).isEmpty();
    }

    @Test
    public void testGetLastBitFromUrl_null() {
        String in = null;
        var out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(PATH_INFIX, in);
        assertThat(out).isEmpty();
    }

    @Test
    public void testGetLastBitFromUrl_successFull() {
        var in = "https://www.google.com/steve/websocket/CentralSystemService/BBEI12";
        var out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(PATH_INFIX, in);
        assertThat(out).isEqualTo("BBEI12");
    }

    @Test
    public void testGetLastBitFromUrl_noPostfix() {
        var in = "/steve/websocket/CentralSystemService/";
        var out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(PATH_INFIX, in);
        assertThat(out).isEmpty();
    }

    @Test
    public void testGetLastBitFromUrl_successPartial() {
        var in = "/steve/websocket/CentralSystemService/BBEI12";
        var out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(PATH_INFIX, in);
        assertThat(out).isEqualTo("BBEI12");
    }

    @Test
    public void testGetLastBitFromUrl_successWithPercent() {
        var in = "/steve/websocket/CentralSystemService/BBE%I12";
        var out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(PATH_INFIX, in);
        assertThat(out).isEqualTo("BBE%I12");
    }

    @Test
    public void testGetLastBitFromUrl_successWithDash() {
        var in = "/steve/websocket/CentralSystemService/BBE-I12";
        var out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(PATH_INFIX, in);
        assertThat(out).isEqualTo("BBE-I12");
    }

    @Test
    public void testGetLastBitFromUrl_successWithSpace() {
        var in = "/steve/websocket/CentralSystemService/BBE I12";
        var out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(PATH_INFIX, in);
        assertThat(out).isEqualTo("BBE I12");
    }

    @Test
    public void testGetLastBitFromUrl_successWithExtraSlash() {
        var in = "/steve/websocket/CentralSystemService/889/BBEI12";
        var out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(PATH_INFIX, in);
        assertThat(out).isEqualTo("889/BBEI12");
    }

    @Test
    public void testGetLastBitFromUrl_successComplex() {
        var in = "/steve/websocket/CentralSystemService/%889 /BBEI12-";
        var out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(PATH_INFIX, in);
        assertThat(out).isEqualTo("%889 /BBEI12-");
    }
}
