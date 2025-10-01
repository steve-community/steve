package de.rwth.idsg.steve.web.dto.ocpp20;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetDisplayMessageParams extends BaseParams {
    @NotNull(message = "Message is required")
    @Valid
    private MessageInfo message = new MessageInfo();
}
