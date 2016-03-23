package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2012._06.CancelReservationRequest;
import ocpp.cp._2012._06.CancelReservationResponse;
import ocpp.cp._2012._06.CancelReservationStatus;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
public class CancelReservationResponseHandler
        extends AbstractOcppResponseHandler<CancelReservationRequest, CancelReservationResponse> {

    private final ReservationRepository reservationRepository;

    public CancelReservationResponseHandler(CancelReservationRequest req, RequestTask task, String chargeBoxId,
                                            ReservationRepository reservationRepository) {
        super(req, task, chargeBoxId);
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void handleResult(CancelReservationResponse response) {
        CancelReservationStatus status = response.getStatus();
        requestTask.addNewResponse(chargeBoxId, status.value());

        if (CancelReservationStatus.ACCEPTED.equals(status)) {
            reservationRepository.cancelled(request.getReservationId());
        }
    }
}
