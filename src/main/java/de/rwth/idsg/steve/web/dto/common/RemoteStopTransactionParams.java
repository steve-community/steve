package de.rwth.idsg.steve.web.dto.common;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 01.01.2015
 */
@Getter
@Setter
public class RemoteStopTransactionParams extends SingleChargePointSelect {

    @NotNull(message = "Transaction ID is required")
    private Integer transactionId;
}
