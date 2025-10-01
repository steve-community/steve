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
public class GetMonitoringReportParams extends BaseParams {
    @NotNull(message = "Request ID is required")
    @Min(value = 0, message = "Request ID must be non-negative")
    private Integer requestId;

    private List<String> monitoringCriteria = new ArrayList<>();

    private List<ComponentVariableDto> componentVariable = new ArrayList<>(Collections.singletonList(new ComponentVariableDto()));
}
