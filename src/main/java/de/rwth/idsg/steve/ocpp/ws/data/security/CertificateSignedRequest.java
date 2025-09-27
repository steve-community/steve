package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class CertificateSignedRequest implements RequestType {

    @NotNull
    @Size(max = 10000)
    private String certificateChain;

}