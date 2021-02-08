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

import javax.persistence.*;

@Entity
@Table(name = "ocpp_connector_charging_profile", uniqueConstraints =
@UniqueConstraint(name = "UQ_connector_charging_profile", columnNames = {"connector_pk", "charging_profile_pk"}))
@Getter
@Setter
public class ConnectorChargingProfile {
    @EmbeddedId
    private ConnectorChargingProfileId connectorChargingProfileId;

    @MapsId("connectorPk")
    @JoinColumn(name = "connector_pk", referencedColumnName = "connector_pk",
            foreignKey = @ForeignKey(name = "FK_connector_charging_profile_connector_pk"))
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Connector connector;

    @MapsId("chargingProfilePk")
    @JoinColumn(name = "charging_profile_pk", referencedColumnName = "charging_profile_pk",
            foreignKey = @ForeignKey(name = "FK_charging_schedule_period_charging_profile_pk"))
    @ManyToOne(cascade = CascadeType.REMOVE)
    private OcppChargingProfile chargingProfile;
}
