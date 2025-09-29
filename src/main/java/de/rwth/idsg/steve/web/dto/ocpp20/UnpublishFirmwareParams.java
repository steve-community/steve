package de.rwth.idsg.steve.web.dto.ocpp20;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class UnpublishFirmwareParams extends BaseParams {
    @NotBlank(message = "Checksum is required")
    private String checksum;
}
