package de.rwth.idsg.steve.repository.dto;

import lombok.Getter;
import lombok.experimental.Builder;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Getter
@Builder
public final class Heartbeat {
    private final String chargeBoxId, lastTimestamp;
}
