package net.parkl.ocpp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;

@Entity
@Table(name = "transaction_energy_import")
@Immutable
@Getter
public class TransactionEnergyImport implements Serializable {
    @Id
    @Column(name = "transaction_pk")
    private int transactionPk;

    @Column(name = "start_value")
    private float startValue;
    @Column(name = "end_value")
    private float endValue;

    @Column(name = "unit")
    private String unit;
}
