package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

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

    @Column(name = "class_name", length = 255, nullable = false)
    private String className;

    @Column(name = "params", nullable = false)
    private String params;

    @Column(name = "end_timestamp", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private DateTime endTimestamp;
}
