package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
public class CostUpdatedParams extends BaseParams {
    @NotNull(message = "Total cost is required")
    private BigDecimal totalCost;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
}
