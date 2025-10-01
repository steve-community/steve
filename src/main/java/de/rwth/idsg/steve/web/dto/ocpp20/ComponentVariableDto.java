package de.rwth.idsg.steve.web.dto.ocpp20;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponentVariableDto {
    @Valid
    private ComponentDto component = new ComponentDto();

    @Valid
    private VariableDto variable = new VariableDto();
}
