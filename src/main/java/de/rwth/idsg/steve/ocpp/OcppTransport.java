package de.rwth.idsg.steve.ocpp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.03.2015
 */
@RequiredArgsConstructor
@Getter
public enum OcppTransport {
    SOAP("S"),  // HTTP with SOAP payloads
    JSON("J");  // WebSocket with JSON payloads

    private final String value;

    public static OcppTransport fromValue(String v) {
        for (OcppTransport c: OcppTransport.values()) {
            if (c.name().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
