package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.UpdateFirmwareParams;

import javax.xml.ws.AsyncHandler;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class UpdateFirmwareTask extends CommunicationTask<UpdateFirmwareParams, String> {

    public UpdateFirmwareTask(OcppVersion ocppVersion, UpdateFirmwareParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2010._08.UpdateFirmwareRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(toDateTime(params.getRetrieve()))
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval());
    }

    @Override
    public ocpp.cp._2012._06.UpdateFirmwareRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetrieveDate(toDateTime(params.getRetrieve()))
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval());
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.UpdateFirmwareResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, "OK");
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.UpdateFirmwareResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, "OK");
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
