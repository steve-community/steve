package de.rwth.idsg.steve.web.dto.ocpp;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.11.2018
 */
public enum ClearChargingProfileFilterType {

    ChargingProfileId("ChargingProfileId"),
    OtherParameters("OtherParameters");

    private final String value;

    ClearChargingProfileFilterType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClearChargingProfileFilterType fromValue(String v) {
        for (ClearChargingProfileFilterType c : ClearChargingProfileFilterType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
