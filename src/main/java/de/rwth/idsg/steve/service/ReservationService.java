package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.ExceptionMessage;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 22.08.2014
 */
@Service
public class ReservationService {

    @Autowired ReservationRepository reservationRepository;

    public int bookReservation(String idTag, String chargeBoxId, DateTime expiryDateTime) {
        DateTime now = new DateTime();

        // Check the date first
        // Continue only if: now < expiryDatetime
        if (now.isAfter(expiryDateTime)) {
            throw new SteveException(ExceptionMessage.INVALID_DATETIME);
        }

        Timestamp startTimestamp = new Timestamp(now.getMillis());
        Timestamp expiryTimestamp = new Timestamp(expiryDateTime.getMillis());

        // Book the reservation and get the id
        return reservationRepository.bookReservation(idTag, chargeBoxId, startTimestamp, expiryTimestamp);
    }

    public void cancelReservation(int reservationId) throws SteveException {
        reservationRepository.cancelReservation(reservationId);
    }
}