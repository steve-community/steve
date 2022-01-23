/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.google.common.collect.Sets.newHashSet;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_12;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_15;
import static de.rwth.idsg.steve.ocpp.OcppVersion.V_16;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyReadWriteEnum.R;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyReadWriteEnum.RW;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 02.01.2015
 */
public enum ConfigurationKeyEnum {

    // -------------------------------------------------------------------------
    // From OCPP 1.2
    // -------------------------------------------------------------------------

    HeartBeatInterval("in seconds", RW, newHashSet(V_12, V_15)),
    ConnectionTimeOut("in seconds", RW, newHashSet(V_12, V_15, V_16)),
    ProximityContactRetries("in times", RW, newHashSet(V_12, V_15)),
    ProximityLockRetries("in times", RW, newHashSet(V_12, V_15)),
    ResetRetries("in times", RW, newHashSet(V_12, V_15, V_16)),
    BlinkRepeat("in times", RW, newHashSet(V_12, V_15, V_16)),
    LightIntensity("in %", RW, newHashSet(V_12, V_15, V_16)),
    ChargePointId("string", RW, newHashSet(V_12, V_15)),
    MeterValueSampleInterval("in seconds", RW, newHashSet(V_12, V_15, V_16)),

    // -------------------------------------------------------------------------
    // New in OCPP 1.5
    // -------------------------------------------------------------------------

    ClockAlignedDataInterval("in seconds", RW, newHashSet(V_15, V_16)),
    MeterValuesSampledData("comma separated list", RW, newHashSet(V_15, V_16)),
    MeterValuesAlignedData("comma separated list", RW, newHashSet(V_15, V_16)),
    StopTxnSampledData("comma separated list", RW, newHashSet(V_15, V_16)),
    StopTxnAlignedData("comma separated list", RW, newHashSet(V_15, V_16)),

    // -------------------------------------------------------------------------
    // New in OCPP 1.6
    // -------------------------------------------------------------------------

    HeartbeatInterval("in seconds", RW, newHashSet(V_16)),
    AllowOfflineTxForUnknownId("boolean", RW, newHashSet(V_16)),
    AuthorizationCacheEnabled("boolean", RW, newHashSet(V_16)),
    // AuthorizeRemoteTxRequests Read or Read-Write is up to Charge Point implementation so set to RW for now
    AuthorizeRemoteTxRequests("boolean", RW, newHashSet(V_16)),
    ConnectorPhaseRotation("comma separated list", RW, newHashSet(V_16)),
    ConnectorPhaseRotationMaxLength("integer", R, newHashSet(V_16)),
    GetConfigurationMaxKeys("integer", R, newHashSet(V_16)),
    LocalAuthorizeOffline("boolean", RW, newHashSet(V_16)),
    LocalPreAuthorize("boolean", RW, newHashSet(V_16)),
    MaxEnergyOnInvalidId("in Wh", RW, newHashSet(V_16)),
    MeterValuesAlignedDataMaxLength("integer", R, newHashSet(V_16)),
    MeterValuesSampledDataMaxLength("integer", R, newHashSet(V_16)),
    MinimumStatusDuration("in seconds", RW, newHashSet(V_16)),
    NumberOfConnectors("integer", R, newHashSet(V_16)),
    StopTransactionOnEVSideDisconnect("boolean", RW, newHashSet(V_16)),
    StopTransactionOnInvalidId("boolean", RW, newHashSet(V_16)),
    StopTxnAlignedDataMaxLength("integer", R, newHashSet(V_16)),
    StopTxnSampledDataMaxLength("integer", R, newHashSet(V_16)),
    SupportedFeatureProfiles("comma separated list", R, newHashSet(V_16)),
    SupportedFeatureProfilesMaxLength("integer", R, newHashSet(V_16)),
    TransactionMessageAttempts("in times", RW, newHashSet(V_16)),
    TransactionMessageRetryInterval("in seconds", RW, newHashSet(V_16)),
    UnlockConnectorOnEVSideDisconnect("boolean", RW, newHashSet(V_16)),
    WebSocketPingInterval("in seconds", RW, newHashSet(V_16)),
    LocalAuthListEnabled("boolean", RW, newHashSet(V_16)),
    LocalAuthListMaxLength("integer", R, newHashSet(V_16)),
    SendLocalListMaxLength("integer", R, newHashSet(V_16)),
    ReserveConnectorZeroSupported("boolean", R, newHashSet(V_16)),
    SupportedFileTransferProtocols("comma separated list", R, newHashSet(V_16)),

    // -------------------------------------------------------------------------
    // New in OCPP 1.6 for Smart Charging profile
    // -------------------------------------------------------------------------

    ChargeProfileMaxStackLevel("integer", R, newHashSet(V_16)),
    ChargingScheduleAllowedChargingRateUnit("comma separated list", R, newHashSet(V_16)),
    ChargingScheduleMaxPeriods("integer", R, newHashSet(V_16)),
    ConnectorSwitch3to1PhaseSupported("boolean", R, newHashSet(V_16)),
    MaxChargingProfilesInstalled("integer", R, newHashSet(V_16));

    private final String value;
    private final String text;
    private final ConfigurationKeyReadWriteEnum rw;
    private final Set<OcppVersion> versions;

    public static final Map<String, String> OCPP_12_MAP = asMap(OcppVersion.V_12, RW);
    public static final Map<String, String> OCPP_15_MAP = asMap(OcppVersion.V_15, RW);

    // In OCPP 1.6 some Configuration Keys are Read-Only
    public static final Map<String, String> OCPP_16_MAP_R = asMap(OcppVersion.V_16, R);
    public static final Map<String, String> OCPP_16_MAP_RW = asMap(OcppVersion.V_16, RW);


    ConfigurationKeyEnum(String valueType, ConfigurationKeyReadWriteEnum rw, Set<OcppVersion> versions) {
        this.value = this.name();
        this.text = String.format("%s (%s)", value, valueType);
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
        Map<String, String> map = new TreeMap<>();
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
