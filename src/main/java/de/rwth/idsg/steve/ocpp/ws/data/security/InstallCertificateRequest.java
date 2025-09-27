package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class InstallCertificateRequest implements RequestType {

    @NotNull
    private CertificateUseType certificateType;

    @NotNull
    @Size(max = 5500)
    private String certificate;

    public enum CertificateUseType {
        CentralSystemRootCertificate,
        ManufacturerRootCertificate
    }
}
