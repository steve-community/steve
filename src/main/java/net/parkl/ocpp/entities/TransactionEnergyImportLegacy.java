package net.parkl.ocpp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;

/**
 * Entity class for legacy transaction energy import view (Mennekes type chargers: no measurand, no unit).
 */
@Entity
@Table(name = "transaction_energy_import_legacy")
@Immutable
@Getter
public class TransactionEnergyImportLegacy extends AbstractTransactionEnergyImport implements Serializable {
    @Id
    @Column(name = "transaction_pk")
    private int transactionPk;


}
