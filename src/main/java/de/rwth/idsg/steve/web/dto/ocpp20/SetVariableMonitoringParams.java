package de.rwth.idsg.steve.web.dto.ocpp20;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class SetVariableMonitoringParams extends BaseParams {
    @NotNull(message = "Monitoring data is required")
    @Size(min = 1, message = "At least one monitoring data item is required")
    @Valid
    private List<SetMonitoringData> setMonitoringData = new ArrayList<>(Collections.singletonList(new SetMonitoringData()));
}
