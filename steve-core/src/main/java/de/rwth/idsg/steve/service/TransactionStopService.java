/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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

import com.google.common.collect.Ordering;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.repository.dto.TransactionStopEventActor;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 09.12.2018
 */
@Service
@RequiredArgsConstructor
public class TransactionStopService {

    private final TransactionRepository transactionRepository;
    private final OcppServerRepository ocppServerRepository;

    public void stop(List<Integer> transactionPkList) {
        transactionPkList.stream().sorted(Ordering.natural()).forEach(this::stop);
    }

    public void stop(Integer transactionPk) {
        var thisTxDetails = transactionRepository.getDetails(transactionPk).orElse(null);

        if (thisTxDetails == null) {
            return;
        }

        var thisTx = thisTxDetails.getTransaction();

        // early exit, if transaction is already stopped
        if (thisTx.getStopValue() != null && thisTx.getStopTimestamp() != null) {
            return;
        }

        var values = thisTxDetails.findNeededValues();

        ocppServerRepository.updateTransaction(UpdateTransactionParams.builder()
                .transactionId(thisTx.getId())
                .chargeBoxId(thisTx.getChargeBoxId())
                .stopMeterValue(values.getStopValue())
                .stopTimestamp(values.getStopTimestamp())
                .eventActor(TransactionStopEventActor.MANUAL)
                .eventTimestamp(Instant.now())
                .build());
    }
}
