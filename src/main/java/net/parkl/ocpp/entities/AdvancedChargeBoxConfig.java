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
import java.util.Date;

@Entity
@Table(name = "advanced_charge_box_config", uniqueConstraints =
@UniqueConstraint(name = "advancedchargebox_cbid_key_UNIQUE", columnNames = {"charge_box_id", "config_key"}))
@Getter
@Setter
public class AdvancedChargeBoxConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "charge_box_id", length = 255, nullable = false)
    private String chargeBoxId;

    @Column(name = "config_key", length = 100, nullable = false)
    private String configKey;

    @Column(name = "config_value", length = 255, nullable = false)
    private String configValue;

    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "mod_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modDate;

    @PrePersist
    public void prePersist() {
        createDate = new Date();
        modDate = new Date(createDate.getTime());
    }

    @PreUpdate
    public void preUpdate() {
        modDate = new Date();
    }
}
