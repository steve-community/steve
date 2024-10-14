package net.parkl.ocpp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.io.Serializable;

@MappedSuperclass
@Getter
public abstract class AbstractTransactionEnergyImport implements Serializable {
    @Column(name = "start_value")
    private float startValue;
    @Column(name = "end_value")
    private float endValue;

    @Column(name = "unit")
    private String unit;
}
