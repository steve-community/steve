package de.rwth.idsg.steve.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.09.2014
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsForm {

    @Min(value = 1, message = "Heartbeat Interval must be at least {value}")
    @NotNull(message = "Heartbeat Interval is required")
    private Integer heartbeat;

    @Min(value = 0, message = "Expiration must be at least {value}")
    @NotNull(message = "Expiration is required")
    private Integer expiration;
}