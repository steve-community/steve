package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class GetCertificateStatusParams extends BaseParams {
    @NotNull(message = "OCSP request data is required")
    private OCSPRequestData ocspRequestData = new OCSPRequestData();
}
