package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class SignedUpdateFirmwareRequest implements RequestType {

    @NotNull
    private Integer requestId;

    @NotNull
    private Firmware firmware;

    private Integer retries;

    private Integer retryInterval;

}