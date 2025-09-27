package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class DeleteCertificateResponse implements ResponseType {

    @NotNull
    private DeleteCertificateStatus status;

    public enum DeleteCertificateStatus {
        Accepted,
        Failed,
        NotFound
}
}
