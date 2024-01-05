/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2024 SteVe Community Team
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
package de.rwth.idsg.steve.repository.dto;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.11.2015
 */
@Getter
@Builder
public final class UpdateChargeboxParams {
    private final OcppProtocol ocppProtocol;
    private final DateTime heartbeatTimestamp;
    private final String vendor, model, pointSerial, boxSerial, fwVersion,
            iccid, imsi, meterType, meterSerial, chargeBoxId;
}
