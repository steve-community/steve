package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ocpp_connector_charging_profile", uniqueConstraints =
@UniqueConstraint(name = "UQ_connector_charging_profile", columnNames = {"connector_pk", "charging_profile_pk"}))
@Getter
@Setter
public class ConnectorChargingProfile {
    @EmbeddedId
    private ConnectorChargingProfileId connectorChargingProfileId;

    @MapsId("connectorPk")
    @JoinColumn(name = "connector_pk", referencedColumnName = "connector_pk",
            foreignKey = @ForeignKey(name = "FK_connector_charging_profile_connector_pk"))
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Connector connector;

    @MapsId("chargingProfilePk")
    @JoinColumn(name = "charging_profile_pk", referencedColumnName = "charging_profile_pk",
            foreignKey = @ForeignKey(name = "FK_charging_schedule_period_charging_profile_pk"))
    @ManyToOne(cascade = CascadeType.REMOVE)
    private OcppChargingProfile chargingProfile;
}
