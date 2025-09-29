package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class GetDisplayMessagesParams extends BaseParams {
    @NotNull(message = "Request ID is required")
    private Integer requestId;

    private String messageIds;
    private String priority;
    private String state;
}
