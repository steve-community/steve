package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Getter
@Builder
public final class ConnectorStatus {
    private final String chargeBoxId, timeStamp, status, errorCode;
    private final int chargeBoxPk, connectorId;

    // For additional internal processing. Not related to the humanized
    // String version above, which is for representation on frontend
    private final DateTime statusTimestamp;

}
