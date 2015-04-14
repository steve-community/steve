package de.rwth.idsg.steve.repository.dto;

import de.rwth.idsg.steve.ocpp.OcppTransport;
import lombok.Getter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
@Getter
public final class ChargePointSelect {
    private final OcppTransport ocppTransport;
    private final String chargeBoxId;
    private final String endpointAddress;

    public ChargePointSelect(OcppTransport ocppTransport, String chargeBoxId, String endpointAddress) {
        this.ocppTransport = ocppTransport;
        this.chargeBoxId = chargeBoxId;
        this.endpointAddress = endpointAddress;
    }

    public ChargePointSelect(OcppTransport ocppTransport, String chargeBoxId) {
        // Provide a non-null value (or placeholder if you will) to frontend for JSON charge points.
        // This is clearly a hack. Not my proudest moment.
        this(ocppTransport, chargeBoxId, "-");
    }

    public boolean isEndpointAddressSet() {
        return !("-".equals(endpointAddress));
    }

    public boolean isSoap() {
        return OcppTransport.SOAP == ocppTransport;
    }
}
