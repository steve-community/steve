/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import jooq.steve.db.enums.TransactionStopEventActor;
import jooq.steve.db.tables.records.TransactionRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 17.02.2026
 */
@Service
@RequiredArgsConstructor
public class CentralSystemService16_ServiceValidator {

    private final Clock clock;
    private final Duration operationalDeltaForNow;

    @Autowired
    public CentralSystemService16_ServiceValidator(Clock clock) {
        this(clock, Duration.ofMinutes(5));
    }

    public SteveException validateStop(TransactionRecord thisTx, UpdateTransactionParams params) {
        if (params.getEventActor() != TransactionStopEventActor.station) {
            // we want to validate messages coming from station only.
            // 'manual' actions are coming internally from us.
            return null;
        }

        if (thisTx == null) {
            return new SteveException("The transaction is not found in database");
        }

        boolean wasStopped = thisTx.getStopEventActor() == TransactionStopEventActor.station
            && thisTx.getStopValue() != null
            && thisTx.getStopTimestamp() != null;

        if (wasStopped) {
            return new SteveException("The transaction was already stopped by the station");
        }

        if (thisTx.getStartTimestamp().isAfter(params.getStopTimestamp())) {
            return new SteveException("start.timestamp is after stop.timestamp");
        }

        if (params.getStopTimestamp().getMillis() > clock.instant().plus(operationalDeltaForNow).toEpochMilli()) {
            return new SteveException("stop.timestamp is in the future");
        }

        if (Integer.parseInt(thisTx.getStartValue()) > Integer.parseInt(params.getStopMeterValue())) {
            return new SteveException("meterStart is greater than meterStop");
        }

        return null;
    }

}
