package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ocpp_connector", uniqueConstraints =
@UniqueConstraint(name = "connector_cbid_cid_UNIQUE", columnNames = {"charge_box_id", "connector_id"}))
@Getter
@Setter
public class Connector implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connector_pk")
    private int connectorPk;

    @Column(name = "charge_box_id", nullable = false)
    private String chargeBoxId;

    @Column(name = "connector_id", nullable = false)
    private int connectorId;

}