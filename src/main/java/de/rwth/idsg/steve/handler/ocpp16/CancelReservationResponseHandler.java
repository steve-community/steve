/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.handler.ocpp16;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.CancelReservationRequest;
import ocpp.cp._2015._10.CancelReservationResponse;
import ocpp.cp._2015._10.CancelReservationStatus;

/**
 *
 * @author david
 */

public class CancelReservationResponseHandler
        extends AbstractOcppResponseHandler<CancelReservationRequest, CancelReservationResponse> {

    private final ReservationRepository reservationRepository;

    public CancelReservationResponseHandler(RequestTask<CancelReservationRequest> task, String chargeBoxId,
                                            ReservationRepository reservationRepository) {
        super(task, chargeBoxId);
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void handleResult(CancelReservationResponse response) {
        CancelReservationStatus status = response.getStatus();
        requestTask.addNewResponse(chargeBoxId, status.value());

        if (CancelReservationStatus.ACCEPTED.equals(status)) {
            reservationRepository.cancelled(getRequest().getReservationId());
        }
    }
}
