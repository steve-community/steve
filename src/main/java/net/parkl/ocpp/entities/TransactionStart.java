package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ocpp_transaction_start")
@Getter
@Setter
@NoArgsConstructor
public class TransactionStart implements Serializable {
    @Id
    @Column(name = "transaction_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionPk;

    @Column(name = "start_timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTimestamp;

    @Column(name = "start_value")
    private String startValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connector_pk", nullable = false)
    private Connector connector;

    @Column(name = "id_tag", length = 255, nullable = false)
    private String ocppTag;

    @Column(name = "event_timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTimestamp;

}