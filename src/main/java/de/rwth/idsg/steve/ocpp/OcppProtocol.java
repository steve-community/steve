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
    V_15_JSON(OcppVersion.V_15, OcppTransport.JSON);

    private final OcppVersion version;
    private final OcppTransport transport;

    public String getCompositeValue() {
        return version.getValue() + transport.getValue();
    }
}
