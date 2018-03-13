package de.rwth.idsg.steve.web.dto.ocpp16;

import de.rwth.idsg.steve.web.dto.common.SingleChargePointSelect;
import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2015._10.MessageTrigger;

import javax.validation.constraints.NotNull;

/**
 *
 * @author david
 */

@Getter
@Setter
public class TriggerMessageParams extends SingleChargePointSelect
{
    @NotNull(message = "Requested Message required")
    private MessageTrigger triggerMessage;

    //public boolean isSetTriggerMessage() {return triggerMessage != null && !triggerMessage.equals(""); }

    private Integer connectorId;

    public boolean isSetConnectorId() {return connectorId != null && !connectorId.equals(""); }
}
