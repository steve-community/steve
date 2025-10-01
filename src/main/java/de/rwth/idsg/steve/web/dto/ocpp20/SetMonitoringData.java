package de.rwth.idsg.steve.web.dto.ocpp20;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetMonitoringData {
    private Integer id;

    private Boolean transaction;

    @NotNull(message = "Value is required")
    private Double value;

    @NotBlank(message = "Type is required")
    private String type;

    @NotNull(message = "Severity is required")
    @Min(value = 0, message = "Severity must be non-negative")
    @Max(value = 9, message = "Severity must not exceed 9")
    private Integer severity;

    @NotNull(message = "Component is required")
    @Valid
    private ComponentDto component = new ComponentDto();

    @NotNull(message = "Variable is required")
    @Valid
    private VariableDto variable = new VariableDto();
}
