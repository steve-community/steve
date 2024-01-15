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
package de.rwth.idsg.steve.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 03.09.2015
 */
@Getter
@Setter
@ToString
public class OcppTagQueryForm {

    @ApiModelProperty(value = "Database primary key of the OCPP tag")
    private Integer ocppTagPk;

    @ApiModelProperty(value = "The OCPP tag")
    private String idTag;

    @ApiModelProperty(value = "The parent OCPP tag of this OCPP tag")
    private String parentIdTag;

    @ApiModelProperty(value = "Return expired, not expired, or all Ocpp tags? Defaults to ALL")
    private BooleanType expired = BooleanType.FALSE;

    @ApiModelProperty(value = "Return in-transaction, not in-transaction, or all Ocpp tags? Defaults to ALL")
    private BooleanType inTransaction = BooleanType.ALL;

    @ApiModelProperty(value = "Return blocked, not blocked, or all Ocpp tags? Defaults to ALL")
    private BooleanType blocked = BooleanType.FALSE;

    @ApiModelProperty(hidden = true)
    public boolean isOcppTagPkSet() {
        return ocppTagPk != null;
    }

    @ApiModelProperty(hidden = true)
    public boolean isIdTagSet() {
        return idTag != null;
    }

    @ApiModelProperty(hidden = true)
    public boolean isParentIdTagSet() {
        return parentIdTag != null;
    }

    public BooleanType getExpired() {
        return Objects.requireNonNullElse(expired, BooleanType.ALL);
    }

    public BooleanType getInTransaction() {
        return Objects.requireNonNullElse(inTransaction, BooleanType.ALL);
    }

    public BooleanType getBlocked() {
        return Objects.requireNonNullElse(blocked, BooleanType.ALL);
    }

    @RequiredArgsConstructor
    public enum BooleanType {
        ALL("All", null),
        TRUE("True", true),
        FALSE("False", false);

        @Getter private final String value;
        private final Boolean boolValue;

        public boolean getBoolValue() {
            if (this.boolValue == null) {
                throw new UnsupportedOperationException("This enum does not have any meaningful bool value set.");
            }
            return this.boolValue;
        }

        public static BooleanType fromValue(String v) {
            for (BooleanType c: BooleanType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }

    @ToString(callSuper = true)
    public static class ForApi extends OcppTagQueryForm {

        public ForApi () {
            super();
            setExpired(BooleanType.ALL);
            setInTransaction(BooleanType.ALL);
            setBlocked(BooleanType.ALL);
        }
    }

}
