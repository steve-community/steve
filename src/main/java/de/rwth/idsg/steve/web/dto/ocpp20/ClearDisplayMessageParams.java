package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class ClearDisplayMessageParams extends BaseParams {
    @NotNull(message = "Display message ID is required")
    @Min(value = 1, message = "Display message ID must be positive")
    private Integer id;
}
