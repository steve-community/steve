package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class Get15118EVCertificateParams extends BaseParams {
    @NotBlank(message = "ISO15118 Schema Version is required")
    private String iso15118SchemaVersion;

    @NotBlank(message = "Action is required")
    private String action;

    @NotBlank(message = "EXI request is required")
    private String exiRequest;
}
