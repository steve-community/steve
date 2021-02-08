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
@Table(name = "ocpp_charging_process")
@Getter
@Setter
public class OcppChargingProcess {
    @Id
    @Column(name = "ocpp_charging_process_id")
    private String ocppChargingProcessId;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "end_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name = "stop_request_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date stopRequestDate;

    @Column(name = "license_plate", nullable = true, length = 20)
    private String licensePlate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connector_pk", nullable = false)
    private Connector connector;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_pk", nullable = true)
    private TransactionStart transactionStart;

    @Column(name = "id_tag", length = 255, nullable = true)
    private String ocppTag;

    @Column(name = "error_code", length = 100, nullable = true)
    private String errorCode;

    @Column(name = "limit_kwh", nullable = true)
    private Float limitKwh;

    @Column(name = "limit_minute", nullable = true)
    private Integer limitMinute;

    @PrePersist
    public void prePersist() {
        startDate = new Date();
    }

    public boolean stoppedExternally() {
        return stopRequestDate == null;
    }

}
