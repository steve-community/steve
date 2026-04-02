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
package de.rwth.idsg.steve.web.dto;

import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Database primary key of the OCPP tag")
    private Integer ocppTagPk;

    @Schema(description = "The OCPP tag")
    private String idTag;

    @Schema(description = "The parent OCPP tag of this OCPP tag")
    private String parentIdTag;

    @Schema(description = "The User ID")
    private Integer userId;

    @Schema(description = "Return expired, not expired, or all Ocpp tags? Defaults to ALL")
    private BooleanType expired = BooleanType.FALSE;

    @Schema(description = "Return in-transaction, not in-transaction, or all Ocpp tags? Defaults to ALL")
    private BooleanType inTransaction = BooleanType.ALL;

    @Schema(description = "Return blocked, not blocked, or all Ocpp tags? Defaults to ALL")
    private BooleanType blocked = BooleanType.FALSE;

    @Schema(description = "Query by the note associated with the OCPP tag. The value of this field does not have to exactly match the note. A substring is also accepted.")
    private String note;

    @Schema(description = "Filter by whether the OCPP tag is associated with a user or not. Defaults to All")
    private UserFilter userFilter = UserFilter.All;

    @Schema(hidden = true)
    public boolean isOcppTagPkSet() {
        return ocppTagPk != null;
    }

    @Schema(hidden = true)
    public boolean isIdTagSet() {
        return idTag != null;
    }

    @Schema(hidden = true)
    public boolean isParentIdTagSet() {
        return parentIdTag != null;
    }

    @Schema(hidden = true)
    public boolean isNoteSet() {
        return !Strings.isNullOrEmpty(note);
    }

    @Schema(hidden = true)
    public boolean isUserIdSet() {
        return userId != null;
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

    public enum UserFilter {
        All,
        OnlyTagsWithUser,
        OnlyTagsWithoutUser
    }

    @ToString(callSuper = true)
    public static class OcppTagQueryFormForApi extends OcppTagQueryForm {

        public OcppTagQueryFormForApi() {
            super();
            setExpired(BooleanType.ALL);
            setInTransaction(BooleanType.ALL);
            setBlocked(BooleanType.ALL);
        }
    }

}
