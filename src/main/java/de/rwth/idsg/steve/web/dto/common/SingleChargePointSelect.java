package de.rwth.idsg.steve.web.dto.common;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Why a list, if the list size == 1?
 * To keep the method calls and data types (for ex: tasks api) consistent for both cases.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
@Getter
@Setter
public class SingleChargePointSelect {

    @NotNull(message = "Charge point selection is required")
    @Size(min = 1, max = 1, message = "It is required to select exactly 1 charge point")
    private List<ChargePointSelect> chargePointSelectList;
}
