package de.rwth.idsg.steve.web.dto.common;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 01.01.2015
 */
@Setter
@Getter
public class UpdateFirmwareParams extends MultipleChargePointSelect {

    @NotBlank(message = "Location is required")
    @Pattern(regexp = "\\S+", message = "Location cannot contain any whitespace")
    private String location;

    @Min(value = 1, message = "Retries must be at least {value}")
    private Integer retries;

    @Min(value = 1, message = "Retry Interval must be at least {value}")
    private Integer retryInterval;

    @Future(message = "Retrieve Date/Time must be in future")
    private LocalDateTime retrieve;
}
