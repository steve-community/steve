package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class ComponentVariableDto {
    @NotNull(message = "Component is required")
    private ComponentDto component = new ComponentDto();

    private VariableDto variable = new VariableDto();
}
