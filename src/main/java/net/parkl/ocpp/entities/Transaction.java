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
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Immutable
@Subselect("SELECT tx1.transaction_pk, tx1.connector_pk, tx1.id_tag, tx1.event_timestamp as start_event_timestamp, " +
                   "tx1.start_timestamp, tx1.start_value, tx2.event_actor as stop_event_actor, " +
                   "tx2.event_timestamp as stop_event_timestamp, tx2.stop_timestamp, tx2.stop_value, tx2.stop_reason " +
                   "FROM ocpp_transaction_start tx1 " +
                   "LEFT JOIN ocpp_transaction_stop tx2 " +
                   "ON tx1.transaction_pk = tx2.transaction_pk")
@Getter
@ToString
public class Transaction {
    @Id
    @Column(name = "transaction_pk")
    private int transactionPk;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connector_pk")
    private Connector connector;

    @Column(name = "id_tag")
    private String ocppTag;

    @Column(name = "start_event_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startEventTimestamp;

    @Column(name = "start_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTimestamp;

    @Column(name = "start_value")
    private String startValue;

    @Column(name = "stop_event_actor")
    @Enumerated(EnumType.STRING)
    private TransactionStopEventActor stopEventActor;

    @Column(name = "stop_event_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stopEventTimestamp;

    @Column(name = "stop_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stopTimestamp;

    @Column(name = "stop_value")
    private String stopValue;

    @Column(name = "stop_reason")
    private String stopReason;

    public boolean vehicleUnplugged() {
        return stopValue == null;
    }
}
