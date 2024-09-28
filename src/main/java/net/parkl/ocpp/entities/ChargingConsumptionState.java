package net.parkl.ocpp.entities;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "charging_consumption_state")
public class ChargingConsumptionState implements Serializable {
    @Id
    @Column(name = "ExternalChargeId")
    private String externalChargeId;

    @Column(name = "CreateDate",nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name="ExternalChargerId",length=50, nullable=false)
    private String externalChargerId;

    @Column(name="ExternalChargeBoxId",length=50, nullable=false)
    private String externalChargeBoxId;


    @Column(name = "Start",nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Column(name = "EndDate",nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    @Column(name = "TotalPower",nullable=false)
    private Float totalPower;

    @Column(name = "StartValue",nullable=true)
    private Float startValue;

    @Column(name = "StopValue",nullable=true)
    private Float stopValue;

    @PrePersist
    public void prePersist() {
        createDate = new Date();
    }
}
