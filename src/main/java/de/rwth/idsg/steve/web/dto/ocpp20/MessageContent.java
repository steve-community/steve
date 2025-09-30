package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class MessageContent {
    @NotBlank(message = "Format is required")
    private String format;

    @NotBlank(message = "Language is required")
    private String language;

    @NotBlank(message = "Content is required")
    private String content;
}
