package de.rwth.idsg.steve.web.dto.ocpp20;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetChargingProfilesParams extends BaseParams {

    @NotNull(message = "Request ID is required")
    private Integer requestId;

    private Integer evseId;
}
