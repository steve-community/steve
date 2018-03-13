package de.rwth.idsg.steve.web.dto.ocpp16;

import lombok.Getter;

public enum ChargingProfilePurposeEnum
{
    CHARGE_POINT_MAX_PROFILE("ChargePointMaxProfile"),
    TX_DEFAULT_PROFILE("TxDefaultProfile"),
    TX_PROFILE("TxProfile");

    private final String value;
    @Getter private final String text;

    private ChargingProfilePurposeEnum(String value)
    {
        this.value = value;
        this.text = String.format("%s", value);
    }

    public String value()
    {
        return value;
    }

    public static ChargingProfilePurposeEnum fromValue(String v)
    {
        for (ChargingProfilePurposeEnum c: ChargingProfilePurposeEnum.values())
        {
            if (c.value.equals(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}