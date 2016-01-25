package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.web.validation.EmailCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.09.2014
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsForm {

    // -------------------------------------------------------------------------
    // OCPP
    // -------------------------------------------------------------------------

    @Min(value = 1, message = "Heartbeat Interval must be at least {value}")
    @NotNull(message = "Heartbeat Interval is required")
    private Integer heartbeat;

    @Min(value = 0, message = "Expiration must be at least {value}")
    @NotNull(message = "Expiration is required")
    private Integer expiration;

    // -------------------------------------------------------------------------
    // Mail notification
    // -------------------------------------------------------------------------

    @NotNull
    private Boolean enabled;

    @Email(message = "'From' field is not a valid e-mail address")
    private String from;

    private String host, username, password, protocol;

    @Min(value = 1, message = "Port must be positive")
    private Integer port;

    @EmailCollection
    private List<String> recipients;

    private List<NotificationFeature> enabledFeatures;
}
