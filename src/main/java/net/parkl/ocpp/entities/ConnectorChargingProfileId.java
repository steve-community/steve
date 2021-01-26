package net.parkl.ocpp.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class ConnectorChargingProfileId implements Serializable {
    @Column(name="connector_pk")
    private int connectorPk;

    @Column(name="charging_profile_pk")
    private int chargingProfilePk;


}
