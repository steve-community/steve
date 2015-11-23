package de.rwth.idsg.steve.handler.ocpp15;

import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.dto.task.RequestTask;
import ocpp.cp._2012._06.DataTransferResponse;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
 */
public class DataTransferResponseHandler extends AbstractOcppResponseHandler<DataTransferResponse> {

    public DataTransferResponseHandler(RequestTask requestTask, String chargeBoxId) {
        super(requestTask, chargeBoxId);
    }

    @Override
    public void handleResult(DataTransferResponse response) {
        StringBuilder builder = new StringBuilder(response.getStatus().value());
        if (response.isSetData()) {
            builder.append(" / Data: ").append(response.getData());
        }
        requestTask.addNewResponse(chargeBoxId, builder.toString());
    }
}
