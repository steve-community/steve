package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.UnlockConnectorParams;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class UnlockConnectorTask extends CommunicationTask<UnlockConnectorParams, String> {

    public UnlockConnectorTask(OcppVersion ocppVersion, UnlockConnectorParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2010._08.UnlockConnectorRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.UnlockConnectorRequest()
                .withConnectorId(params.getConnectorId());
    }

    @Override
    public ocpp.cp._2012._06.UnlockConnectorRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.UnlockConnectorRequest()
                .withConnectorId(params.getConnectorId());
    }

    @Override
    public ocpp.cp._2015._10.UnlockConnectorRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.UnlockConnectorRequest()
                .withConnectorId(params.getConnectorId());
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.UnlockConnectorResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.UnlockConnectorResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.UnlockConnectorResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
