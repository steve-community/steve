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
package de.rwth.idsg.steve.web.dto;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 26.11.2015
 */
@Getter
@Setter
@ToString
public class ChargePointQueryForm {

    private Integer chargeBoxPk;
    private String chargeBoxId;
    private String description;
    private String note;
    private OcppVersion ocppVersion;
    private QueryPeriodType heartbeatPeriod;

    /**
     * Init with sensible default values
     */
    public ChargePointQueryForm() {
        heartbeatPeriod = QueryPeriodType.ALL;
    }

    public boolean isSetOcppVersion() {
        return ocppVersion != null;
    }

    public boolean isSetDescription() {
        return description != null;
    }

    public boolean isSetChargeBoxId() {
        return StringUtils.isNotBlank(chargeBoxId);
    }

    public boolean isSetNote() {
        return StringUtils.isNotBlank(note);
    }

    public enum ChargeBoxIdMatchType {
        Exact,
        PatternMatchLike
    }

    @RequiredArgsConstructor
    public enum QueryPeriodType {
        ALL("All"),
        TODAY("Today"),
        YESTERDAY("Yesterday"),
        EARLIER("Earlier");

        @Getter private final String value;

        public static QueryPeriodType fromValue(String v) {
            for (QueryPeriodType c: QueryPeriodType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }

}
