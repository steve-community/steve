package de.rwth.idsg.steve.ocpp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class OcppProtocolTest {

    @ParameterizedTest
    @EnumSource(OcppProtocol.class)
    public void testFromCompositeValue(OcppProtocol input) {
        String toTest = input.getCompositeValue();
        OcppProtocol inputBack = OcppProtocol.fromCompositeValue(toTest);
        Assertions.assertEquals(input, inputBack);
    }
}
