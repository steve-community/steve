package de.rwth.idsg.steve.web.dto.ocpp;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 20.03.2018
 */
public enum TriggerMessageEnum {

    BootNotification("BootNotification"),
    DiagnosticsStatusNotification("DiagnosticsStatusNotification"),
    FirmwareStatusNotification("FirmwareStatusNotification"),
    Heartbeat("Heartbeat"),
    MeterValues("MeterValues"),
    StatusNotification("StatusNotification");

    private final String value;

    TriggerMessageEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static TriggerMessageEnum fromValue(String v) {
        for (TriggerMessageEnum c : TriggerMessageEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}

