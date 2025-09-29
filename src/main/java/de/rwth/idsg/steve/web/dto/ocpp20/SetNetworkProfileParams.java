package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for OCPP 2.0 SetNetworkProfile
 */
@Getter
@Setter
public class SetNetworkProfileParams extends BaseParams {

    @NotNull
    private Integer configurationSlot;

    @NotNull
    @Size(max = 512)
    private String ocppInterface; // Wired0, Wired1, Wired2, Wired3, Wireless0, Wireless1, Wireless2, Wireless3

    @NotNull
    @Size(max = 512)
    private String ocppTransport; // SOAP, JSON

    @NotNull
    @Size(max = 512)
    private String ocppCsmsUrl;

    private Integer messageTimeout = 60;
    private String securityProfile = "0"; // 0, 1, 2, 3

    private String ocppVersion = "2.0.1";
}