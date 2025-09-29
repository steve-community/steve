package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.CancelReservationRequest;
import de.rwth.idsg.steve.ocpp20.model.CancelReservationResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class CancelReservationTask extends Ocpp20Task<CancelReservationRequest, CancelReservationResponse> {

    private final Integer reservationId;

    public CancelReservationTask(List<String> chargeBoxIdList, Integer reservationId) {
        super("CancelReservation", chargeBoxIdList);
        this.reservationId = reservationId;
    }

    @Override
    public CancelReservationRequest createRequest() {
        CancelReservationRequest request = new CancelReservationRequest();
        request.setReservationId(reservationId);
        return request;
    }

    @Override
    public Class<CancelReservationResponse> getResponseClass() {
        return CancelReservationResponse.class;
    }
}