package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 06.11.2015
 */
@Getter
@Builder
public class Settings {

    // Heartbeat interval in min
    private final int heartbeatIntervalInMinutes;

    // Determines how many hours the idtag should be stored in the local whitelist of a chargebox
    private final int hoursToExpire;

}
