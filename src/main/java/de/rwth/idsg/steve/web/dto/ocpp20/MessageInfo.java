package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

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
    private MessageContent message = new MessageContent();

    private Display display;
}
