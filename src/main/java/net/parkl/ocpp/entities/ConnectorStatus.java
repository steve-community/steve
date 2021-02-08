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
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ocpp_connector_status")
@Getter
@Setter
public class ConnectorStatus implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connector_status_pk")
    private int connectorStatusPk;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_info")
    private String errorInfo;

    private String status;

    @Column(name = "status_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusTimestamp;

    @Column(name = "vendor_error_code")
    private String vendorErrorCode;

    @Column(name = "vendor_id")
    private String vendorId;

    @ManyToOne
    @JoinColumn(name = "connector_pk", nullable = false)
    private Connector connector;

}