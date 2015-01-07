package de.rwth.idsg.steve;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 01.12.2014
 */
@RequiredArgsConstructor
@Getter
public enum OcppVersion {
    V_12("1.2"),
    V_15("1.5");

    private final String value;
}
