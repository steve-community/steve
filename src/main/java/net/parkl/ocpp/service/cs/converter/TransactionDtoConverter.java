/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
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
package net.parkl.ocpp.service.cs.converter;

import de.rwth.idsg.steve.utils.DateTimeUtils;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.entities.OcppTag;
import net.parkl.ocpp.entities.Transaction;
import org.joda.time.DateTime;

public class TransactionDtoConverter {

    public static de.rwth.idsg.steve.repository.dto.Transaction toTransactionDto(Transaction t, OcppChargeBox box, OcppTag tag) {
        return de.rwth.idsg.steve.repository.dto.Transaction.builder()
                .id(t.getTransactionPk())
                .chargeBoxId(t.getConnector().getChargeBoxId())
                .connectorId(t.getConnector().getConnectorId())
                .ocppIdTag(t.getOcppTag())
                .startTimestamp(t.getStartTimestamp() != null ? new DateTime(t.getStartTimestamp()) : null)
                .startTimestampFormatted(DateTimeUtils.humanize(t.getStartTimestamp() != null ? new DateTime(t.getStartTimestamp()) : null))
                .startValue(t.getStartValue())
                .stopTimestamp(t.getStopTimestamp() != null ? new DateTime(t.getStopTimestamp()) : null)
                .stopTimestampFormatted(DateTimeUtils.humanize(t.getStopTimestamp() != null ? new DateTime(t.getStopTimestamp()) : null))
                .stopValue(t.getStopValue())
                .chargeBoxPk(box.getChargeBoxPk())
                .ocppTagPk(tag.getOcppTagPk())
                .stopEventActor(t.getStopEventActor())
                .build();
    }
}
