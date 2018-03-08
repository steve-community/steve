package de.rwth.idsg.steve.web.dto.common;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 08.03.2018
 */
@Getter
@Setter
public class ResetParams extends MultipleChargePointSelect {

    @NotNull(message = "Reset Type is required")
    private ResetType resetType;
}
