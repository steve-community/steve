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
package de.rwth.idsg.steve.gateway.oicp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * OICP v2.3 EVSEData model
 *
 * @author Steve Community
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EVSEData {

    @JsonProperty("EvseID")
    private String evseId;

    @JsonProperty("ChargingStationID")
    private String chargingStationId;

    @JsonProperty("ChargingStationName")
    private String chargingStationName;

    @JsonProperty("Address")
    private Address address;

    @JsonProperty("GeoCoordinates")
    private GeoCoordinates geoCoordinates;

    @JsonProperty("Plugs")
    private List<PlugType> plugs;

    @JsonProperty("ChargingFacilities")
    private List<ChargingFacility> chargingFacilities;

    @JsonProperty("RenewableEnergy")
    private Boolean renewableEnergy;

    @JsonProperty("CalibrationLawDataAvailability")
    private CalibrationLawDataAvailability calibrationLawDataAvailability;

    @JsonProperty("AuthenticationModes")
    private List<AuthenticationMode> authenticationModes;

    @JsonProperty("MaxCapacity")
    private BigDecimal maxCapacity;

    @JsonProperty("PaymentOptions")
    private List<PaymentOption> paymentOptions;

    @JsonProperty("ValueAddedServices")
    private List<ValueAddedService> valueAddedServices;

    @JsonProperty("Accessibility")
    private AccessibilityType accessibility;

    @JsonProperty("HotlinePhoneNumber")
    private String hotlinePhoneNumber;

    @JsonProperty("AdditionalInfo")
    private List<AdditionalInfo> additionalInfo;

    @JsonProperty("ChargingStationLocationReference")
    private List<ChargingStationLocationReference> chargingStationLocationReference;

    @JsonProperty("GeoChargingPointEntrance")
    private GeoCoordinates geoChargingPointEntrance;

    @JsonProperty("IsOpen24Hours")
    private Boolean isOpen24Hours;

    @JsonProperty("OpeningTimes")
    private List<OpeningTime> openingTimes;

    @JsonProperty("HubOperatorID")
    private String hubOperatorId;

    @JsonProperty("ClearingHouseID")
    private String clearingHouseId;

    @JsonProperty("IsHubjectCompatible")
    private Boolean isHubjectCompatible;

    @JsonProperty("DynamicInfoAvailable")
    private DynamicInfoAvailable dynamicInfoAvailable;

    @JsonProperty("LastUpdate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private DateTime lastUpdate;
}