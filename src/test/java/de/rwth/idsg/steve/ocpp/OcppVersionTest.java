package de.rwth.idsg.steve.ocpp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class OcppVersionTest {

    @ParameterizedTest
    @EnumSource(OcppVersion.class)
    public void testFromValue(OcppVersion input) {
        String toTest = input.getValue();
        OcppVersion inputBack = OcppVersion.fromValue(toTest);
        Assertions.assertEquals(input, inputBack);
    }

    @ParameterizedTest
    @EnumSource(OcppTransport.class)
    public void testToProtocol(OcppTransport transport) {
        for (OcppVersion version : OcppVersion.values()) {
            OcppProtocol protocol = version.toProtocol(transport);

            Assertions.assertEquals(transport, protocol.getTransport());
            Assertions.assertEquals(version, protocol.getVersion());
        }
    }
}
