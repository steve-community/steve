package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "transaction_stop_failed")
@Getter
@Setter
public class TransactionStopFailed extends TransactionStop {

    @Column(name = "fail_reason", nullable = true)
    @Lob
    private String failReason;
}
