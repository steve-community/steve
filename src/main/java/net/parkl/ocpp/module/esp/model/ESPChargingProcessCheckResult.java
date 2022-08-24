package net.parkl.ocpp.module.esp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ESPChargingProcessCheckResult implements Serializable {
    private boolean exists;
    private Long timeElapsedSinceStop;

    @JsonIgnore
    public boolean isStopped() {
        return timeElapsedSinceStop != null;
    }
}
