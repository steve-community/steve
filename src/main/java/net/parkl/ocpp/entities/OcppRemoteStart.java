package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ocpp_remote_start")
@Getter
@Setter
public class OcppRemoteStart implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "remote_start_id")
    private int remoteStartId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connector_pk", nullable = false)
    private Connector connector;

    @Column(name = "id_tag")
    private String ocppTag;

    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;

    @PrePersist
    public void prePersist() {
        createDate = LocalDateTime.now();
    }
}
