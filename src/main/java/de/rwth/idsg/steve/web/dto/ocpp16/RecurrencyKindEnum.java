package de.rwth.idsg.steve.web.dto.ocpp16;

import lombok.Getter;

public enum RecurrencyKindEnum
{
    DAILY("Daily"),
    WEEKLY("Weekly");

    private final String value;
    @Getter private final String text;

    private RecurrencyKindEnum(String value)
    {
        this.value = value;
        this.text = String.format("%s", value);
    }

    public String value()
    {
        return value;
    }

    public static RecurrencyKindEnum fromValue(String v)
    {
        for (RecurrencyKindEnum c: RecurrencyKindEnum.values())
        {
            if (c.value.equals(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
