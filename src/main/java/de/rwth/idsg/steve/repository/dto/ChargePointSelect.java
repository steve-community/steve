package de.rwth.idsg.steve.repository.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
@Getter
@RequiredArgsConstructor
public class ChargePointSelect {
    private final String chargeBoxId, endpointAddress;
}
