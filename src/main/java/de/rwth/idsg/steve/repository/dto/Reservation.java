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
public final class Reservation {
    private int id;
    private Integer transactionId;
    private String idTag, chargeBoxId, startDatetime, expiryDatetime, status;
}
