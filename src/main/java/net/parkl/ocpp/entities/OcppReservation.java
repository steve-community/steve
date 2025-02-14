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
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "ocpp_reservation")
@Getter
@Setter
public class OcppReservation implements Serializable {
    @Id
    @Column(name = "reservation_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservationPk;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiry_datetime")
    private LocalDateTime expiryDatetime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_datetime")
    private LocalDateTime startDatetime;

    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connector_pk", nullable = false)
    private Connector connector;

    @Column(name = "id_tag", length = 255, nullable = false)
    private String ocppTag;

    @ManyToOne
    @JoinColumn(name = "transaction_pk", nullable = true)
    private TransactionStart transaction;

}
