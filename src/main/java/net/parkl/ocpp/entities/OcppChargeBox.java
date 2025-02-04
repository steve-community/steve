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
import ocpp.cs._2015._10.RegistrationStatus;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "ocpp_charge_box", uniqueConstraints =
@UniqueConstraint(name = "chargeBoxId_UNIQUE", columnNames = {"charge_box_id"}))
@Getter
@Setter
public class OcppChargeBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_box_pk")
    private int chargeBoxPk;

    @Column(name = "charge_box_id", length = 255, nullable = false)
    private String chargeBoxId;

    @Column(name = "endpoint_address", length = 255, nullable = true)
    private String endpointAddress;

    @Column(name = "ocpp_protocol", length = 255, nullable = true)
    private String ocppProtocol;

    @Column(name = "registration_status", length = 255, nullable = false)
    private String registrationStatus = RegistrationStatus.ACCEPTED.value();

    @Column(name = "charge_point_vendor", length = 255, nullable = true)
    private String chargePointVendor;

    @Column(name = "charge_point_model", length = 255, nullable = true)
    private String chargePointModel;

    @Column(name = "charge_point_serial_number", length = 255, nullable = true)
    private String chargePointSerialNumber;

    @Column(name = "charge_box_serial_number", length = 255, nullable = true)
    private String chargeBoxSerialNumber;

    @Column(name = "fw_version", length = 255, nullable = true)
    private String fwVersion;

    @Column(name = "fw_update_status", length = 255, nullable = true)
    private String fwUpdateStatus;

    @Column(name = "fw_update_timestamp", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fwUpdateTimestamp;

    @Column(name = "iccid", length = 255, nullable = true)
    private String iccid;

    @Column(name = "imsi", length = 255, nullable = true)
    private String imsi;

    @Column(name = "meter_type", length = 255, nullable = true)
    private String meterType;

    @Column(name = "meter_serial_number", length = 255, nullable = true)
    private String meterSerialNumber;

    @Column(name = "diagnostics_status", length = 255, nullable = true)
    private String diagnosticsStatus;

    @Column(name = "diagnostics_timestamp", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime diagnosticsTimestamp;

    @Column(name = "last_heartbeat_timestamp", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastHeartbeatTimestamp;

    @Column(name = "description", nullable = true)
    @Lob
    private String description;

    @Column(name = "note", nullable = true)
    @Lob
    private String note;

    @Column(name = "location_latitude", nullable = true, precision = 11, scale = 8)
    private BigDecimal locationLatitude;

    @Column(name = "location_longitude", nullable = true, precision = 11, scale = 8)
    private BigDecimal locationLongitude;


    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "address_pk", referencedColumnName = "address_pk", insertable = true, updatable = true,
            nullable = true, foreignKey = @ForeignKey(name = "FK_charge_box_address_apk"))
    private OcppAddress address;

    @Column(name = "admin_address", length = 255, nullable = true)
    private String adminAddress;

    @Column(name = "insert_connector_status_after_transaction_msg", length = 255, nullable = true)
    private Boolean insertConnectorStatusAfterTransactionMsg;

    public boolean insertConnectorStatusAfterTransactionMsg () {
        return insertConnectorStatusAfterTransactionMsg != null && insertConnectorStatusAfterTransactionMsg;
    }
}
