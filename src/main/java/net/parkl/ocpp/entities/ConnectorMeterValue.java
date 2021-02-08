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
@Table(name = "ocpp_connector_meter_value")
@Getter
@Setter
public class ConnectorMeterValue implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmv_pk")
    private int cmvPk;

    private String format;

    private String location;

    private String measurand;

    @Column(name = "reading_context")
    private String readingContext;

    private String unit;

    private String value;

    @Column(name = "value_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date valueTimestamp;

    @ManyToOne
    @JoinColumn(name = "connector_pk", nullable = false)
    private Connector connector;

    @ManyToOne
    @JoinColumn(name = "transaction_pk")
    private TransactionStart transaction;

    @Column(name = "phase", length = 255, nullable = true)
    private String phase;

}