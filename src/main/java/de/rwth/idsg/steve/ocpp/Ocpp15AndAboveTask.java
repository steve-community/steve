package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.10.2018
 */
public abstract class Ocpp15AndAboveTask<S extends ChargePointSelection, RESPONSE> extends CommunicationTask<S, RESPONSE> {

    public Ocpp15AndAboveTask(OcppVersion ocppVersion, S params) {
        super(ocppVersion, params);
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp12Request() {
        throw new RuntimeException("Not supported");
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp12Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }
}
