package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class ChangeConfigurationTask extends CommunicationTask<ChangeConfigurationParams, String> {

    public ChangeConfigurationTask(OcppVersion ocppVersion, ChangeConfigurationParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2010._08.ChangeConfigurationRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.ChangeConfigurationRequest()
                .withKey(params.getConfKey())
                .withValue(params.getValue());
    }

    @Override
    public ocpp.cp._2012._06.ChangeConfigurationRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.ChangeConfigurationRequest()
                .withKey(params.getConfKey())
                .withValue(params.getValue());
    }

    @Override
    public ocpp.cp._2015._10.ChangeConfigurationRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.ChangeConfigurationRequest()
                .withKey(params.getConfKey())
                .withValue(params.getValue());
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.ChangeConfigurationResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.ChangeConfigurationResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.ChangeConfigurationResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
