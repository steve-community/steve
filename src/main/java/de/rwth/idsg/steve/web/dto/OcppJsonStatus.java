package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 25.03.2015
 */
@Getter
@Builder
public final class OcppJsonStatus {
    private final int chargeBoxPk;
    private final String chargeBoxId, connectedSince;
    private final String connectionDuration;
    private final OcppVersion version;
}
