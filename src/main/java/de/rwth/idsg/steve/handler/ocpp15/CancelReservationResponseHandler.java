package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.RequestTask;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2012._06.CancelReservationResponse;
import ocpp.cp._2012._06.CancelReservationStatus;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
@RequiredArgsConstructor
public class CancelReservationResponseHandler implements AsyncHandler<CancelReservationResponse> {
    private final RequestTask requestTask;
    private final String chargeBoxId;
    private final ReservationRepository reservationRepository;
    private final int reservationId;

    @Override
    public void handleResponse(Response<CancelReservationResponse> res) {
        try {
            CancelReservationStatus status = res.get().getStatus();
            requestTask.addNewResponse(chargeBoxId, status.value());

            if (CancelReservationStatus.ACCEPTED.equals(status)) {
                reservationRepository.cancelReservation(reservationId);
            }
        } catch (InterruptedException | CancellationException | ExecutionException e) {
            requestTask.addNewError(chargeBoxId, e);
        }
    }
}