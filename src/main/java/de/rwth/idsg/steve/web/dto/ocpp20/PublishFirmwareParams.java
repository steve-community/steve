package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class PublishFirmwareParams extends BaseParams {
    @NotBlank(message = "Location URL is required")
    private String location;

    private Integer retries = 3;
    private Integer retryInterval = 60;

    @NotBlank(message = "Checksum is required")
    private String checksum;

    @NotNull(message = "Request ID is required")
    private Integer requestId;
}
