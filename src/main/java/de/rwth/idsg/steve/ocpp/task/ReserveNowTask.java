package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.Ocpp15AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonError;
import de.rwth.idsg.steve.repository.ReservationRepository;
import de.rwth.idsg.steve.service.dto.EnhancedReserveNowParams;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class ReserveNowTask extends Ocpp15AndAboveTask<EnhancedReserveNowParams, String> {

    private final ReservationRepository reservationRepository;

    public ReserveNowTask(OcppVersion ocppVersion, EnhancedReserveNowParams params,
                          ReservationRepository reservationRepository) {
        super(ocppVersion, params);
        this.reservationRepository = reservationRepository;
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new OcppCallback<String>() {
            @Override
            public void success(String chargeBoxId, String responseStatus) {
                addNewResponse(chargeBoxId, responseStatus);

                if ("Accepted".equalsIgnoreCase(responseStatus)) {
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

    @Override
    public ocpp.cp._2012._06.ReserveNowRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.ReserveNowRequest()
                .withConnectorId(params.getReserveNowParams().getConnectorId())
                .withExpiryDate(params.getReserveNowParams().getExpiry().toDateTime())
                .withIdTag(params.getReserveNowParams().getIdTag())
                .withReservationId(params.getReservationId())
                .withParentIdTag(params.getParentIdTag());
    }

    @Override
    public ocpp.cp._2015._10.ReserveNowRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.ReserveNowRequest()
                .withConnectorId(params.getReserveNowParams().getConnectorId())
                .withExpiryDate(params.getReserveNowParams().getExpiry().toDateTime())
                .withIdTag(params.getReserveNowParams().getIdTag())
                .withReservationId(params.getReservationId())
                .withParentIdTag(params.getParentIdTag());
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.ReserveNowResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.ReserveNowResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    private void delete() {
        reservationRepository.delete(params.getReservationId());
    }

}
