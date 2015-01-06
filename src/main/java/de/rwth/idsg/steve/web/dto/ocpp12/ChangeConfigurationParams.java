package de.rwth.idsg.steve.web.dto.ocpp12;

import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
@Getter
@Setter
public class ChangeConfigurationParams extends MultipleChargePointSelect {

    @NotNull(message = "Configuration Key is required")
    private ConfigurationKeyEnum confKey;

    @NotBlank(message = "Value is required")
    @Pattern(regexp = "\\S+", message = "Value cannot contain any whitespace")
    private String value;

}
