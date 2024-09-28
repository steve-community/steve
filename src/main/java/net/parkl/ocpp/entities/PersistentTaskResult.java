package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "persistent_task_result")
@Getter
@Setter
public class PersistentTaskResult implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persistent_task_result_id")
    private int persistentTaskResultId;

    @Column(name = "response")
    @Lob
    private String response;

    @Column(name = "error_message")
    @Lob
    private String errorMessage;

    @Column(name = "charge_box_id", length = 255, nullable = false)
    private String chargeBoxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persistent_task_id", nullable = false)
    private PersistentTask task;
}
