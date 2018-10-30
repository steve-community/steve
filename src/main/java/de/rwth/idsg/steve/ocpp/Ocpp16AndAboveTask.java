package de.rwth.idsg.steve.ocpp;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.10.2018
 */
public abstract class Ocpp16AndAboveTask<S extends ChargePointSelection, RESPONSE> extends Ocpp15AndAboveTask<S, RESPONSE> {

    public Ocpp16AndAboveTask(OcppVersion ocppVersion, S params) {
        super(ocppVersion, params);
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp15Request() {
        throw new RuntimeException("Not supported");
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp15Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }
}
