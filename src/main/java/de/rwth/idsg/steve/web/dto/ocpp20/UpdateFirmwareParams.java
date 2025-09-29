package de.rwth.idsg.steve.web.dto.ocpp20;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFirmwareParams extends BaseParams {

    @NotNull(message = "Request ID is required")
    private Integer requestId;

    @NotBlank(message = "Firmware location is required")
    private String location;

    private Integer retries;

    private Integer retryInterval;
}
