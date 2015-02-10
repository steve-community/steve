package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
@Getter
@Builder
public class ChargePointSelect {
    private String chargeBoxId, endpointAddress;
}
