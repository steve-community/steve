package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.handler.OcppCallback;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.web.dto.ocpp.DataTransferParams;
import ocpp.cs._2012._06.DataTransferRequest;
import ocpp.cs._2012._06.DataTransferResponse;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class DataTransferTask extends CommunicationTask<DataTransferParams, DataTransferResponse> {

    public DataTransferTask(OcppVersion ocppVersion, DataTransferParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<DataTransferResponse> defaultCallback() {
        return new DefaultOcppCallback<DataTransferResponse>() {
            @Override
            public void success(String chargeBoxId, DataTransferResponse response) {
                StringBuilder builder = new StringBuilder(response.getStatus().value());
                if (response.isSetData()) {
                    builder.append(" / Data: ").append(response.getData());
                }
                addNewResponse(chargeBoxId, builder.toString());
            }
        };
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp12Request() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public DataTransferRequest getOcpp15Request() {
        return new DataTransferRequest()
                .withData(params.getData())
                .withMessageId(params.getMessageId())
                .withVendorId(params.getVendorId());
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp12Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public AsyncHandler<DataTransferResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
 }
