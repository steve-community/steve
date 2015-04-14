package de.rwth.idsg.steve.ocpp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The values are as defined in spec "OCPP implementation guide SOAP - RC1 0.6" and in section "5. OCPP version"
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 01.12.2014
 */
@RequiredArgsConstructor
@Getter
public enum OcppVersion {
    V_12("ocpp1.2"),
    V_15("ocpp1.5");

    private final String value;
}
