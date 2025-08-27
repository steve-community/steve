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
import jooq.steve.db.enums.TransactionStopEventActor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 *
 */
@Schema(
        description =
                """
        For active transactions, all 'stop'-prefixed fields would be null.
        The energy consumed during the transaction can be calculated by subtracting the 'startValue' from the 'stopValue'.
        The unit of the 'startValue' and 'stopValue' is watt-hours (Wh).
        """)
@Getter
@Builder
@ToString
public final class Transaction {

    @Schema(description = "PK of the transaction")
    private final int id;

    @Schema(description = "Connector ID of the charge box at which the transaction took place")
    private final int connectorId;

    @Schema(description = "PK of the charge box at which the transaction took place")
    private final int chargeBoxPk;

    @Schema(description = "PK of the OCPP tag used in the transaction")
    private final int ocppTagPk;

    @Schema(description = "The identifier of the charge box at which the transaction took place")
    private final String chargeBoxId;

    @Schema(description = "The Ocpp Tag used in the transaction")
    private final String ocppIdTag;

    /**
     * Only relevant for the web pages. Disabled for API
     */
    @JsonIgnore
    @Schema(hidden = true)
    private final String startTimestampFormatted;

    @Schema(description = "The meter value reading at the start of the transaction")
    private final String startValue;

    @Schema(description = "The timestamp at which the transaction started")
    private final @Nullable Instant startTimestamp;

    /**
     * Only relevant for the web pages. Disabled for API
     */
    @JsonIgnore
    @Schema(hidden = true)
    private final @Nullable String stopTimestampFormatted;

    @Schema(description = "The meter value reading at the end of the transaction")
    private final @Nullable String stopValue;

    @Schema(description = "The reason for the transaction being stopped")
    private final @Nullable String stopReason; // new in OCPP 1.6

    @Schema(description = "The timestamp at which the transaction ended")
    private final @Nullable Instant stopTimestamp;

    @Schema(description = "The actor who stopped the transaction")
    private final @Nullable TransactionStopEventActor stopEventActor;
}
