package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import ocpp.cp._2015._10.ClearCacheResponse;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class ClearCacheTask extends CommunicationTask<MultipleChargePointSelect, String> {

    public ClearCacheTask(OcppVersion ocppVersion, MultipleChargePointSelect params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2010._08.ClearCacheRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.ClearCacheRequest();
    }

    @Override
    public ocpp.cp._2012._06.ClearCacheRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.ClearCacheRequest();
    }

    @Override
    public ocpp.cp._2015._10.ClearCacheRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.ClearCacheRequest();
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.ClearCacheResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.ClearCacheResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.ClearCacheResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                ClearCacheTask.this.success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                ClearCacheTask.this.failed(chargeBoxId, e);
            }
        };
    }
}
