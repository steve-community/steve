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
package de.rwth.idsg.steve.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 31.08.2015
 */
public enum ReservationStatus {

    WAITING,    // Waiting for charge point to respond to a reservation request
    ACCEPTED,   // Charge point accepted. The only status for active, usable reservations (if expiryDatetime is in future)
    USED,       // Reservation used by the user for a transaction
    CANCELLED;  // Reservation cancelled by the user

    public String value() {
        return this.name();
    }

    @Override
    public String toString() {
        return value();
    }

    public static ReservationStatus fromValue(String v) {
        for (ReservationStatus c: ReservationStatus.values()) {
            if (c.value().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public static List<String> getValues() {
        List<String> list = new ArrayList<>(ReservationStatus.values().length);
        for (ReservationStatus c: ReservationStatus.values()) {
            list.add(c.value());
        }
        return list;
    }
}
