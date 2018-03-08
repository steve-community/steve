package de.rwth.idsg.steve.web.dto.common;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 08.03.2018
 */
public enum ResetType {
    HARD("Hard"),
    SOFT("Soft");

    private final String value;

    ResetType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResetType fromValue(String v) {
        for (ResetType c : ResetType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
