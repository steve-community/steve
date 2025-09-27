package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CertificateSignedResponse implements ResponseType {

    @NotNull
    private CertificateSignedStatus status;

    public enum CertificateSignedStatus {
        Accepted,
        Rejected
}
}
