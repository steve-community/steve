package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 18.12.2014
 */
@Getter
@Setter
public class ChargeBoxForm {

    @NotBlank(message = "ChargeBox ID is required")
    @Pattern(regexp = "\\s", message = "ChargeBox ID cannot contain any whitespace")
    private String chargeBoxId;

}
