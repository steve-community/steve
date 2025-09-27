package de.rwth.idsg.steve.ocpp.ws.data.security;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class CertificateHashData {

    @NotNull
    private HashAlgorithm hashAlgorithm;

    @NotNull
    @Size(max = 128)
    private String issuerNameHash;

    @NotNull
    @Size(max = 128)
    private String issuerKeyHash;

    @NotNull
    @Size(max = 40)
    private String serialNumber;

    public enum HashAlgorithm {
        SHA256,
        SHA384,
        SHA512
}
}
