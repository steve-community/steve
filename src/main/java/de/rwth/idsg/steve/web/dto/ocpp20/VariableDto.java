package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class VariableDto {
    @NotBlank(message = "Variable name is required")
    private String name;

    private String instance;
}
