/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.service.eichrecht.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.Nullable;

/**
 * Result of one check of the signed data of a transaction.
 */
@Schema(description = "Result of one check of the signed data of a transaction")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SignedDataCheck(
    @Schema(description = "Stable identifier of the check")
    Type type,

    @Schema(description = "Description of the check, for humans")
    String description,

    @Schema(description = "Result of the check")
    Result result,

    @Schema(description = "Details, e.g. why it failed. Omitted, if there are none.")
    @Nullable String note
) {

    public enum Type {
        BeginReceived,
        BeginSignature,
        BeginValue,
        EndReceived,
        EndSignature,
        EndValue
    }

    public enum Result {
        Passed,
        Failed,
        NotChecked
    }
}
