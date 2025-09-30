package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class GetMonitoringReportParams extends BaseParams {
    @NotNull(message = "Request ID is required")
    @Min(value = 0, message = "Request ID must be non-negative")
    private Integer requestId;

    private List<String> monitoringCriteria;

    private List<ComponentVariableDto> componentVariable;
}
