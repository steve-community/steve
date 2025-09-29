package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class ClearVariableMonitoringParams extends BaseParams {
    @NotBlank(message = "Monitoring IDs are required")
    private String monitoringIds; // comma-separated
}
