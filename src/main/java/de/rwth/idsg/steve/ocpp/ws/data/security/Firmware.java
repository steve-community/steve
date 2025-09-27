package de.rwth.idsg.steve.ocpp.ws.data.security;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class Firmware {

    @NotNull
    @Size(max = 512)
    private String location;

    @NotNull
    private String retrieveDateTime;

    private String installDateTime;

    @NotNull
    @Size(max = 5500)
    private String signingCertificate;

    @NotNull
    @Size(max = 800)
    private String signature;
}