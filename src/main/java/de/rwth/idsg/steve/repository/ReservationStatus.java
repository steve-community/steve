package de.rwth.idsg.steve.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
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
