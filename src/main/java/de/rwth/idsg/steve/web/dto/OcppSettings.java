package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.09.2014
 */
@Getter
@Setter
public class OcppSettings {

    @Min(value = 0)
    @NotNull(message = "heartbeat is required")
    private Integer heartbeat;

    @Min(value = 0)
    @NotNull(message = "expiration is required")
    private Integer expiration;
}