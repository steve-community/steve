package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class CustomerInformationParams extends BaseParams {
    @NotNull(message = "Request ID is required")
    private Integer requestId;

    private Boolean report = true;
    private Boolean clear = false;
    private String customerIdentifier;

    private String idToken;
    private String idTokenType;
}
