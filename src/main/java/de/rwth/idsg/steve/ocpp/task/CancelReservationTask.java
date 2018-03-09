package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import ocpp.cp._2012._06.CancelReservationRequest;
import ocpp.cp._2012._06.CancelReservationResponse;
import ocpp.cp._2012._06.CancelReservationStatus;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class CancelReservationTask extends CommunicationTask<CancelReservationParams, CancelReservationStatus> {

    private final ReservationRepository reservationRepository;

    public CancelReservationTask(OcppVersion ocppVersion, CancelReservationParams params,
                                 ReservationRepository reservationRepository) {
        super(ocppVersion, params);
        this.reservationRepository = reservationRepository;
    }

    @Override
    public OcppCallback<CancelReservationStatus> defaultCallback() {
        return new DefaultOcppCallback<CancelReservationStatus>() {
            @Override
            public void success(String chargeBoxId, CancelReservationStatus status) {
                addNewResponse(chargeBoxId, status.value());

                if (CancelReservationStatus.ACCEPTED.equals(status)) {
                    reservationRepository.cancelled(params.getReservationId());
                }
            }
        };
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp12Request() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public CancelReservationRequest getOcpp15Request() {
        return new CancelReservationRequest()
                .withReservationId(params.getReservationId());
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp12Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public AsyncHandler<CancelReservationResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
