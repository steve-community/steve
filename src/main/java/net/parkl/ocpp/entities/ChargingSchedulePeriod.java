package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "charging_schedule_period", uniqueConstraints=
    @UniqueConstraint(name="UQ_charging_schedule_period",columnNames={"charging_profile_pk", "start_period_in_seconds"})
)
@Getter
@Setter
public class ChargingSchedulePeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="charging_schedule_period_pk")
    private int chargingSchedulePeriodPk;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="charging_profile_pk", referencedColumnName = "charging_profile_pk", nullable=false,
        foreignKey = @ForeignKey(name = "FK_connector_charging_profile_charging_profile_pk"))
    private OcppChargingProfile chargingProfile;

    @Column(name = "start_period_in_seconds", nullable = false)
    private int startPeriodInSeconds;

    @Column(name = "power_limit_in_amperes", nullable = false, precision = 15, scale = 1)
    private BigDecimal powerLimitInAmperes;

    @Column(name = "number_phases", nullable = true)
    private Integer numberPhases;
}
