package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
public final class OcppTag {

    @Getter
    @Builder
    public static final class Overview {
        private final Integer ocppTagPk, parentOcppTagPk;
        private final String idTag, parentIdTag, expiryDate;
        private final boolean inTransaction, blocked;
    }
}
