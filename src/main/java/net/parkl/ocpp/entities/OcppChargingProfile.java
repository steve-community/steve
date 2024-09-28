/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ocpp_charging_profile")
@Getter
@Setter
public class OcppChargingProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charging_profile_pk")
    private int chargingProfilePk;

    @Column(name = "stack_level", nullable = false)
    private int stackLevel;

    @Column(name = "charging_profile_purpose", nullable = false)
    private String chargingProfilePurpose;

    @Column(name = "charging_profile_kind", nullable = false)
    private String chargingProfileKind;

    @Column(name = "recurrency_kind", nullable = true)
    private String recurrencyKind;

    @Column(name = "valid_from", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;

    @Column(name = "valid_to", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date validTo;

    @Column(name = "duration_in_seconds", nullable = true)
    private Integer durationInSeconds;

    @Column(name = "start_schedule", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startSchedule;

    @Column(name = "charging_rate_unit", nullable = false)
    private String chargingRateUnit;

    @Column(name = "min_charging_rate", nullable = true, precision = 15, scale = 1)
    private BigDecimal minChargingRate;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "note", nullable = true)
    @Lob
    private String note;
}
