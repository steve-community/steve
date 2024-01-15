/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
import io.swagger.annotations.ApiModelProperty;
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
        private final Integer ocppTagPk;
        private final String idTag;

        private final Integer parentOcppTagPk;
        private final String parentIdTag;

        private final boolean inTransaction;
        private final boolean blocked;

        /**
         * Only relevant for the web pages. Disabled for API
         */
        @JsonIgnore
        @ApiModelProperty(hidden = true)
        private final String expiryDateFormatted;

        private final DateTime expiryDate;

        private final Integer maxActiveTransactionCount;
        private final Long activeTransactionCount;
        private final String note;
    }
}
