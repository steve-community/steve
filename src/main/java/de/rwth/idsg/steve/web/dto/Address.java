/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.web.dto;

import com.neovisionaries.i18n.CountryCode;
import de.rwth.idsg.steve.web.validation.TimeZoneId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 24.11.2015
 */
@Getter
@Setter
@ToString
public class Address {

    // Internal database id
    private Integer addressPk;

    private String street;
    private String houseNumber;
    private String zipCode;
    private String city;
    private CountryCode country;

    @Range(min = -90, max = 90, message = "Latitude must be between {min} and {max}")
    private BigDecimal latitude;

    @Range(min = -180, max = 180, message = "Longitude must be between {min} and {max}")
    private BigDecimal longitude;

    @TimeZoneId
    private String timeZone;

    public boolean isEmpty() {
        return addressPk == null
                && StringUtils.isEmpty(street)
                && StringUtils.isEmpty(houseNumber)
                && StringUtils.isEmpty(zipCode)
                && StringUtils.isEmpty(city)
                && country == null
                && latitude == null
                && longitude == null
                && StringUtils.isEmpty(timeZone);
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = StringUtils.isEmpty(timeZone) ? null : timeZone;
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
