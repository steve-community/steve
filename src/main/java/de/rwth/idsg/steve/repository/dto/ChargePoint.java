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
package de.rwth.idsg.steve.repository.dto;

import jooq.steve.db.tables.records.AddressRecord;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;

/**
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 *
 */
public final class ChargePoint {

    @Getter
    @Builder
    public static final class Overview {
        private final int chargeBoxPk;
        private final String chargeBoxId, description, ocppProtocol, lastHeartbeatTimestamp;
        private final DateTime lastHeartbeatTimestampDT;
    }

    @Getter
    @RequiredArgsConstructor
    public static final class Details {
        private final ChargeBoxRecord chargeBox;
        private final AddressRecord address;
    }

}
