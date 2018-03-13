/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.web.dto.ocpp16;

import lombok.Getter;

/**
 *
 * @author david
 */
public enum ConfigurationKeyEnum 
{
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

    // From Ocpp 1.5
    ClockAlignedDataInterval("ClockAlignedDataInterval", "in seconds"),
    MeterValuesSampledData("MeterValuesSampledData", "comma seperated list"),
    MeterValuesAlignedData("MeterValuesAlignedData", "comma seperated list"),
    StopTxnSampledData("StopTxnSampledData", "comma seperated list"),
    StopTxnAlignedData("StopTxnAlignedData", "comma seperated list"),
    
    // New in Ocpp 1.6
    AllowOfflineTxForUnknownId("AllowOfflineTxForUnknownId", "boolean"),
    AuthorizationCacheEnabled("AuthorizationCacheEnabled", "boolean"),
    AuthorizeRemoteTxRequests("AuthorizeRemoteTxRequests", "boolean"),
    ConnectorPhaseRotation("ConnectorPhaseRotation", "comma seperated list"),
    ConnectorPhaseRotationMaxLength("ConnectorPhaseRotationMaxLength", "integer"),
    GetConfigurationMaxKeys("GetConfigurationMaxKeys", "integer"),
    LocalAuthorizeOffline("LocalAuthorizeOffline", "boolean"),
    LocalPreAuthorize("LocalPreAuthorize", "boolean"),
    MaxEnergyOnInvalidId("MaxEnergyOnInvalidId", "in Wh"),
    MeterValuesAlignedDataMaxLength("MeterValuesAlignedDataMaxLength", "integer"),
    MeterValuesSampledDataMaxLength("MeterValuesSampledDataMaxLength", "integer"),
    MinimumStatusDuration("MinimumStatusDuration", "in seconds"),
    NumberOfConnectors("NumberOfConnectors", "integer"),
    StopTransactionOnEVSideDisconnect("StopTransactionOnEVSideDisconnect", "boolean"),
    StopTransactionOnInvalidId("StopTransactionOnInvalidId", "boolean"),
    StopTxnAlignedDataMaxLength("StopTxnAlignedDataMaxLength", "integer"),
    StopTxnSampledDataMaxLength("StopTxnSampledDataMaxLength", "integer"),
    SupportedFeatureProfiles("SupportedFeatureProfiles", "comma seperated list"),
    SupportedFeatureProfilesMaxLength("SupportedFeatureProfilesMaxLength", "integer"),
    TransactionMessageAttempts("TransactionMessageAttempts", "in times"),
    TransactionMessageRetryInterval("TransactionMessageRetryInterval", "in seconds"),
    UnlockConnectorOnEVSideDisconnect("UnlockConnectorOnEVSideDisconnect", "boolean"),
    WebSocketPingInterval("WebSocketPingInterval", "in seconds"),
    LocalAuthListEnabled("LocalAuthListEnabled", "boolean"),
    LocalAuthListMaxLength("LocalAuthListMaxLength", "integer"),
    SendLocalListMaxLength("SendLocalListMaxLength", "integer"),
    ReserveConnectorZeroSupported("ReserveConnectorZeroSupported", "boolean"),
    ChargeProfileMaxStackLevel("ChargeProfileMaxStackLevel", "integer"),
    ChargingScheduleAllowedChargingRateUnit("ChargingScheduleAllowedChargingRateUnit", "comma seperated list"),
    ChargingScheduleMaxPeriods("ChargingScheduleMaxPeriods", "integer"),
    ConnectorSwitch3to1PhaseSupported("ConnectorSwitch3to1PhaseSupported", "boolean"),
    MaxChargingProfilesInstalled("MaxChargingProfilesInstalled", "integer");
    
    
    
    
    private final String value;
    @Getter private final String text;

    private ConfigurationKeyEnum(String value, String comment) 
    {
        this.value = value;
        this.text = String.format("%s (%s)", value, comment);
    }
    
    public String value() 
    {
        return value;
    }
    
    public static ConfigurationKeyEnum fromValue(String v)
    {
        for (ConfigurationKeyEnum c: ConfigurationKeyEnum.values()) 
        {
            if (c.value.equals(v)) 
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}

