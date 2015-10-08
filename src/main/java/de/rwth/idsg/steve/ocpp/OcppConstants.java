package de.rwth.idsg.steve.ocpp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * This class holds the values that are relevant to OCPP
 * and used by the application.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Component
public class OcppConstants {

    // Heartbeat interval in seconds
    private int heartbeatInterval = 14400;

    // Determines how many hours the idtag should be stored in the local whitelist of a chargebox
    @Getter @Setter private int hoursToExpire = 1;

    public int getHeartbeatIntervalInSeconds() {
        return heartbeatInterval;
    }

    public void setHeartbeatIntervalInMinutes(int heartbeatIntervalInMinutes) {
        this.heartbeatInterval = (int) TimeUnit.MINUTES.toSeconds(heartbeatIntervalInMinutes);
    }

    public int getHeartbeatIntervalInMinutes() {
        return (int) TimeUnit.SECONDS.toMinutes(heartbeatInterval);
    }
}
