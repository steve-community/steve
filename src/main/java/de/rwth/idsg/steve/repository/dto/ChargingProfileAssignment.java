package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 16.11.2018
 */
@Getter
@Builder
public class ChargingProfileAssignment {
    private final int chargeBoxPk, connectorId, chargingProfilePk;
    private final String chargeBoxId, chargingProfileDescription;
}
