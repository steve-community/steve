package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class OCSPRequestData {
    @NotBlank(message = "Hash algorithm is required")
    private String hashAlgorithm;

    @NotBlank(message = "Issuer Name Hash is required")
    private String issuerNameHash;

    @NotBlank(message = "Issuer Key Hash is required")
    private String issuerKeyHash;

    @NotBlank(message = "Serial Number is required")
    private String serialNumber;

    @NotBlank(message = "Responder URL is required")
    private String responderUrl;
}
