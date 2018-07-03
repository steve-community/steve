package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.web.dto.ocpp.DataTransferParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2012._06.DataTransferResponse;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class DataTransferTask extends CommunicationTask<DataTransferParams, DataTransferTask.ResponseWrapper> {

    public DataTransferTask(OcppVersion ocppVersion, DataTransferParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<ResponseWrapper> defaultCallback() {
        return new DefaultOcppCallback<ResponseWrapper>() {
            @Override
            public void success(String chargeBoxId, ResponseWrapper response) {
                String status = response.getStatus();
                String data = response.getData();

                StringBuilder builder = new StringBuilder(status);
                if (data != null) {
                    builder.append(" / Data: ").append(data);
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

    /**
     * Dummy implementation. It must be vendor-specific.
     */
    @Override
    public ocpp.cp._2012._06.DataTransferRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.DataTransferRequest()
                .withData(params.getData())
                .withMessageId(params.getMessageId())
                .withVendorId(params.getVendorId());
    }

    /**
     * Dummy implementation. It must be vendor-specific.
     */
    @Override
    public ocpp.cp._2015._10.DataTransferRequest getOcpp16Request() {
        return new ocpp.cp._2015._10.DataTransferRequest()
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
    public AsyncHandler<ocpp.cp._2012._06.DataTransferResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                DataTransferResponse response = res.get();
                success(chargeBoxId, new ResponseWrapper(response.getStatus().value(), response.getData()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.DataTransferResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                ocpp.cp._2015._10.DataTransferResponse response = res.get();
                success(chargeBoxId, new ResponseWrapper(response.getStatus().value(), response.getData()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Getter
    @RequiredArgsConstructor
    public static class ResponseWrapper {
        private final String status;
        private final String data;
    }
}
