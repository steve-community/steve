package net.parkl.ocpp.entities;

import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.*;
import java.util.Date;

@Entity
@Immutable
@Subselect("SELECT\n" +
        "  tx1.transaction_pk, tx1.connector_pk, tx1.id_tag, tx1.event_timestamp as start_event_timestamp, tx1.start_timestamp, tx1.start_value,\n" +
        "  tx2.event_actor as stop_event_actor, tx2.event_timestamp as stop_event_timestamp, tx2.stop_timestamp, tx2.stop_value, tx2.stop_reason\n" +
        "  FROM ocpp_transaction_start tx1\n" +
        "  LEFT JOIN (\n" +
        "    SELECT s1.*\n" +
        "    FROM ocpp_transaction_stop s1\n" +
        "    WHERE s1.event_timestamp = (SELECT MAX(event_timestamp) FROM ocpp_transaction_stop s2 WHERE s1.transaction_pk = s2.transaction_pk)\n" +
        "    GROUP BY s1.transaction_pk, s1.event_timestamp) tx2\n" +
        "  ON tx1.transaction_pk = tx2.transaction_pk\n")
@Getter
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
}
