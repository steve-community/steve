package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
@Getter
@Setter
public class MultipleChargePointSelect {

    @NotNull(message = "Charge point selection is required")
    @Size(min = 1, message = "Please select at least {min} charge point")
    private List<ChargePointSelect> chargePointSelectList;
}
