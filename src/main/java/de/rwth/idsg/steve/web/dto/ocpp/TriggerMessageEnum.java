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

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
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

