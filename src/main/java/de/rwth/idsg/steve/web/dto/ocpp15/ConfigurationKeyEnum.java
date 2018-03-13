package de.rwth.idsg.steve.web.dto.ocpp15;

import lombok.Getter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
 */
public enum ConfigurationKeyEnum {
    // From Ocpp 1.2
    HeartBeatInterval("HeartBeatInterval", "in seconds"),
    ConnectionTimeOut("ConnectionTimeOut", "in seconds"),
    ProximityContactRetries("ProximityContactRetries", "in times"),
    ProximityLockRetries("ProximityLockRetries", "in times"),
    ResetRetries("ResetRetries", "in times"),
    BlinkRepeat("BlinkRepeat", "in times"),
    LightIntensity("LightIntensity", "in %"),
    ChargePointId("ChargePointId", "string"),
    MeterValueSampleInterval("MeterValueSampleInterval", "in seconds"),

    // New in Ocpp 1.5
    ClockAlignedDataInterval("ClockAlignedDataInterval", "in seconds"),
    MeterValuesSampledData("MeterValuesSampledData", "comma seperated list"),
    MeterValuesAlignedData("MeterValuesAlignedData", "comma seperated list"),
    StopTxnSampledData("StopTxnSampledData", "comma seperated list"),
    StopTxnAlignedData("StopTxnAlignedData", "comma seperated list");

    private final String value;
    @Getter private final String text;

    ConfigurationKeyEnum(String value, String comment) {
        this.value = value;
        this.text = String.format("%s (%s)", value, comment);
    }

    public String value() {
        return value;
    }

    public static ConfigurationKeyEnum fromValue(String v) {
        for (ConfigurationKeyEnum c: ConfigurationKeyEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
