package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class SetDisplayMessageParams extends BaseParams {
    @NotNull(message = "Message is required")
    private MessageInfo message = new MessageInfo();
}
