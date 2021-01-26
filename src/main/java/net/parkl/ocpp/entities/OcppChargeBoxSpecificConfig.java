package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ocpp_charge_box_spec_config",uniqueConstraints=
    @UniqueConstraint(name="chargeboxspecconfig_cbid_key_UNIQUE",columnNames={"charge_box_id", "config_key"})
)
@Getter
@Setter
public class OcppChargeBoxSpecificConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ocpp_charge_box_spec_config_id")
    private int ocppChargeBoxConfigId;

    @Column(name="charge_box_id",length=255,nullable=false)
    private String chargeBoxId;

    /**
     * Beállítás neve
     */
    @Column(name = "config_key", length = 100, nullable = false)
    private String configKey;

    /**
     * Beállítás értéke
     */
    @Column(name = "config_value", length = 255, nullable = false)
    private String configValue;

    /**
     * Létrehozás időpontja
     */
    @Column(name="create_date", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;


    /**
     * Utolsó módosítás időpontja
     */
    @Column(name="mod_date", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modDate;


    /**
     * INSERT előtt lefutó metódus
     */
    @PrePersist
    public void prePersist() {
        createDate=new Date();
        modDate=new Date(createDate.getTime());
    }

    /**
     * UPDATE előtt lefutó metódus
     */
    @PreUpdate
    public void preUpdate() {
        modDate=new Date();
    }
}
