package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.ocpp.OcppVersion;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_12;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_15;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_16;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
 */
public enum ConfigurationKeyEnum {
    // From Ocpp 1.2
    HeartBeatInterval("HeartBeatInterval", "in seconds", newHashSet(V_12, V_15, V_16)),
    ConnectionTimeOut("ConnectionTimeOut", "in seconds", newHashSet(V_12, V_15, V_16)),
    ProximityContactRetries("ProximityContactRetries", "in times", newHashSet(V_12, V_15, V_16)),
    ProximityLockRetries("ProximityLockRetries", "in times", newHashSet(V_12, V_15, V_16)),
    ResetRetries("ResetRetries", "in times", newHashSet(V_12, V_15, V_16)),
    BlinkRepeat("BlinkRepeat", "in times", newHashSet(V_12, V_15, V_16)),
    LightIntensity("LightIntensity", "in %", newHashSet(V_12, V_15, V_16)),
    ChargePointId("ChargePointId", "string", newHashSet(V_12, V_15, V_16)),
    MeterValueSampleInterval("MeterValueSampleInterval", "in seconds", newHashSet(V_12, V_15, V_16)),

    // New in Ocpp 1.5
    ClockAlignedDataInterval("ClockAlignedDataInterval", "in seconds", newHashSet(V_15, V_16)),
    MeterValuesSampledData("MeterValuesSampledData", "comma seperated list", newHashSet(V_15, V_16)),
    MeterValuesAlignedData("MeterValuesAlignedData", "comma seperated list", newHashSet(V_15, V_16)),
    StopTxnSampledData("StopTxnSampledData", "comma seperated list", newHashSet(V_15, V_16)),
    StopTxnAlignedData("StopTxnAlignedData", "comma seperated list", newHashSet(V_15, V_16)),

    // New in Ocpp 1.6
    AllowOfflineTxForUnknownId("AllowOfflineTxForUnknownId", "boolean", newHashSet(V_16)),
    AuthorizationCacheEnabled("AuthorizationCacheEnabled", "boolean", newHashSet(V_16)),
    AuthorizeRemoteTxRequests("AuthorizeRemoteTxRequests", "boolean", newHashSet(V_16)),
    ConnectorPhaseRotation("ConnectorPhaseRotation", "comma seperated list", newHashSet(V_16)),
    ConnectorPhaseRotationMaxLength("ConnectorPhaseRotationMaxLength", "integer", newHashSet(V_16)),
    GetConfigurationMaxKeys("GetConfigurationMaxKeys", "integer", newHashSet(V_16)),
    LocalAuthorizeOffline("LocalAuthorizeOffline", "boolean", newHashSet(V_16)),
    LocalPreAuthorize("LocalPreAuthorize", "boolean", newHashSet(V_16)),
    MaxEnergyOnInvalidId("MaxEnergyOnInvalidId", "in Wh", newHashSet(V_16)),
    MeterValuesAlignedDataMaxLength("MeterValuesAlignedDataMaxLength", "integer", newHashSet(V_16)),
    MeterValuesSampledDataMaxLength("MeterValuesSampledDataMaxLength", "integer", newHashSet(V_16)),
    MinimumStatusDuration("MinimumStatusDuration", "in seconds", newHashSet(V_16)),
    NumberOfConnectors("NumberOfConnectors", "integer", newHashSet(V_16)),
    StopTransactionOnEVSideDisconnect("StopTransactionOnEVSideDisconnect", "boolean", newHashSet(V_16)),
    StopTransactionOnInvalidId("StopTransactionOnInvalidId", "boolean", newHashSet(V_16)),
    StopTxnAlignedDataMaxLength("StopTxnAlignedDataMaxLength", "integer", newHashSet(V_16)),
    StopTxnSampledDataMaxLength("StopTxnSampledDataMaxLength", "integer", newHashSet(V_16)),
    SupportedFeatureProfiles("SupportedFeatureProfiles", "comma seperated list", newHashSet(V_16)),
    SupportedFeatureProfilesMaxLength("SupportedFeatureProfilesMaxLength", "integer", newHashSet(V_16)),
    TransactionMessageAttempts("TransactionMessageAttempts", "in times", newHashSet(V_16)),
    TransactionMessageRetryInterval("TransactionMessageRetryInterval", "in seconds", newHashSet(V_16)),
    UnlockConnectorOnEVSideDisconnect("UnlockConnectorOnEVSideDisconnect", "boolean", newHashSet(V_16)),
    WebSocketPingInterval("WebSocketPingInterval", "in seconds", newHashSet(V_16)),
    LocalAuthListEnabled("LocalAuthListEnabled", "boolean", newHashSet(V_16)),
    LocalAuthListMaxLength("LocalAuthListMaxLength", "integer", newHashSet(V_16)),
    SendLocalListMaxLength("SendLocalListMaxLength", "integer", newHashSet(V_16)),
    ReserveConnectorZeroSupported("ReserveConnectorZeroSupported", "boolean", newHashSet(V_16)),
    ChargeProfileMaxStackLevel("ChargeProfileMaxStackLevel", "integer", newHashSet(V_16)),
    ChargingScheduleAllowedChargingRateUnit("ChargingScheduleAllowedChargingRateUnit", "comma seperated list", newHashSet(V_16)),
    ChargingScheduleMaxPeriods("ChargingScheduleMaxPeriods", "integer", newHashSet(V_16)),
    ConnectorSwitch3to1PhaseSupported("ConnectorSwitch3to1PhaseSupported", "boolean", newHashSet(V_16)),
    MaxChargingProfilesInstalled("MaxChargingProfilesInstalled", "integer", newHashSet(V_16));

    private final String value;
    private final String text;
    private final Set<OcppVersion> versions;

    public static final Map<String, String> OCPP_12_MAP = asMap(OcppVersion.V_12);
    public static final Map<String, String> OCPP_15_MAP = asMap(OcppVersion.V_15);
    public static final Map<String, String> OCPP_16_MAP = asMap(OcppVersion.V_16);

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
