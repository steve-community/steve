package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RemoteStartTransactionStatusResponse {

    private final int taskId;
    private final String chargeBoxId;
    private final Integer connectorId;
    private final String idTag;
    private final boolean finished;
    private final String response;
    private final String errorMessage;
    private final Integer transactionId;
    private final List<Integer> activeTransactionIds;
}
