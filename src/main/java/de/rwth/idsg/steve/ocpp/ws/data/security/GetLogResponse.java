package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.ResponseType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class GetLogResponse implements ResponseType {

    @NotNull
    private LogStatus status;

    private String filename;

    public enum LogStatus {
        Accepted,
        Rejected,
        AcceptedCanceled
}
}
