package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

@Getter
@Builder
public class StatusEvent {
    private final Integer jobId;
    private final String chargeBoxId;
    private final int chargeBoxPk;
    private final String status;
    private final DateTime timestamp;
}
