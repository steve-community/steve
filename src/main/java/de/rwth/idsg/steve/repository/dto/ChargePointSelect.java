package de.rwth.idsg.steve.repository.dto;

import lombok.Getter;
import lombok.experimental.Builder;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
@Getter
@Builder
public class ChargePointSelect {
    private final String chargeBoxId, endpointAddress;
}
