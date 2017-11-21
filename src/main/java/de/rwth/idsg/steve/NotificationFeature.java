package de.rwth.idsg.steve;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 22.01.2016
 */
@RequiredArgsConstructor
public enum NotificationFeature {

    // Ocpp related
    //
    OcppStationBooted(" a charging station sends a boot notification (Note: This activates notifications about failed connection attempts for unregistered JSON stations, as well)"),
    OcppStationStatusFailure(" a connector gets faulted"),
    OcppStationWebSocketConnected(" a JSON charging station connects"),
    OcppStationWebSocketDisconnected(" a JSON charging station disconnects");

    @Getter
    private final String text;

    public static NotificationFeature fromName(String v) {
        for (NotificationFeature c: NotificationFeature.values()) {
            if (c.name().equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
