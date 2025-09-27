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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.List;

@Data
public class Connector {

    private String id;

    private ConnectorType standard;

    private ConnectorFormat format;

    @JsonProperty("power_type")
    private PowerType powerType;

    @JsonProperty("max_voltage")
    private Integer maxVoltage;

    @JsonProperty("max_amperage")
    private Integer maxAmperage;

    @JsonProperty("max_electric_power")
    private Integer maxElectricPower;

    @JsonProperty("tariff_ids")
    private List<String> tariffIds;

    @JsonProperty("terms_and_conditions")
    private String termsAndConditions;

    @JsonProperty("last_updated")
    private DateTime lastUpdated;
}