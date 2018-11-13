package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.11.2018
 */
@Setter
@Getter
public class SetChargingProfileParams extends MultipleChargePointSelect {

    @NotNull
    @Min(value = 0, message = "Connector ID must be at least {value}")
    private Integer connectorId;

    @NotNull
    @Positive
    private Integer chargingProfilePk;
}
