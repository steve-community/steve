package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.GetTransactionStatusRequest;
import de.rwth.idsg.steve.ocpp20.model.GetTransactionStatusResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class GetTransactionStatusTask extends Ocpp20Task<GetTransactionStatusRequest, GetTransactionStatusResponse> {

    private final String transactionId;

    public GetTransactionStatusTask(List<String> chargeBoxIdList, String transactionId) {
        super("GetTransactionStatus", chargeBoxIdList);
        this.transactionId = transactionId;
    }

    @Override
    public GetTransactionStatusRequest createRequest() {
        GetTransactionStatusRequest request = new GetTransactionStatusRequest();
        if (transactionId != null) {
            request.setTransactionId(transactionId);
        }
        return request;
    }

    @Override
    public Class<GetTransactionStatusResponse> getResponseClass() {
        return GetTransactionStatusResponse.class;
    }
}