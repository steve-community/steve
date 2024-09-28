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
import java.util.Date;

@Entity
@Table(name = "ocpp_transaction_stop")
@Inheritance(strategy=InheritanceType.JOINED)
@Getter
@Setter
public class TransactionStop {
    @EmbeddedId
    private TransactionStopId transactionStopId;

    @MapsId("transactionPk")
    @JoinColumn(name = "transaction_pk", referencedColumnName = "transaction_pk",
            foreignKey = @ForeignKey(name = "FK_transaction_stop_transaction_pk"))
    @ManyToOne(cascade = CascadeType.REMOVE)
    private TransactionStart transaction;

    @Column(name = "event_actor", length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionStopEventActor eventActor;

    @Column(name = "stop_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stopTimestamp;

    @Column(name = "stop_value")
    private String stopValue;

    @Column(name = "stop_reason", length = 255, nullable = true)
    private String stopReason;
}
