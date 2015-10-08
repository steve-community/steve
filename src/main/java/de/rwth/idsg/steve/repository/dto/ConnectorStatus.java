package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Getter
@Builder
public final class ConnectorStatus {
    private final String chargeBoxId, timeStamp, status, errorCode;
    private final int connectorId;
}
