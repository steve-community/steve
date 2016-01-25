package de.rwth.idsg.steve.repository.dto;

import de.rwth.idsg.steve.NotificationFeature;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 22.01.2016
 */
@Getter
@Builder
public class MailSettings {
    private final boolean enabled;
    private final String host, username, password, from, protocol;
    private final Integer port;
    private final List<String> recipients;
    private final List<NotificationFeature> enabledFeatures;
}
