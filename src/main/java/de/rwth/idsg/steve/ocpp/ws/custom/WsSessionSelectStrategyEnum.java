package de.rwth.idsg.steve.ocpp.ws.custom;

import lombok.Getter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.04.2015
 */
@Getter
public enum WsSessionSelectStrategyEnum {

    // Always use the last opened session/connection.
    ALWAYS_LAST,

    // The sessions/connections are chosen in a round robin fashion.
    // This would allow to distribute load to different connections.
    ROUND_ROBIN;

    public static WsSessionSelectStrategyEnum fromName(String v) {
        for (WsSessionSelectStrategyEnum s: WsSessionSelectStrategyEnum.values()) {
            if (s.name().equals(v)) {
                return s;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
