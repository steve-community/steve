package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.handler.OcppCallback;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeAvailabilityParams;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class ChangeAvailabilityTask extends CommunicationTask<ChangeAvailabilityParams, String> {

    public ChangeAvailabilityTask(OcppVersion ocppVersion, ChangeAvailabilityParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new OcppCallback<String>() {
            @Override
            public void success(String chargeBoxId, String response) {
                addNewResponse(chargeBoxId, response);
            }

            @Override
            public void failed(String chargeBoxId, String errorMessage) {
                addNewError(chargeBoxId, errorMessage);
            }
        };
    }

    @Override
    public ocpp.cp._2010._08.ChangeAvailabilityRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.ChangeAvailabilityRequest()
                .withConnectorId(params.getConnectorId())
                .withType(ocpp.cp._2010._08.AvailabilityType.fromValue(params.getAvailType().value()));
    }

    @Override
    public ocpp.cp._2012._06.ChangeAvailabilityRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.ChangeAvailabilityRequest()
                .withConnectorId(params.getConnectorId())
                .withType(ocpp.cp._2012._06.AvailabilityType.fromValue(params.getAvailType().value()));
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.ChangeAvailabilityResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.ChangeAvailabilityResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
