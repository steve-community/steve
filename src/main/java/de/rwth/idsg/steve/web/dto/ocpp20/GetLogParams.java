package de.rwth.idsg.steve.web.dto.ocpp20;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetLogParams extends BaseParams {

    @NotNull(message = "Request ID is required")
    private Integer requestId;

    @NotBlank(message = "Remote location is required")
    private String remoteLocation;

    @NotBlank(message = "Log type is required")
    private String logType;

    private Integer retries;

    private Integer retryInterval;
}
