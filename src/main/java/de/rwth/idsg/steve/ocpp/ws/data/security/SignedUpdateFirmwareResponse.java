package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class SignedUpdateFirmwareResponse implements ResponseType {

    @NotNull
    private UpdateFirmwareStatus status;

    public enum UpdateFirmwareStatus {
        Accepted,
        Rejected,
        AcceptedCanceled,
        InvalidCertificate,
        RevokedCertificate
}
}
