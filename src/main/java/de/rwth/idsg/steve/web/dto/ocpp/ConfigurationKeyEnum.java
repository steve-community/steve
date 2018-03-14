package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.ocpp.OcppVersion;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_12;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_15;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
 */
public enum ConfigurationKeyEnum {
    // From Ocpp 1.2
    HeartBeatInterval("HeartBeatInterval", "in seconds", newHashSet(V_12, V_15)),
    ConnectionTimeOut("ConnectionTimeOut", "in seconds", newHashSet(V_12, V_15)),
    ProximityContactRetries("ProximityContactRetries", "in times", newHashSet(V_12, V_15)),
    ProximityLockRetries("ProximityLockRetries", "in times", newHashSet(V_12, V_15)),
    ResetRetries("ResetRetries", "in times", newHashSet(V_12, V_15)),
    BlinkRepeat("BlinkRepeat", "in times", newHashSet(V_12, V_15)),
    LightIntensity("LightIntensity", "in %", newHashSet(V_12, V_15)),
    ChargePointId("ChargePointId", "string", newHashSet(V_12, V_15)),
    MeterValueSampleInterval("MeterValueSampleInterval", "in seconds", newHashSet(V_12, V_15)),

    // New in Ocpp 1.5
    ClockAlignedDataInterval("ClockAlignedDataInterval", "in seconds", newHashSet(V_15)),
    MeterValuesSampledData("MeterValuesSampledData", "comma seperated list", newHashSet(V_15)),
    MeterValuesAlignedData("MeterValuesAlignedData", "comma seperated list", newHashSet(V_15)),
    StopTxnSampledData("StopTxnSampledData", "comma seperated list", newHashSet(V_15)),
    StopTxnAlignedData("StopTxnAlignedData", "comma seperated list", newHashSet(V_15));

    private final String value;
    private final String text;
    private final Set<OcppVersion> versions;

    public static final Map<String, String> OCPP_12_MAP = asMap(OcppVersion.V_12);
    public static final Map<String, String> OCPP_15_MAP = asMap(OcppVersion.V_15);

    ConfigurationKeyEnum(String value, String comment, Set<OcppVersion> versions) {
        this.value = value;
        this.text = String.format("%s (%s)", value, comment);
        this.versions = versions;
    }

    public String value() {
        return value;
    }

    public static ConfigurationKeyEnum fromValue(String v) {
        for (ConfigurationKeyEnum c : ConfigurationKeyEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    private static Map<String, String> asMap(OcppVersion version) {
        Map<String, String> map = new LinkedHashMap<>();
        for (ConfigurationKeyEnum c : ConfigurationKeyEnum.values()) {
            if (c.versions.contains(version)) {
                map.put(c.value, c.text);
            }
        }
        return map;
    }
}
