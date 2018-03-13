package de.rwth.idsg.steve.web.dto.common;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 01.01.2015
 */
@Setter
@Getter
public class UnlockConnectorParams extends SingleChargePointSelect {

    @NotNull(message = "Connector ID is required")
    @Min(value = 1, message = "Connector ID must be at least {value}")
    private Integer connectorId;
}
