package de.rwth.idsg.steve.web.dto.ocpp16;

import lombok.Getter;

public enum ChargingRateUnitEnum
{
    W("Watts", "power"),
    A("Amperes", "current");

    private final String value;
    @Getter private final String text;

    private ChargingRateUnitEnum(String value, String comment)
    {
        this.value = value;
        this.text = String.format("%s (%s)", value, comment);
    }

    public String value()
    {
        return value;
    }

    public static ChargingRateUnitEnum fromValue(String v)
    {
        for (ChargingRateUnitEnum c: ChargingRateUnitEnum.values())
        {
            if (c.value.equals(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}