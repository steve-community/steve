package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.09.2015
 */
@Getter
@Setter
public class OcppTagQueryForm {

    private String idTag;
    private String parentIdTag;

    private BooleanType expired;
    private BooleanType inTransaction;
    private BooleanType blocked;

    /**
     * Init with sensible default values
     */
    public OcppTagQueryForm() {
        expired = BooleanType.FALSE;
        blocked = BooleanType.FALSE;
        inTransaction = BooleanType.ALL;
    }

    public boolean isIdTagSet() {
        return idTag != null;
    }

    public boolean isParentIdTagSet() {
        return parentIdTag != null;
    }

    @RequiredArgsConstructor
    public enum BooleanType {
        ALL("All", null),
        TRUE("True", true),
        FALSE("False", false);

        @Getter private final String value;
        private final Boolean boolValue;

        public boolean getBoolValue() {
            if (this.boolValue == null) {
                throw new UnsupportedOperationException("This enum does not have any meaningful bool value set.");
            }
            return this.boolValue;
        }

        public static BooleanType fromValue(String v) {
            for (BooleanType c: BooleanType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }

}
