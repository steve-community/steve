package de.rwth.idsg.steve.web.dto.common;

import de.rwth.idsg.steve.web.validation.IdTag;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 01.01.2015
 */
@Getter
public class RemoteStartTransactionParams extends SingleChargePointSelect {

    @Min(value = 0, message = "Connector ID must be at least {value}")
    private Integer connectorId;

    @NotBlank(message = "User ID Tag is required")
    @IdTag(message = "User ID Tag can only contain upper or lower case letters, numbers and dot, dash, underscore symbols")
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
