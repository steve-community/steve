package net.parkl.ocpp.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ChargingConsumptionState")
public class ChargingConsumptionState implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private String id;

    @Column(name = "CreateDate",nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name="ExternalChargerId",length=50, nullable=true)
    private String externalChargerId;

    @Column(name="ExternalChargeBoxId",length=50, nullable=true)
    private String externalChargeBoxId;

    @Column(name="ExternalChargeId",length=255, nullable=true)
    private String externalChargeId;

    @Column(name = "Start",nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Column(name = "EndDate",nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    @Column(name = "TotalPower",nullable=true)
    private Float totalPower;

    @Column(name = "StartValue",nullable=true)
    private Float startValue;

    @Column(name = "StopValue",nullable=true)
    private Float stopValue;
}
