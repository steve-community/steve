package de.rwth.idsg.steve.ocpp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.03.2015
 */
@RequiredArgsConstructor
@Getter
public enum OcppProtocol {
    V_12_SOAP(OcppVersion.V_12, OcppTransport.SOAP),
    V_12_JSON(OcppVersion.V_12, OcppTransport.JSON),

    V_15_SOAP(OcppVersion.V_15, OcppTransport.SOAP),
    V_15_JSON(OcppVersion.V_15, OcppTransport.JSON),

    V_16_SOAP(OcppVersion.V_16, OcppTransport.SOAP),
    V_16_JSON(OcppVersion.V_16, OcppTransport.JSON);

    private final OcppVersion version;
    private final OcppTransport transport;

    public String getCompositeValue() {
        return version.getValue() + transport.getValue();
    }

    public static OcppProtocol fromCompositeValue(String v) {

        // If we, in future, decide to use values
        // containing more than one character for OcppTransport,
        // this will break.
        //
        int splitIndex = v.length() - 1;

        String version = v.substring(0, splitIndex);
        String transport = String.valueOf(v.charAt(splitIndex));

        OcppVersion ov = OcppVersion.fromValue(version);
        OcppTransport ot = OcppTransport.fromValue(transport);

        for (OcppProtocol c: OcppProtocol.values()) {
            if (c.getVersion() == ov && c.getTransport() == ot) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
