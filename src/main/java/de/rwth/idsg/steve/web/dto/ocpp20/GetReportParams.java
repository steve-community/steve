package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for OCPP 2.0 GetReport
 */
@Getter
@Setter
public class GetReportParams extends BaseParams {

    @NotNull
    private Integer requestId;

    private String componentName;
    private String variableName;
    private String componentCriteria; // Active, Available, Enabled, Problem
}