package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 20.03.2018
 */
@Setter
@Getter
public class TriggerMessageParams extends MultipleChargePointSelect {

    @NotNull(message = "Requested Message required")
    private TriggerMessageEnum triggerMessage;

    @Min(value = 1, message = "Connector ID must be at least {value}")
    private Integer connectorId;
}
