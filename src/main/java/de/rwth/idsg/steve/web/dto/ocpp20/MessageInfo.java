package de.rwth.idsg.steve.web.dto.ocpp20;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageInfo {
    @NotNull(message = "Message ID is required")
    @Min(value = 1, message = "Message ID must be positive")
    private Integer id;

    @NotBlank(message = "Priority is required")
    private String priority;

    private String state;

    private String startDateTime;

    private String endDateTime;

    private String transactionId;

    @NotNull(message = "Message content is required")
    @Valid
    private MessageContent message = new MessageContent();

    @Valid
    private Display display;
}
