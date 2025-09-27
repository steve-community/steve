package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class GetInstalledCertificateIdsRequest implements RequestType {

    @NotNull
    private CertificateUseType certificateType;

    public enum CertificateUseType {
        CentralSystemRootCertificate,
        ManufacturerRootCertificate
    }
}
