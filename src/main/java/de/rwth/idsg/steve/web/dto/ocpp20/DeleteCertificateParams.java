package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class DeleteCertificateParams extends BaseParams {
    @NotNull(message = "Certificate hash data is required")
    private CertificateHashData certificateHashData = new CertificateHashData();
}
