package de.rwth.idsg.steve;

/**
 * This class holds the values that are relevant to OCPP
 * and used by the application.
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *  
 */
public final class OcppConstants {
    private OcppConstants() {}

    public static final String V12 = "1.2";
    public static final String V15 = "1.5";

    // Heartbeat interval in seconds
	private static int heartbeatInterval = 14400;
	// Determines how many hours the idtag should be stored in the local whitelist of a chargebox
	private static int hoursToExpire = 1;

    private static final Object HEARTBEAT_LOCK = new Object();
    private static final Object HOURS_LOCK = new Object();

    public static int getHeartbeatInterval() {
        synchronized (HEARTBEAT_LOCK) {
            return heartbeatInterval;
        }
    }

    public static void setHeartbeatInterval(int interval) {
        synchronized (HEARTBEAT_LOCK) {
            heartbeatInterval = interval;
        }
    }

    public static int getHoursToExpire() {
        synchronized (HOURS_LOCK) {
            return hoursToExpire;
        }
    }

    public static void setHoursToExpire(int expire) {
        synchronized (HOURS_LOCK) {
            hoursToExpire = expire;
        }
    }
}