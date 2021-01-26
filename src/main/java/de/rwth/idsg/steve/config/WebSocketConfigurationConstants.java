package de.rwth.idsg.steve.config;

import java.util.concurrent.TimeUnit;

public class WebSocketConfigurationConstants {
    public static final long PING_INTERVAL = TimeUnit.MINUTES.toMinutes(15);
    public static final long IDLE_TIMEOUT = TimeUnit.HOURS.toMillis(2);
    public static final int MAX_MSG_SIZE = 8_388_608; // 8 MB for max message size

}
