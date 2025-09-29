package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class InstallCertificateParams extends BaseParams {
    @NotNull(message = "Certificate type is required")
    private String certificateType;

    @NotBlank(message = "Certificate is required")
    private String certificate;
}
