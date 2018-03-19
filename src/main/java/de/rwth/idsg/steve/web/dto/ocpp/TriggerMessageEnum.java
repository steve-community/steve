package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Getter;
/**
 *
 * @author david
 */
public enum TriggerMessageEnum
{
    BOOT_NOTIFICATION("BootNotification"),
    DIAGNOSTICS_STATUS_NOTIFICATION("DiagnosticsStatusNotification"),
    FIRMWARE_STATUS_NOTIFICATION("FirmwareStatusNotification"),
    HEARTBEAT("Heartbeat"),
    METER_VALUES("MeterValues"),
    STATUS_NOTIFICATION("StatusNotification");

    private final String value;
    @Getter private final String text;

    private TriggerMessageEnum(String value)
    {
        this.value = value;
        this.text = String.format("%s", value);
    }

    public String value()
    {
        return value;
    }

    public static TriggerMessageEnum fromValue(String v)
    {
        for (TriggerMessageEnum c: TriggerMessageEnum.values())
        {
            if (c.value.equals(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}

