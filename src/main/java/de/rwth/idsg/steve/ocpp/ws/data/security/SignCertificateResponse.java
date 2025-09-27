package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class SignCertificateResponse implements ResponseType {

    @NotNull
    private GenericStatus status;

    public enum GenericStatus {
        Accepted,
        Rejected
}
}
