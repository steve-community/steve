/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.repository.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;

/**
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 *
 */
public final class OcppTag {

    @Getter
    @Builder
    @ToString
    public static final class Overview {
        @Schema(description = "PK of the OCPP tag")
        private final Integer ocppTagPk;

        @Schema(description = "The OCPP tag")
        private final String idTag;

        @Schema(description = "PK of the parent OCPP tag of this OCPP tag")
        private final Integer parentOcppTagPk;

        @Schema(description = "The parent OCPP tag of this OCPP tag")
        private final String parentIdTag;

        @Schema(description = "Has the OCPP tag active transactions (i.e. ongoing charging sessions)?")
        private final boolean inTransaction;

        @Schema(description = "Is the OCPP tag blocked?")
        private final boolean blocked;

        /**
         * Only relevant for the web pages. Disabled for API
         */
        @JsonIgnore
        @Schema(hidden = true)
        private final String expiryDateFormatted;

        @Schema(description = "The date/time at which the OCPP tag will expire (if set)")
        private final DateTime expiryDate;

        @Schema(description = "The maximum number of active transactions allowed for this OCPP tag")
        private final Integer maxActiveTransactionCount;

        @Schema(description = "The number of currently active transactions for this OCPP tag")
        private final Long activeTransactionCount;

        @Schema(description = "An additional note")
        private final String note;
    }
}
