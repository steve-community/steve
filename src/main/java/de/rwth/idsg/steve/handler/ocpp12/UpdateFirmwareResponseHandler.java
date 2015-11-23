package de.rwth.idsg.steve.handler.ocpp12;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2010._08.UpdateFirmwareResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
public class UpdateFirmwareResponseHandler extends AbstractOcppResponseHandler<UpdateFirmwareResponse> {

    public UpdateFirmwareResponseHandler(RequestTask requestTask, String chargeBoxId) {
        super(requestTask, chargeBoxId);
    }

    @Override
    public void handleResult(UpdateFirmwareResponse response) {
        requestTask.addNewResponse(chargeBoxId, "OK");
    }
}
