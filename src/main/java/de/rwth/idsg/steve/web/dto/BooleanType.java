package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BooleanType {
    ALL("All", null),
    TRUE("True", true),
    FALSE("False", false);

    @Getter
    private final String value;
    private final Boolean boolValue;

    public boolean getBoolValue() {
        if (this.boolValue == null) {
            throw new UnsupportedOperationException("This enum does not have any meaningful bool value set.");
        }
        return this.boolValue;
    }

    public static BooleanType fromValue(String v) {
        for (BooleanType c: BooleanType.values()) {
            if (c.getValue().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
