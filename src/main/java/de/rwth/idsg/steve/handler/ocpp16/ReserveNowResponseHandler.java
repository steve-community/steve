/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.handler.ocpp16;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2015._10.ReservationStatus;
import ocpp.cp._2015._10.ReserveNowRequest;
import ocpp.cp._2015._10.ReserveNowResponse;

/**
 *
 * @author david
 */
public class ReserveNowResponseHandler extends AbstractOcppResponseHandler<ReserveNowRequest, ReserveNowResponse> {

    private final ReservationRepository reservationRepository;

    public ReserveNowResponseHandler(RequestTask<ReserveNowRequest> task, String chargeBoxId,
                                     ReservationRepository reservationRepository) {
        super(task, chargeBoxId);
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void handleResult(ReserveNowResponse response) {
        ReservationStatus status = response.getStatus();
        requestTask.addNewResponse(chargeBoxId, status.value());

        if (ReservationStatus.ACCEPTED.equals(status)) {
            reservationRepository.accepted(getRequest().getReservationId());
        } else {
            delete();
        }
    }

    @Override
    public void handleException(Exception e) {
        super.handleException(e);
        delete();
    }

    private void delete() {
        reservationRepository.delete(getRequest().getReservationId());
    }
}
