package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class GetInstalledCertificateIdsResponse implements ResponseType {

    @NotNull
    private GetInstalledCertificateStatus status;

    private List<CertificateHashData> certificateHashData;

    public enum GetInstalledCertificateStatus {
        Accepted,
        NotFound
}
}
