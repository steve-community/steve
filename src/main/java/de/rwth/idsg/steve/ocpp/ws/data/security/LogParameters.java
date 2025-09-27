package de.rwth.idsg.steve.ocpp.ws.data.security;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class LogParameters {

    @NotNull
    @Size(max = 512)
    private String remoteLocation;

    private String oldestTimestamp;

    private String latestTimestamp;
}