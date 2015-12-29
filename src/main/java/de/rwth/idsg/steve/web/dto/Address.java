package de.rwth.idsg.steve.web.dto;

import com.neovisionaries.i18n.CountryCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.11.2015
 */
@Getter
@Setter
public class Address {

    // Internal database id
    private Integer addressPk;

    private String street;
    private String houseNumber;
    private String zipCode;
    private String city;
    private CountryCode country;

    public boolean isEmpty() {
        return addressPk == null
                && street == null
                && houseNumber == null
                && zipCode == null
                && city == null
                && country == null;
    }

    /**
     * Otherwise, if the country field is not set, we would get a NPE.
     */
    public String getCountryAlpha2OrNull() {
        if (country == null) {
            return null;
        } else {
            return country.getAlpha2();
        }
    }
}
