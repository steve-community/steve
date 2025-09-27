/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve.gateway.ocpi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.List;

/**
 * OCPI v2.2 Tariff model
 *
 * @author Steve Community
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("party_id")
    private String partyId;

    @JsonProperty("id")
    private String id;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("type")
    private TariffType type;

    @JsonProperty("tariff_alt_text")
    private List<DisplayText> tariffAltText;

    @JsonProperty("tariff_alt_url")
    private String tariffAltUrl;

    @JsonProperty("min_price")
    private Price minPrice;

    @JsonProperty("max_price")
    private Price maxPrice;

    @JsonProperty("elements")
    private List<TariffElement> elements;

    @JsonProperty("start_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private DateTime startDateTime;

    @JsonProperty("end_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private DateTime endDateTime;

    @JsonProperty("energy_mix")
    private EnergyMix energyMix;

    @JsonProperty("last_updated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private DateTime lastUpdated;
}