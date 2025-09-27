package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class LogStatusNotificationRequest implements RequestType {

    @NotNull
    private UploadLogStatus status;

    private Integer requestId;

    public enum UploadLogStatus {
        BadMessage,
        Idle,
        NotSupportedOperation,
        PermissionDenied,
        Uploaded,
        UploadFailure,
        Uploading
    }
}
