package de.rwth.idsg.steve.ocpp.ws;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OcppWebSocketHandshakeHandlerTest {

    @Test
    public void testGetLastBitFromUrl_empty() {
        String in = "";
        String out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(in);
        Assertions.assertEquals("", out);
    }

    @Test
    public void testGetLastBitFromUrl_null() {
        String in = null;
        String out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(in);
        Assertions.assertEquals("", out);
    }

    @Test
    public void testGetLastBitFromUrl_successFull() {
        String in = "https://www.google.com/steve/websocket/CentralSystemService/BBEI12";
        String out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(in);
        Assertions.assertEquals("BBEI12", out);
    }

    @Test
    public void testGetLastBitFromUrl_noPostfix() {
        String in = "/steve/websocket/CentralSystemService/";
        String out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(in);
        Assertions.assertEquals("", out);
    }

    @Test
    public void testGetLastBitFromUrl_successPartial() {
        String in = "/steve/websocket/CentralSystemService/BBEI12";
        String out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(in);
        Assertions.assertEquals("BBEI12", out);
    }

    @Test
    public void testGetLastBitFromUrl_successWithPercent() {
        String in = "/steve/websocket/CentralSystemService/BBE%I12";
        String out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(in);
        Assertions.assertEquals("BBE%I12", out);
    }

    @Test
    public void testGetLastBitFromUrl_successWithDash() {
        String in = "/steve/websocket/CentralSystemService/BBE-I12";
        String out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(in);
        Assertions.assertEquals("BBE-I12", out);
    }

    @Test
    public void testGetLastBitFromUrl_successWithSpace() {
        String in = "/steve/websocket/CentralSystemService/BBE I12";
        String out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(in);
        Assertions.assertEquals("BBE I12", out);
    }

    @Test
    public void testGetLastBitFromUrl_successWithExtraSlash() {
        String in = "/steve/websocket/CentralSystemService/889/BBEI12";
        String out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(in);
        Assertions.assertEquals("889/BBEI12", out);
    }

    @Test
    public void testGetLastBitFromUrl_successComplex() {
        String in = "/steve/websocket/CentralSystemService/%889 /BBEI12-";
        String out = OcppWebSocketHandshakeHandler.getLastBitFromUrl(in);
        Assertions.assertEquals("%889 /BBEI12-", out);
    }
}
