package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "advanced_charge_box_config", uniqueConstraints =
@UniqueConstraint(name = "advancedchargebox_cbid_key_UNIQUE", columnNames = {"charge_box_id", "config_key"}))
@Getter
@Setter
public class AdvancedChargeBoxConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "charge_box_id", length = 255, nullable = false)
    private String chargeBoxId;

    @Column(name = "config_key", length = 100, nullable = false)
    private String configKey;

    @Column(name = "config_value", length = 255, nullable = false)
    private String configValue;

    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "mod_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modDate;

    @PrePersist
    public void prePersist() {
        createDate = new Date();
        modDate = new Date(createDate.getTime());
    }

    @PreUpdate
    public void preUpdate() {
        modDate = new Date();
    }
}
