package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.ocpp.OcppVersion;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_12;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_15;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_16;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyReadWriteEnum.R;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyReadWriteEnum.RW;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
 */
public enum ConfigurationKeyEnum {
    // From Ocpp 1.2
    HeartBeatInterval("HeartBeatInterval", "in seconds", RW, newHashSet(V_12, V_15)),
    ConnectionTimeOut("ConnectionTimeOut", "in seconds", RW, newHashSet(V_12, V_15, V_16)),
    ProximityContactRetries("ProximityContactRetries", "in times", RW, newHashSet(V_12, V_15, V_16)),
    ProximityLockRetries("ProximityLockRetries", "in times", RW, newHashSet(V_12, V_15, V_16)),
    ResetRetries("ResetRetries", "in times", RW, newHashSet(V_12, V_15, V_16)),
    BlinkRepeat("BlinkRepeat", "in times", RW, newHashSet(V_12, V_15, V_16)),
    LightIntensity("LightIntensity", "in %", RW, newHashSet(V_12, V_15, V_16)),
    ChargePointId("ChargePointId", "string", RW, newHashSet(V_12, V_15, V_16)),
    MeterValueSampleInterval("MeterValueSampleInterval", "in seconds", RW, newHashSet(V_12, V_15, V_16)),

    // New in Ocpp 1.5
    ClockAlignedDataInterval("ClockAlignedDataInterval", "in seconds", RW, newHashSet(V_15, V_16)),
    MeterValuesSampledData("MeterValuesSampledData", "comma seperated list", RW, newHashSet(V_15, V_16)),
    MeterValuesAlignedData("MeterValuesAlignedData", "comma seperated list", RW, newHashSet(V_15, V_16)),
    StopTxnSampledData("StopTxnSampledData", "comma seperated list", RW, newHashSet(V_15, V_16)),
    StopTxnAlignedData("StopTxnAlignedData", "comma seperated list", RW, newHashSet(V_15, V_16)),

    // New in Ocpp 1.6
    HeartbeatInterval("HeartbeatInterval", "in seconds", RW, newHashSet(V_16)),
    AllowOfflineTxForUnknownId("AllowOfflineTxForUnknownId", "boolean", RW, newHashSet(V_16)),
    AuthorizationCacheEnabled("AuthorizationCacheEnabled", "boolean", RW, newHashSet(V_16)),
    // AuthorizeRemoteTxRequests Read or Read-Write is up to Charge Point implementation so set to RW for now
    AuthorizeRemoteTxRequests("AuthorizeRemoteTxRequests", "boolean", RW, newHashSet(V_16)),
    ConnectorPhaseRotation("ConnectorPhaseRotation", "comma seperated list", RW, newHashSet(V_16)),
    ConnectorPhaseRotationMaxLength("ConnectorPhaseRotationMaxLength", "integer", R, newHashSet(V_16)),
    GetConfigurationMaxKeys("GetConfigurationMaxKeys", "integer", R, newHashSet(V_16)),
    LocalAuthorizeOffline("LocalAuthorizeOffline", "boolean", RW, newHashSet(V_16)),
    LocalPreAuthorize("LocalPreAuthorize", "boolean", RW, newHashSet(V_16)),
    MaxEnergyOnInvalidId("MaxEnergyOnInvalidId", "in Wh", RW, newHashSet(V_16)),
    MeterValuesAlignedDataMaxLength("MeterValuesAlignedDataMaxLength", "integer", R, newHashSet(V_16)),
    MeterValuesSampledDataMaxLength("MeterValuesSampledDataMaxLength", "integer", R, newHashSet(V_16)),
    MinimumStatusDuration("MinimumStatusDuration", "in seconds", RW, newHashSet(V_16)),
    NumberOfConnectors("NumberOfConnectors", "integer", R, newHashSet(V_16)),
    StopTransactionOnEVSideDisconnect("StopTransactionOnEVSideDisconnect", "boolean", RW, newHashSet(V_16)),
    StopTransactionOnInvalidId("StopTransactionOnInvalidId", "boolean", RW, newHashSet(V_16)),
    StopTxnAlignedDataMaxLength("StopTxnAlignedDataMaxLength", "integer", R, newHashSet(V_16)),
    StopTxnSampledDataMaxLength("StopTxnSampledDataMaxLength", "integer", R, newHashSet(V_16)),
    SupportedFeatureProfiles("SupportedFeatureProfiles", "comma seperated list", R, newHashSet(V_16)),
    SupportedFeatureProfilesMaxLength("SupportedFeatureProfilesMaxLength", "integer", R, newHashSet(V_16)),
    TransactionMessageAttempts("TransactionMessageAttempts", "in times", RW, newHashSet(V_16)),
    TransactionMessageRetryInterval("TransactionMessageRetryInterval", "in seconds", RW, newHashSet(V_16)),
    UnlockConnectorOnEVSideDisconnect("UnlockConnectorOnEVSideDisconnect", "boolean", RW, newHashSet(V_16)),
    WebSocketPingInterval("WebSocketPingInterval", "in seconds", RW, newHashSet(V_16)),
    LocalAuthListEnabled("LocalAuthListEnabled", "boolean", RW, newHashSet(V_16)),
    LocalAuthListMaxLength("LocalAuthListMaxLength", "integer", R, newHashSet(V_16)),
    SendLocalListMaxLength("SendLocalListMaxLength", "integer", R, newHashSet(V_16)),
    ReserveConnectorZeroSupported("ReserveConnectorZeroSupported", "boolean", R, newHashSet(V_16)),
    ChargeProfileMaxStackLevel("ChargeProfileMaxStackLevel", "integer", R, newHashSet(V_16)),
    ChargingScheduleAllowedChargingRateUnit("ChargingScheduleAllowedChargingRateUnit", "comma seperated list", R, newHashSet(V_16)),
    ChargingScheduleMaxPeriods("ChargingScheduleMaxPeriods", "integer", R, newHashSet(V_16)),
    ConnectorSwitch3to1PhaseSupported("ConnectorSwitch3to1PhaseSupported", "boolean", R, newHashSet(V_16)),
    MaxChargingProfilesInstalled("MaxChargingProfilesInstalled", "integer", R, newHashSet(V_16));

    private final String value;
    private final String text;
    private final ConfigurationKeyReadWriteEnum rw;
    private final Set<OcppVersion> versions;

    public static final Map<String, String> OCPP_12_MAP = asMap(OcppVersion.V_12, RW);
    public static final Map<String, String> OCPP_15_MAP = asMap(OcppVersion.V_15, RW);

    // In OCPP 1.6 some Configuration Keys are Read-Only
    public static final Map<String, String> OCPP_16_MAP_R = asMap(OcppVersion.V_16, R);
    public static final Map<String, String> OCPP_16_MAP_RW = asMap(OcppVersion.V_16, RW);

    ConfigurationKeyEnum(String value, String comment, ConfigurationKeyReadWriteEnum rw, Set<OcppVersion> versions) {
        this.value = value;
        this.text = String.format("%s (%s)", value, comment);
        this.rw = rw;
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

    private static Map<String, String> asMap(OcppVersion version, ConfigurationKeyReadWriteEnum rw) {
        Map<String, String> map = new LinkedHashMap<>();
        for (ConfigurationKeyEnum c : ConfigurationKeyEnum.values()) {
            if (c.versions.contains(version) && c.rw == rw) {
                map.put(c.value, c.text);
            } else if (c.versions.contains(version) && rw == ConfigurationKeyReadWriteEnum.R) {
                map.put(c.value, c.text);
            }
        }
        return map;
    }
}
