package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2015._10.MessageTrigger;

import javax.validation.constraints.NotNull;

/**
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 20.03.2018
 */
@Setter
@Getter
public class TriggerMessageParams extends MultipleChargePointSelect {
    @NotNull(message = "Requested Message required")
    private MessageTrigger triggerMessage;

    private Integer connectorId;
}
