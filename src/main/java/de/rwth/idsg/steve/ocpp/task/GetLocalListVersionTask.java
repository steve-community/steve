package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class GetLocalListVersionTask extends CommunicationTask<MultipleChargePointSelect, String> {

    public GetLocalListVersionTask(OcppVersion ocppVersion, MultipleChargePointSelect params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp12Request() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public ocpp.cp._2012._06.GetLocalListVersionRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.GetLocalListVersionRequest();
    }

    @Override
    public ocpp.cp._2015._10.GetLocalListVersionRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.GetLocalListVersionRequest();
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp12Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.GetLocalListVersionResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, String.valueOf(res.get().getListVersion()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.GetLocalListVersionResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, String.valueOf(res.get().getListVersion()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
