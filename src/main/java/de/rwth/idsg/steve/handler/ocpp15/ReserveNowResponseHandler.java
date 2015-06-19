package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.RequestTask;
import ocpp.cp._2012._06.ReservationStatus;
import ocpp.cp._2012._06.ReserveNowResponse;

import javax.xml.ws.Response;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
public class ReserveNowResponseHandler extends AbstractOcppResponseHandler<ReserveNowResponse> {
    private final ReservationRepository reservationRepository;
    private final int reservationId;

    public ReserveNowResponseHandler(RequestTask requestTask, String chargeBoxId,
                                     ReservationRepository reservationRepository, int reservationId) {
        super(requestTask, chargeBoxId);
        this.reservationRepository = reservationRepository;
        this.reservationId = reservationId;
    }

    @Override
    public void handleResponse(Response<ReserveNowResponse> res) {
        try {
            handleResult(res.get());
        } catch (InterruptedException | CancellationException | ExecutionException e) {
            requestTask.addNewError(chargeBoxId, e);
            delete();
        }
    }

    @Override
    public void handleResult(ReserveNowResponse response) {
        ReservationStatus status = response.getStatus();
        requestTask.addNewResponse(chargeBoxId, status.value());

        if (ReservationStatus.ACCEPTED.equals(status)) {
            reservationRepository.accepted(reservationId);
        } else {
            delete();
        }
    }

    private void delete() {
        reservationRepository.delete(reservationId);
    }
}
