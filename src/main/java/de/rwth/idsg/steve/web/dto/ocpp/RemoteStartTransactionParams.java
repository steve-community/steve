package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.web.validation.IdTag;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 01.01.2015
 */
@Getter
public class RemoteStartTransactionParams extends SingleChargePointSelect {

    @Min(value = 0, message = "Connector ID must be at least {value}")
    private Integer connectorId;

    @NotBlank(message = "User ID Tag is required")
    @IdTag
    @Setter private String idTag;

    /**
     * Not for a specific connector, when frontend sends the value 0.
     * This corresponds to not to include the connector id parameter in OCPP request.
     */
    public void setConnectorId(Integer connectorId) {
        if (connectorId == 0) {
            this.connectorId = null;
        } else {
            this.connectorId = connectorId;
        }
    }
}
