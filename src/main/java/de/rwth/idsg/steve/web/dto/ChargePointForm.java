package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 18.12.2014
 */
@Getter
@Setter
public class ChargePointForm {

    // Internal database id
    private Integer chargeBoxPk;

    @NotBlank(message = "ChargeBox ID is required")
    @Pattern(regexp = "\\S+", message = "ChargeBox ID cannot contain any whitespace")
    private String chargeBoxId;
    
    private Address address;

    @Range(min = -90, max = 90, message = "Latitude must be between {min} and {max}")
    private BigDecimal locationLatitude;

    @Range(min = -180, max = 180, message = "Longitude must be between {min} and {max}")
    private BigDecimal locationLongitude;

    private String description;
    private String note;
}
