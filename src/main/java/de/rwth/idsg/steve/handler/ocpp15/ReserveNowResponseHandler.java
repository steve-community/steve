package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.RequestTask;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2012._06.ReservationStatus;
import ocpp.cp._2012._06.ReserveNowResponse;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
@RequiredArgsConstructor
public class ReserveNowResponseHandler implements AsyncHandler<ReserveNowResponse> {
    private final RequestTask requestTask;
    private final String chargeBoxId;
    private final ReservationRepository reservationRepository;
    private final int reservationId;

    @Override
    public void handleResponse(Response<ReserveNowResponse> res) {
        try {
            ReservationStatus status = res.get().getStatus();
            requestTask.addNewResponse(chargeBoxId, status.value());

            if (!ReservationStatus.ACCEPTED.equals(status)) {
                cancelReservation();
            }
        } catch (InterruptedException | CancellationException | ExecutionException e) {
            requestTask.addNewError(chargeBoxId, e);
            cancelReservation();
        }
    }

    private void cancelReservation() {
        reservationRepository.cancelReservation(reservationId);
    }
}
