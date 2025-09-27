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

import java.math.BigDecimal;
import java.util.List;

/**
 * OCPI v2.2 CDR (Charge Detail Record) model
 *
 * @author Steve Community
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CDR {

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("party_id")
    private String partyId;

    @JsonProperty("id")
    private String id;

    @JsonProperty("start_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private DateTime startDateTime;

    @JsonProperty("end_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private DateTime endDateTime;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("cdr_token")
    private CdrToken cdrToken;

    @JsonProperty("auth_method")
    private AuthMethod authMethod;

    @JsonProperty("authorization_reference")
    private String authorizationReference;

    @JsonProperty("cdr_location")
    private CdrLocation cdrLocation;

    @JsonProperty("meter_id")
    private String meterId;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("tariffs")
    private List<Tariff> tariffs;

    @JsonProperty("charging_periods")
    private List<ChargingPeriod> chargingPeriods;

    @JsonProperty("signed_data")
    private SignedData signedData;

    @JsonProperty("total_cost")
    private Price totalCost;

    @JsonProperty("total_fixed_cost")
    private Price totalFixedCost;

    @JsonProperty("total_energy")
    private BigDecimal totalEnergy;

    @JsonProperty("total_energy_cost")
    private Price totalEnergyCost;

    @JsonProperty("total_time")
    private BigDecimal totalTime;

    @JsonProperty("total_time_cost")
    private Price totalTimeCost;

    @JsonProperty("total_parking_time")
    private BigDecimal totalParkingTime;

    @JsonProperty("total_parking_cost")
    private Price totalParkingCost;

    @JsonProperty("total_reservation_cost")
    private Price totalReservationCost;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("invoice_reference_id")
    private String invoiceReferenceId;

    @JsonProperty("credit")
    private Boolean credit;

    @JsonProperty("credit_reference_id")
    private String creditReferenceId;

    @JsonProperty("last_updated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private DateTime lastUpdated;
}