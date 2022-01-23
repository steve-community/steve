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

import de.rwth.idsg.steve.ocpp.OcppVersion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 26.11.2015
 */
@Getter
@Setter
public class ChargePointQueryForm {

    private String chargeBoxId;
    private String description;
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
        return chargeBoxId != null;
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
