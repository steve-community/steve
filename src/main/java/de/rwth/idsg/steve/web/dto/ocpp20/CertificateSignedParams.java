package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class CertificateSignedParams extends BaseParams {
    @NotBlank(message = "Certificate chain is required")
    private String certificateChain;

    private String certificateType;
}
