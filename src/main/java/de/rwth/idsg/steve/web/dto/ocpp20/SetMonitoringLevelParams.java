package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class SetMonitoringLevelParams extends BaseParams {
    @NotNull(message = "Severity level is required")
    @Min(0)
    @Max(9)
    private Integer severity;
}
