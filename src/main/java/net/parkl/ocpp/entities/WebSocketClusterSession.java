package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "web_socket_cluster_session")
@Getter
@Setter
public class WebSocketClusterSession implements Serializable {
    @Id
    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "pod_ip", nullable = false, length = 30)
    private String podIp;

    @Column(name = "charge_box_id", nullable = false)
    private String chargeBoxId;


    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @PrePersist
    public void prePersist() {
        this.createDate = new Date();
    }
}
