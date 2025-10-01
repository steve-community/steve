package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.CostUpdatedRequest;
import de.rwth.idsg.steve.ocpp20.model.CostUpdatedResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class CostUpdatedTask extends Ocpp20Task<CostUpdatedRequest, CostUpdatedResponse> {

    private final Double totalCost;
    private final String transactionId;

    public CostUpdatedTask(List<String> chargeBoxIdList, Double totalCost, String transactionId) {
        super("CostUpdated", chargeBoxIdList);
        this.totalCost = totalCost;
        this.transactionId = transactionId;
    }

    @Override
    public CostUpdatedRequest createRequest() {
        CostUpdatedRequest request = new CostUpdatedRequest();
        request.setTotalCost(totalCost);
        request.setTransactionId(transactionId);
        return request;
    }

    @Override
    public Class<CostUpdatedResponse> getResponseClass() {
        return CostUpdatedResponse.class;
    }
}
