package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.handler.OcppCallback;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.ocpp.task.dto.EnhancedReserveNowParams;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.repository.ReservationRepository;
import ocpp.cp._2012._06.ReservationStatus;
import ocpp.cp._2012._06.ReserveNowRequest;
import ocpp.cp._2012._06.ReserveNowResponse;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class ReserveNowTask extends CommunicationTask<EnhancedReserveNowParams, ReserveNowResponse> {

    private final ReservationRepository reservationRepository;

    public ReserveNowTask(OcppVersion ocppVersion, EnhancedReserveNowParams params,
                          ReservationRepository reservationRepository) {
        super(ocppVersion, params);
        this.reservationRepository = reservationRepository;
    }

    @Override
    public OcppCallback<ReserveNowResponse> defaultCallback() {
        return new OcppCallback<ReserveNowResponse>() {
            @Override
            public void success(String chargeBoxId, ReserveNowResponse response) {
                ReservationStatus status = response.getStatus();
                addNewResponse(chargeBoxId, status.value());

                if (ReservationStatus.ACCEPTED.equals(status)) {
                    reservationRepository.accepted(params.getReservationId());
                } else {
                    delete();
                }
            }

            @Override
            public void success(String chargeBoxId, OcppJsonError error) {
                addNewError(chargeBoxId, error.toString());
                delete();
            }

            @Override
            public void failed(String chargeBoxId, Exception e) {
                addNewError(chargeBoxId, e.getMessage());
                delete();
            }
        };
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp12Request() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public ReserveNowRequest getOcpp15Request() {
        return new ReserveNowRequest()
                .withConnectorId(params.getReserveNowParams().getConnectorId())
                .withExpiryDate(params.getReserveNowParams().getExpiry().toDateTime())
                .withIdTag(params.getReserveNowParams().getIdTag())
                .withReservationId(params.getReservationId())
                .withParentIdTag(params.getParentIdTag());
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp12Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public AsyncHandler<ReserveNowResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    private void delete() {
        reservationRepository.delete(params.getReservationId());
    }

}
