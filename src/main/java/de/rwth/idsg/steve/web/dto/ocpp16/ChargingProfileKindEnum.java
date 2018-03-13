package de.rwth.idsg.steve.web.dto.ocpp16;

import lombok.Getter;

public enum ChargingProfileKindEnum
{
    ABSOLUTE("Absolute"),
    RECURRING("Recurring"),
    RELATIVE("Relative");

    private final String value;
    @Getter private final String text;

    private ChargingProfileKindEnum(String value)
    {
        this.value = value;
        this.text = String.format("%s", value);
    }

    public String value()
    {
        return value;
    }

    public static ChargingProfileKindEnum fromValue(String v)
    {
        for (ChargingProfileKindEnum c: ChargingProfileKindEnum.values())
        {
            if (c.value.equals(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
