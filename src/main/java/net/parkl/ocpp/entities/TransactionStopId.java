package net.parkl.ocpp.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class TransactionStopId implements Serializable {
    @Column(name="transaction_pk")
    private int transactionPk;

    @Column(name="event_timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTimestamp;
}
