package de.rwth.idsg.steve.ocpp.ws.data.security;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class SignedFirmwareStatusNotificationRequest implements RequestType {

    @NotNull
    private FirmwareStatusType status;

    private Integer requestId;

    public enum FirmwareStatusType {
        Downloaded,
        DownloadFailed,
        Downloading,
        DownloadScheduled,
        DownloadPaused,
        Idle,
        InstallationFailed,
        Installing,
        Installed,
        InstallRebooting,
        InstallScheduled,
        InstallVerificationFailed,
        InvalidSignature,
        SignatureVerified
    }
}
