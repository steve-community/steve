package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class GetLogRequest implements RequestType {

    @NotNull
    private LogType logType;

    @NotNull
    private Integer requestId;

    @NotNull
    private LogParameters log;

    private Integer retries;

    private Integer retryInterval;

    public enum LogType {
        DiagnosticsLog,
        SecurityLog
    }
}
