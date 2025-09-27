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
public class Location {

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("party_id")
    private String partyId;

    private String id;

    private Boolean publish;

    @JsonProperty("publish_allowed_to")
    private List<PublishToken> publishAllowedTo;

    private String name;

    private String address;

    private String city;

    @JsonProperty("postal_code")
    private String postalCode;

    private String state;

    private String country;

    private GeoLocation coordinates;

    @JsonProperty("related_locations")
    private List<AdditionalGeoLocation> relatedLocations;

    @JsonProperty("parking_type")
    private ParkingType parkingType;

    private List<EVSE> evses;

    private List<String> directions;

    private BusinessDetails operator;

    private BusinessDetails suboperator;

    private BusinessDetails owner;

    private List<String> facilities;

    @JsonProperty("time_zone")
    private String timeZone;

    @JsonProperty("opening_times")
    private Hours openingTimes;

    @JsonProperty("charging_when_closed")
    private Boolean chargingWhenClosed;

    private List<Image> images;

    @JsonProperty("energy_mix")
    private EnergyMix energyMix;

    @JsonProperty("last_updated")
    private DateTime lastUpdated;
}