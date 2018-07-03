package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import ocpp.cp._2012._06.CancelReservationRequest;
import ocpp.cp._2012._06.CancelReservationResponse;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class CancelReservationTask extends CommunicationTask<CancelReservationParams, String> {

    private final ReservationRepository reservationRepository;

    public CancelReservationTask(OcppVersion ocppVersion, CancelReservationParams params,
                                 ReservationRepository reservationRepository) {
        super(ocppVersion, params);
        this.reservationRepository = reservationRepository;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new DefaultOcppCallback<String>() {
            @Override
            public void success(String chargeBoxId, String statusValue) {
                addNewResponse(chargeBoxId, statusValue);

                if ("Accepted".equalsIgnoreCase(statusValue)) {
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

    @Override
    public ocpp.cp._2015._10.CancelReservationRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.CancelReservationRequest()
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
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.CancelReservationResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
