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

@Entity
@Table(name = "charging_schedule_period", uniqueConstraints =
@UniqueConstraint(name = "UQ_charging_schedule_period", columnNames = {"charging_profile_pk", "start_period_in_seconds"}))
@Getter
@Setter
public class ChargingSchedulePeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charging_schedule_period_pk")
    private int chargingSchedulePeriodPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_profile_pk", referencedColumnName = "charging_profile_pk", nullable = false,
            foreignKey = @ForeignKey(name = "FK_connector_charging_profile_charging_profile_pk"))
    private OcppChargingProfile chargingProfile;

    @Column(name = "start_period_in_seconds", nullable = false)
    private int startPeriodInSeconds;

    @Column(name = "power_limit", nullable = false, precision = 15, scale = 1)
    private BigDecimal powerLimit;

    @Column(name = "number_phases", nullable = true)
    private Integer numberPhases;
}
