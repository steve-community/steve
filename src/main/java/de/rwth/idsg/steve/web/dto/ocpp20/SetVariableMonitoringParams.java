package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class SetVariableMonitoringParams extends BaseParams {
    @NotNull(message = "Monitoring data is required")
    @Size(min = 1, message = "At least one monitoring data item is required")
    private List<SetMonitoringData> setMonitoringData;
}
