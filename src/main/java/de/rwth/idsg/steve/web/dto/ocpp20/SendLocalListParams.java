package de.rwth.idsg.steve.web.dto.ocpp20;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendLocalListParams extends BaseParams {

    @NotNull(message = "Version number is required")
    private Integer versionNumber;

    @NotBlank(message = "Update type is required")
    private String updateType;

    private String authorizationList;
}