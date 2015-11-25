package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.11.2015
 */
@Getter
@Setter
public class Address {

    @NotBlank(message = "Street and house number is not set")
    private String streetAndHouseNumber;

    @NotBlank(message = "Zip code is not set")
    private String zipCode;

    @NotBlank(message = "City is not set")
    private String city;

    @NotBlank(message = "Country is not set")
    private String country;
}
