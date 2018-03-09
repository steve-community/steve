package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
@Getter
@Setter
public class GetDiagnosticsParams extends MultipleChargePointSelect {

    @NotBlank(message = "Location is required")
    @Pattern(regexp = "\\S+", message = "Location cannot contain any whitespace")
    private String location;

    @Min(value = 1, message = "Retries must be at least {value}")
    private Integer retries;

    @Min(value = 1, message = "Retry Interval must be at least {value}")
    private Integer retryInterval;

    @Past(message = "Start Date/Time must be in the past")
    private LocalDateTime start;

    @Past(message = "Stop Date/Time must be in the past")
    private LocalDateTime stop;

    @AssertTrue(message = "Stop Date/Time must be after Start Date/Time")
    public boolean isValid() {
        return !(start != null && stop != null) || stop.isAfter(start);
    }
}
