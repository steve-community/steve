package de.rwth.idsg.steve.web.dto.ocpp20;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCompositeScheduleParams extends BaseParams {

    @NotNull(message = "Duration is required")
    private Integer duration;

    @NotNull(message = "EVSE ID is required")
    private Integer evseId;

    private String chargingRateUnit;
}
