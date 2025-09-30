package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class ComponentDto {
    @NotBlank(message = "Component name is required")
    private String name;

    private String instance;

    private Integer evseId;

    private Integer connectorId;
}
