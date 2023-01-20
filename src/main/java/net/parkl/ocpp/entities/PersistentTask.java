package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "persistent_task")
@Getter
@Setter
public class PersistentTask implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persistent_task_id")
    private int persistentTaskId;

    @Column(name = "ocpp_version", length = 255, nullable = false)
    private String ocppVersion;

}
