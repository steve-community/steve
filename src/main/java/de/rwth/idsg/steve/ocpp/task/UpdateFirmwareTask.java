package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.handler.OcppCallback;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.UpdateFirmwareParams;

import javax.xml.ws.AsyncHandler;

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
    public ocpp.cp._2010._08.UpdateFirmwareRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval())
                .withRetrieveDate(params.getRetrieve().toDateTime());
    }

    @Override
    public ocpp.cp._2012._06.UpdateFirmwareRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.UpdateFirmwareRequest()
                .withLocation(params.getLocation())
                .withRetries(params.getRetries())
                .withRetryInterval(params.getRetryInterval())
                .withRetrieveDate(params.getRetrieve().toDateTime());
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
