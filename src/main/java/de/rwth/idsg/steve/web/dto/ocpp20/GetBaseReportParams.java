package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for OCPP 2.0 GetBaseReport
 */
@Getter
@Setter
public class GetBaseReportParams extends BaseParams {

    @NotNull
    private Integer requestId;

    @NotNull
    private String reportBase = "FullInventory"; // FullInventory, ConfigurationInventory, SummaryInventory
}