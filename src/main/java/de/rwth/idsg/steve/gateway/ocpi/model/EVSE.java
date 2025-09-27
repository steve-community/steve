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
public class EVSE {

    private String uid;

    @JsonProperty("evse_id")
    private String evseId;

    private StatusType status;

    @JsonProperty("status_schedule")
    private List<StatusSchedule> statusSchedule;

    private List<String> capabilities;

    private List<Connector> connectors;

    @JsonProperty("floor_level")
    private String floorLevel;

    private GeoLocation coordinates;

    @JsonProperty("physical_reference")
    private String physicalReference;

    private List<String> directions;

    @JsonProperty("parking_restrictions")
    private List<ParkingRestriction> parkingRestrictions;

    private List<Image> images;

    @JsonProperty("last_updated")
    private DateTime lastUpdated;
}