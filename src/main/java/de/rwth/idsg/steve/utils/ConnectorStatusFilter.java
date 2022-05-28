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
package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 10.03.2016
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectorStatusFilter {

    public static List<ConnectorStatus> filterAndPreferZero(List<ConnectorStatus> initialList) {
        return processAndFilterList(initialList, Strategy.PreferZero);
    }

    public static List<ConnectorStatus> filterAndPreferOthersWithStatusOfZero(List<ConnectorStatus> initialList) {
        return processAndFilterList(initialList, Strategy.PreferOthersWithStatusOfZero);
    }

    /**
     * Problem description: For a station we have regular connectors ids > 0 and connector id 0 (represents whole
     * station). Connector id 0 should have upper hand while providing status information if its timestamp is
     * more recent than the others and mask status info of other connectors.
     *
     * Link: https://github.com/RWTH-i5-IDSG/steve/issues/16
     *
     * Logic walk-through:
     *
     * For each station
     * 0) group ConnectorStatus items around charge box ids, since input is a flat list (mixed bag with all charge
     *    box ids and connectors)
     * 1) find the latest ConnectorStatus of connector id 0 (actually there can only be at most one)
     * 2) find the latest ConnectorStatus within the connectors ids > 0
     * 3) compare the two, which timestamp is more recent: 0 or others?
     * 4) depending on the previous step, build the list to return
     */
    private static List<ConnectorStatus> processAndFilterList(List<ConnectorStatus> initialList, Strategy strategy) {
        return initialList.stream()
                          .collect(Collectors.groupingBy(ConnectorStatus::getChargeBoxId))
                          .values()
                          .stream()
                          .flatMap(val -> processForOneStation(val, strategy).stream())
                          .collect(Collectors.toList());
    }

    private static List<ConnectorStatus> processForOneStation(List<ConnectorStatus> statsList, Strategy strategy) {
        Map<Boolean, List<ConnectorStatus>> partition =
                statsList.stream()
                         .collect(Collectors.partitioningBy(s -> s.getConnectorId() == 0));

        List<ConnectorStatus> zero = partition.get(Boolean.TRUE);
        List<ConnectorStatus> nonZero = partition.get(Boolean.FALSE);

        Optional<ConnectorStatus> maxZero =
                zero.stream()
                    .max(Comparator.comparing(ConnectorStatus::getStatusTimestamp));

        Optional<ConnectorStatus> maxNonZero =
                nonZero.stream()
                       .max(Comparator.comparing(ConnectorStatus::getStatusTimestamp));

        // decide what to return
        //
        if (maxZero.isPresent()) {
            Predicate<ConnectorStatus> pr = o -> o.getStatusTimestamp().isAfter(maxZero.get().getStatusTimestamp());

            if (maxNonZero.filter(pr).isPresent()) {
                return nonZero;
            } else {
                // this is the special case we need to handle
                return strategy.process(zero, nonZero);
            }
        } else if (maxNonZero.isPresent()) {
            return nonZero;

        } else {
            return Collections.emptyList();
        }
    }

    // -------------------------------------------------------------------------
    // Strategy stuff
    // -------------------------------------------------------------------------

    private enum Strategy implements ZeroMoreRecentStrategy {

        PreferZero {
            @Override
            public List<ConnectorStatus> process(List<ConnectorStatus> zero, List<ConnectorStatus> nonZero) {
                return zero;
            }
        },

        /**
         * If connector 0 is more recent, copy the status of connector 0 to
         * other connector ids, and return ONLY others.
         */
        PreferOthersWithStatusOfZero {
            @Override
            public List<ConnectorStatus> process(List<ConnectorStatus> zero, List<ConnectorStatus> nonZero) {

                ConnectorStatus zeroStat = zero.get(0); // we are sure that there is only one

                return nonZero.stream()
                              .map(cs -> ConnectorStatus.builder()
                                                        .chargeBoxPk(cs.getChargeBoxPk())
                                                        .chargeBoxId(cs.getChargeBoxId())
                                                        .connectorId(cs.getConnectorId())
                                                        .timeStamp(zeroStat.getTimeStamp())
                                                        .statusTimestamp(zeroStat.getStatusTimestamp())
                                                        .status(zeroStat.getStatus())
                                                        .errorCode(zeroStat.getErrorCode())
                                                        .ocppProtocol(cs.getOcppProtocol())
                                                        .jsonAndDisconnected(cs.isJsonAndDisconnected())
                                                        .build())
                              .collect(Collectors.toList());
            }
        }
    }

    private interface ZeroMoreRecentStrategy {
        List<ConnectorStatus> process(List<ConnectorStatus> zero, List<ConnectorStatus> nonZero);
    }

}
