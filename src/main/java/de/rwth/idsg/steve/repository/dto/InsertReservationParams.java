package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2016
 */
@Getter
@Builder
public class InsertReservationParams {
    private final String idTag, chargeBoxId;
    private final int connectorId;
    private final DateTime startTimestamp, expiryTimestamp;
}
