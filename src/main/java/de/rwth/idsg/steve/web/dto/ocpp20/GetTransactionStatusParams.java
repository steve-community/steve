package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTransactionStatusParams extends BaseParams {

    private String transactionId;
}