package net.parkl.ocpp.service;

import net.parkl.ocpp.module.esp.model.ESPChargeBoxConfiguration;
import net.parkl.ocpp.service.middleware.OcppConfigParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OcppConfigParserTest {
    @Test
    public void testParseConfigLines() {
        List<ESPChargeBoxConfiguration> configurations = OcppConfigParser.parseConfList("OCPPBackendJSONURL: ws://ocpp.parkl.net/ocpp/websocket/CentralSystemService");
        assertNotNull(configurations);
        assertNotEquals(0, configurations.size());

        ESPChargeBoxConfiguration configuration = configurations.get(0);
        assertNotNull(configuration);
        Assertions.assertEquals("OCPPBackendJSONURL", configuration.getKey());
        Assertions.assertEquals("ws://ocpp.parkl.net/ocpp/websocket/CentralSystemService", configuration.getValue());
    }
}
