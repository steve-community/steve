/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.web.validation.IdTag;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2014
 */
@Getter
@Setter
public class OcppTagForm {

    // Internal database id
    private Integer ocppTagPk;

    @NotEmpty(message = "ID Tag is required")
    @IdTag
    private String idTag;

    // Is a FK in DB table. No validation needed. Operation will fail if DB constraint fails.
    private String parentIdTag;

    @Future(message = "Expiry Date/Time must be in future")
    private LocalDateTime expiration;

    private Integer maxActiveTransactionCount;

    private String note;

    /**
     * As specified in V0_9_9__update.sql default value is 1.
     */
    public Integer getMaxActiveTransactionCount() {
        return Objects.requireNonNullElse(maxActiveTransactionCount, 1);
    }
}
